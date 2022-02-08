package com.leqiwl.novel.job.receive;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.leqiwl.novel.config.sysconst.TopicAndQueueKeyConst;
import com.leqiwl.novel.domain.dto.NovelIdTopicDto;
import com.leqiwl.novel.domain.entify.Novel;
import com.leqiwl.novel.domain.entify.NovelConver;
import com.leqiwl.novel.service.NovelConverService;
import com.leqiwl.novel.service.NovelService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author: 飞鸟不过江
 * @Date: 2022/1/10 10:42
 * @Description:
 */
@Slf4j
@Component
@Order(200)
public class ClickQueueMessageReceive implements ApplicationRunner {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private NovelConverService novelConverService;

    @Resource
    private NovelService novelService;

    @Async("birdsExecutor")
    @Override
    public void run(ApplicationArguments args) throws Exception {
        while (true){
            RLock lock = null;
            try {
                RBlockingQueue<NovelIdTopicDto> blockingQueue =
                        redissonClient.getBlockingQueue(TopicAndQueueKeyConst.CLICK_QUEUE);
                NovelIdTopicDto novelIdTopicDto = blockingQueue.take();
                //点击
                log.info("线程：{},收到点击消息：{}",Thread.currentThread().getName(),novelIdTopicDto);
                Date date = new Date();
                String novelId = novelIdTopicDto.getNovelId();
                if(StrUtil.isBlank(novelId)){
                    continue;
                }
                lock = redissonClient.getLock(novelId);
                lock.tryLock(3,6,TimeUnit.SECONDS);
                NovelConver novelConver = novelConverService.getByNovelId(novelId);
                if(null != novelConver){
                    //更新数据
                    novelConver.setClickNum(novelConver.getClickNum() + 1);
                    novelConver.setUpdateTime(date);
                    novelConverService.save(novelConver);
                    continue;
                }
                Novel novel = novelService.getByNovelId(novelId);
                if(null == novel || StrUtil.isBlank(novel.getNovelId())){
                    continue;
                }
                novelConver = new NovelConver();

                BeanUtil.copyProperties(novel,novelConver);
                novelConver.setClickNum(1L);
                novelConver.setCreateTime(date);
                novelConver.setUpdateTime(date);
                novelConverService.save(novelConver);
            } catch (InterruptedException e) {
                log.info(e.getMessage(),e);
            }finally {
                if(null != lock){
                    lock.unlock();
                }
            }
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                log.info(e.getMessage(),e);
            }
        }

    }
}

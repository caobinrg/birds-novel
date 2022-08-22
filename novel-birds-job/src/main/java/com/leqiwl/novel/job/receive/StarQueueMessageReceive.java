package com.leqiwl.novel.job.receive;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.leqiwl.novel.config.sysconst.TopicAndQueueKeyConst;
import com.leqiwl.novel.domain.dto.NovelIdTopicDto;
import com.leqiwl.novel.domain.entify.Novel;
import com.leqiwl.novel.domain.entify.NovelConver;
import com.leqiwl.novel.service.NovelConverService;
import com.leqiwl.novel.service.NovelService;
import com.leqiwl.novel.util.Spinlock;
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
@Order(202)
public class StarQueueMessageReceive implements ApplicationRunner {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private NovelConverService novelConverService;

    @Async("birdsExecutor")
    @Override
    public void run(ApplicationArguments args) throws Exception {
        while (!Thread.currentThread().isInterrupted()){
            try {
                RBlockingQueue<NovelIdTopicDto> blockingQueue =
                        redissonClient.getBlockingQueue(TopicAndQueueKeyConst.STAR_QUEUE);
                NovelIdTopicDto novelIdTopicDto = blockingQueue.take();
                log.info("线程：{},收到收藏消息：{}",Thread.currentThread().getName(),novelIdTopicDto);
                Date date = new Date();
                String novelId = novelIdTopicDto.getNovelId();
                Spinlock<Object> spinlock = new Spinlock<>(redissonClient, novelId);
                spinlock.process(null,o -> {
                    NovelConver novelConver = novelConverService.getByNovelId(novelId);
                    if(null != novelConver){
                        //更新数据
                        novelConver.setStarNum(novelConver.getStarNum() + 1);
                        novelConver.setUpdateTime(date);
                        novelConverService.save(novelConver);
                        return;
                    }
                    novelConver = novelConverService.generateConver(novelId);
                    novelConver.setStarNum(1L);
                    novelConverService.save(novelConver);
                });
            } catch (InterruptedException e) {
                log.info(e.getMessage(),e);
                throw e;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                log.info(e.getMessage(),e);
                throw e;
            }
        }
    }
}

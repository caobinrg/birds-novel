package com.leqiwl.novel.job.receive;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.leqiwl.novel.config.sysconst.TopicAndQueueKeyConst;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author: 飞鸟不过江
 * @Date: 2022/1/10 10:42
 * @Description: 百度推送
 */
@Slf4j
@Component
@Order(203)
public class UrlSaveQueueMessageReceive implements ApplicationRunner {

    @Resource
    private RedissonClient redissonClient;

    @Value("${baiduPush.pushUrl}")
    private String pushUrl;


    @Async("birdsExecutor")
    @Override
    public void run(ApplicationArguments args) throws Exception {
        while (true){
            try {
                RBlockingQueue<String> blockingQueue =
                        redissonClient.getBlockingQueue(TopicAndQueueKeyConst.ULR_SAVE_QUEUE);
                String url = blockingQueue.take();
                log.info("线程：{},收到url保存消息：{}", Thread.currentThread().getName(),url);
                if(StrUtil.isBlank(url)){
                    continue;
                }
                String body = HttpRequest.post(pushUrl)
                        .header(Header.CONTENT_TYPE, ContentType.TEXT_PLAIN.getValue())
                        .body(url)
                        .execute().body();
                log.info("百度推送url:{},结果：{}",url,body);
            } catch (InterruptedException e) {
                log.info(e.getMessage(),e);
            }
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                log.info(e.getMessage(),e);
            }
        }
    }
}

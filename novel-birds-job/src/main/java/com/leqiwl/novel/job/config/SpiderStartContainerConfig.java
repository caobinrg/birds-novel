package com.leqiwl.novel.job.config;

import com.leqiwl.novel.job.pip.SpiderStartContainer;
import com.leqiwl.novel.job.pip.downloader.SpiderDownloader;
import com.leqiwl.novel.job.pip.listener.SpiderEventListener;
import com.leqiwl.novel.job.pip.pipeline.SpiderSavePipLine;
import com.leqiwl.novel.job.pip.processor.SpiderProcessor;
import com.leqiwl.novel.job.pip.scheduler.SpiderRedisScheduler;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * @author: 飞鸟不过江
 * @Date: 2021/12/27 15:02
 * @Description: SpiderStartContainer注入
 */
@Slf4j
@Configuration
public class SpiderStartContainerConfig {

    @Value("${spider.threadNum:0}")
    private int threadNum;

    @Resource
    private SpiderProcessor spiderProcessor;

    @Resource
    private SpiderEventListener spiderEventListener;

    @Resource
    private SpiderDownloader spiderDownloader;

    @Resource
    private SpiderRedisScheduler spiderRedisScheduler;

    @Resource
    private SpiderSavePipLine spiderSavePipLine;

    @Resource
    private RedissonClient redissonClient;

    @Bean
    public SpiderStartContainer getStartContainer() {
        SpiderStartContainer spiderStartContainer =
                SpiderStartContainer.create(spiderProcessor, spiderEventListener,redissonClient);
        spiderStartContainer.setDownloader(spiderDownloader);
        spiderStartContainer.setScheduler(spiderRedisScheduler);
        spiderStartContainer.setPipelines(Collections.singletonList(spiderSavePipLine));
        //线程数 cpu * 2
        if(threadNum == 0 ){
            threadNum = Runtime.getRuntime().availableProcessors() * 2;
        }
        spiderStartContainer.thread(threadNum);
        log.info("================ spider thread num :{} ================",threadNum);
        return spiderStartContainer;
    }



}

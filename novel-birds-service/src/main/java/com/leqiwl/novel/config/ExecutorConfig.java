package com.leqiwl.novel.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: 飞鸟不过江
 * @Date: 2020/7/3 09:33
 * @Description: 异步线程连接池
 */
@Configuration
@EnableAsync
@Slf4j
public class ExecutorConfig {

    /**
     * 定义策略处理线程池
     * @return
     */
    @Bean("birdsExecutor")
    public Executor exportServiceExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 核心线程数量：当前机器的核心数
        executor.setCorePoolSize(
                Runtime.getRuntime().availableProcessors());

        // 最大线程数
        executor.setMaxPoolSize(
                Runtime.getRuntime().availableProcessors() * 2);

        // 队列大小
        executor.setQueueCapacity(Integer.MAX_VALUE);

        // 线程池中的线程名前缀
        executor.setThreadNamePrefix("birdExecutor-");

        // 拒绝策略：直接拒绝
        executor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.AbortPolicy());

        // 执行初始化
        executor.initialize();

        return executor;
    }

}

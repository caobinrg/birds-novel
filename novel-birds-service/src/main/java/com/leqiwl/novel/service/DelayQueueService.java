package com.leqiwl.novel.service;

import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2022/1/1 0001 23:50
 */
@Service
public class DelayQueueService<T> {

    @Resource
    private RedissonClient redissonClient;

    public void pushData(T data, String queueName){
        pushData(data, 0, TimeUnit.MILLISECONDS, queueName);
    }

    public void pushData(T data,long time, String queueName){
        pushData(data, time, TimeUnit.MINUTES, queueName);
    }

    public void pushData(T data, long time, TimeUnit timeUnit, String queueName){
        getDelayQueue(queueName).offerAsync(data, time < 0 ? 0 : time, timeUnit);
    }


    public T pullData(String queueName) throws InterruptedException {
        return getBlockingQueue(queueName).take();
    }


    private RBlockingQueue<T> getBlockingQueue(String queueName) {
        return redissonClient.getBlockingQueue(queueName);
    }


    private RDelayedQueue<T> getDelayQueue(String queueName) {
        return redissonClient.getDelayedQueue(getBlockingQueue(queueName));
    }

    private RDelayedQueue<T> getDelayQueue(RBlockingQueue<T> blockingQueue) {
        return redissonClient.getDelayedQueue(blockingQueue);
    }

}

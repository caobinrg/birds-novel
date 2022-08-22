package com.leqiwl.novel.util;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * @author: CaoBin
 * @Date: 2022/2/18 15:30
 * @Description: 分布式自旋锁
 */
@Slf4j
public class Spinlock<T> {

    private RedissonClient redissonClient;

    private String lockKey;

    public Spinlock(RedissonClient redissonClient, String lockKey) {
        this.redissonClient = redissonClient;
        this.lockKey = lockKey;
    }

    public void process(T t, LockFunctionInterface<T> lockFunctionInterface)
            throws InterruptedException {
        if(StrUtil.isBlank(lockKey)){
            return;
        }
        RLock lock = null;
        try{
            lock = redissonClient.getLock(lockKey);
            boolean tryLock = false;
            //重试次数
            int retryTime = 0;
            int maxRetryTime = 5;
            while (!tryLock && retryTime < maxRetryTime){
                retryTime ++;
                tryLock = lock.tryLock(3, 6, TimeUnit.SECONDS);
                if(!tryLock){
                    TimeUnit.MILLISECONDS.sleep(500);
                    continue;
                }
                lockFunctionInterface.process(t);
            }
        } catch (InterruptedException e) {
            log.info(e.getMessage(),e);
            throw e;
        } finally {
            if(null != lock && lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }
}

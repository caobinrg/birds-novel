package com.leqiwl.novel;

import com.leqiwl.novel.config.sysconst.DelayQueueConst;
import com.leqiwl.novel.service.DelayQueueService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2022/1/1 0001 23:42
 */
@Slf4j
@SpringBootTest(classes = ServiceTestApplication.class)
@RunWith(SpringRunner.class)
public class DelayedTest {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private DelayQueueService delayQueueService;

    private RAtomicLong leftTotal;

    @Test
    public void testOffer(){
        delayQueueService.pushData("测试延迟队列1",1,"delay");
    }

    @Test
    public void testOffer1(){
        delayQueueService.pushData(new Request("http://www.baidu.com"),5,TimeUnit.MILLISECONDS, DelayQueueConst.CRAWLER_LIST_QUEUE);
    }

    @Test
    public void testTake() throws InterruptedException {
        String s = (String) delayQueueService.pullData("delay");
        System.out.println(s);
    }

    @Test
    public void testAddBlockQ(){
        RBlockingQueue<Object> test = redissonClient.getBlockingQueue("test");
        RDelayedQueue<Object> delayedQueue = redissonClient.getDelayedQueue(test);
        delayedQueue.offerAsync("test",2, TimeUnit.SECONDS);
    }

    @Test
    public void testPollBlockQ() throws Exception {
        RBlockingQueue<Object> test = redissonClient.getBlockingQueue("test");
        Object take = test.take();
        System.out.println(take.toString());
        int g = 1/0;
    }

    @Test
    public void TestLong(){
        System.out.println(leftTotal.get());
    }

    @PostConstruct
    public void post(){
        leftTotal = redissonClient.getAtomicLong("atomicLong");
    }


}

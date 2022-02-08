//package com.leqiwl.novel;
//
//import com.leqiwl.novel.remote.SpiderContainerRemote;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.redisson.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
///**
// * @author 飞鸟不过江
// * @description:
// * @date 2022/1/1 0001 2:37
// */
//@Slf4j
//@SpringBootTest(classes = ServiceTestApplication.class)
//@RunWith(SpringRunner.class)
//public class ServiceTest {
//
//    @Autowired
//    private RedissonClient redissonClient;
//
//    @Test
//    public void testSpiderStop(){
//        RRemoteService remoteService = redissonClient.getRemoteService();
//        SpiderContainerRemote spiderContainer = remoteService.get(SpiderContainerRemote.class);
//        spiderContainer.spiderStop(null);
//    }
//
//    @Test
//    public void testSpiderStart(){
//        RRemoteService remoteService = redissonClient.getRemoteService();
//        SpiderContainerRemote spiderContainer = remoteService.get(SpiderContainerRemote.class);
//        spiderContainer.spiderStart();
//    }
//
//    @Test
//    public void testSpiderStatus(){
//        RRemoteService remoteService = redissonClient.getRemoteService();
//        SpiderContainerRemote spiderContainer = remoteService.get(SpiderContainerRemote.class);
//        Integer spiderStatus = spiderContainer.getSpiderStatus();
//        System.out.println(spiderStatus);
//    }
//
//    @Test
//    public void testSpiderClose(){
//        RRemoteService remoteService = redissonClient.getRemoteService();
//        SpiderContainerRemote spiderContainer = remoteService.get(SpiderContainerRemote.class);
//         spiderContainer.spiderClose(null);
//    }
//
//    @Test
//    public void testCountDownLatch() throws InterruptedException {
//        RRemoteService remoteService = redissonClient.getRemoteService();
//        SpiderContainerRemote spiderContainer = remoteService.get(SpiderContainerRemote.class,
//                RemoteInvocationOptions.defaults().noAck().noResult());
//        spiderContainer.spiderStart(null);
//        RCountDownLatch latch = redissonClient.getCountDownLatch("test");
//        latch.trySetCount(1);
//        latch.await();
//        System.out.println("解锁=========================");
//    }
//
//}

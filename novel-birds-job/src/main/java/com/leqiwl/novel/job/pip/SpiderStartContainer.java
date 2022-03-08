package com.leqiwl.novel.job.pip;

import cn.hutool.core.util.StrUtil;
import com.leqiwl.novel.config.sysconst.RequestConst;
import com.leqiwl.novel.domain.dto.CrawlerRequestDto;
import com.leqiwl.novel.job.pip.listener.SpiderEventListener;
import com.leqiwl.novel.job.pip.scheduler.SpiderRedisScheduler;
import org.apache.commons.collections.CollectionUtils;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RedissonClient;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加监听器
 * @author 飞鸟不过江
 */
public class SpiderStartContainer extends Spider {

    private SpiderEventListener listener;

    private RedissonClient redissonClient;

    private int status = 0;

    public SpiderStartContainer(PageProcessor pageProcessor, SpiderEventListener listener, RedissonClient redissonClient) {
        super(pageProcessor);
        this.redissonClient = redissonClient;

        List<SpiderListener> spiderListeners = this.getSpiderListeners();
        if(CollectionUtils.isEmpty(spiderListeners)) {
            spiderListeners = new ArrayList<>();
        }
        this.listener = listener;
        spiderListeners.add(listener);
        this.setSpiderListeners(spiderListeners);
    }

    public static SpiderStartContainer create(PageProcessor pageProcessor,SpiderEventListener listener,RedissonClient redissonClient) {
        return new SpiderStartContainer(pageProcessor,listener, redissonClient);
    }

//    @Override
//    public void runAsync() {
//        if(null != this.executorService){
//            this.executorService.execute(this);
//        }else{
//            Thread thread = new Thread(this);
//            thread.setDaemon(false);
//            thread.start();
//        }
//    }


    @Override
    public void run() {
        super.run();
        //TODO 爬虫任务完成时处理爬取失败的请求
        List<Request> failRequests = listener.getFailRequests();
        if(CollectionUtils.isNotEmpty(failRequests)) {

        }
    }

    @Override
    public void close() {
        super.close();
        this.status = 3;
    }

    public void spiderResetDuplicateCheck(){
        if(scheduler != null && scheduler instanceof DuplicateRemover && this.stat.intValue() != 1) {
            ((DuplicateRemover) scheduler).resetDuplicateCheck(this);
        }
    }
    public void spiderResetDuplicateCheckByDomain(String domain){
        if(scheduler != null && scheduler instanceof DuplicateRemover && this.stat.intValue() != 1) {
            ((SpiderRedisScheduler) scheduler).resetDuplicateCheck(domain);
        }
    }


    public void spiderClose(String countDownSpace) {
        this.close();
        spiderCountDown(countDownSpace);
    }

    public void spiderStop(String countDownSpace){
        this.stop();
        this.status = this.stat.intValue();
        spiderCountDown(countDownSpace);
    }

    public void spiderStart(){
        spiderStart(null);
    }


    public void spiderStart(String countDownSpace){
        logger.info("===================== check spider:{} status:{} ====================="
                ,this.getUUID(),this.getSpiderStatus());
        if(this.getSpiderStatus() == 3){
            logger.info("===================== spider:{} is close! status:{} spider will be restart! ====================="
                    ,this.getUUID(),this.getSpiderStatus());
            this.spiderStop(null);
        }
        while (this.getSpiderStatus() == 3){
            logger.info("===================== spider:{}，status:{} is to stop ====================="
                    ,this.getUUID(),this.getSpiderStatus());
        }
        if(this.stat.intValue() == 2){
//            this.stat.set(1);
            this.stat.compareAndSet(2, 1);
        }
        if(this.stat.intValue() == 0){
            this.start();
        }
        this.status = this.stat.intValue();
        logger.info("===================== spider:{},status:{} is start ====================="
                ,this.getUUID(),this.getSpiderStatus());
        spiderCountDown(countDownSpace);
    }



    public void spiderJumpQueue(Request request){
        logger.info("收到插队采集请求,url:{}",request.getUrl());
        CrawlerRequestDto requestInfo = request.getExtra(RequestConst.REQUEST_INFO);
        if(null == requestInfo){
            return;
        }
        requestInfo.setJump(true);
        this.addRequest(request);
        spiderStart();
    }

    public String getSpiderUUID() {
        return this.getUUID();
    }

    public Integer getSpiderStatus(){
        if(status == 3){
            return status;
        }
        return stat.get();
    }

    public int getLeftRequestsCount(String domain){
        SpiderRedisScheduler scheduler = (SpiderRedisScheduler)this.getScheduler();
        if(null != scheduler){
            return scheduler.getLeftRequestsCount(domain);
        }
        return 0;
    }

    public int getTotalRequestsCount(String domain){
        SpiderRedisScheduler scheduler = (SpiderRedisScheduler)this.getScheduler();
        if(null != scheduler){
            return scheduler.getTotalRequestsCountByDomain(domain);
        }
        return 0;
    }

    private void spiderCountDown(String countDownSpace){
        if(null == redissonClient){
            return;
        }
        if(StrUtil.isNotBlank(countDownSpace)){
            RCountDownLatch latch = redissonClient.getCountDownLatch(countDownSpace);
            latch.countDown();
        }
    }

}

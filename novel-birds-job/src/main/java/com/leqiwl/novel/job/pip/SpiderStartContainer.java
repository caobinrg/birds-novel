package com.leqiwl.novel.job.pip;

import cn.hutool.core.util.StrUtil;
import com.leqiwl.novel.job.pip.listener.SpiderEventListener;
import com.leqiwl.novel.job.pip.scheduler.SpiderRedisScheduler;
import com.leqiwl.novel.remote.SpiderContainerRemote;
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
public class SpiderStartContainer extends Spider implements SpiderContainerRemote {

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

    @Override
    public void run() {
        super.run();
        if(scheduler != null && scheduler instanceof DuplicateRemover) {
            ((DuplicateRemover) scheduler).resetDuplicateCheck(this);
        }
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

    @Override
    public void spiderClose(String countDownSpace) {
        this.close();
        spiderCountDown(countDownSpace);
    }

    @Override
    public void spiderStop(String countDownSpace){
        this.stop();
        this.status = this.stat.intValue();
        spiderCountDown(countDownSpace);
    }

    @Override
    public void spiderStart(){
        spiderStart(null);
    }


    @Override
    public void spiderStart(String countDownSpace){
        logger.info("===================== check spider status =====================");
        if(this.getSpiderStatus() == 3){
            logger.info("===================== spider is close! spider will be restart! =====================");
            this.spiderStop(null);
        }
        while (this.getSpiderStatus() == 3){
            logger.info("===================== spider is to stop =====================");
        }
        if(this.stat.intValue() == 2){
            this.start();

            this.status = this.stat.intValue();
        }
        logger.info("===================== spider is start =====================");
        spiderCountDown(countDownSpace);
    }


    @Override
    public void spiderJumpQueue(Request request){
        spiderJumpQueue(request,null);
    }

    @Override
    public void spiderJumpQueue(Request request, String domain){
        SpiderRedisScheduler scheduler = (SpiderRedisScheduler)this.getScheduler();
        if(null != scheduler){
            scheduler.jumpQueue(request,domain);
        }
        spiderStart();
    }

    @Override
    public String getSpiderUUID() {
        return this.getUUID();
    }

    @Override
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
            return scheduler.getTotalRequestsCount(domain);
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

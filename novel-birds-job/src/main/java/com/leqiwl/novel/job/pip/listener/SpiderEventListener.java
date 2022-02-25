package com.leqiwl.novel.job.pip.listener;

import cn.hutool.core.util.StrUtil;
import com.leqiwl.novel.config.sysconst.RedisKeyConst;
import com.leqiwl.novel.config.sysconst.RequestConst;
import com.leqiwl.novel.domain.dto.CrawlerRequestDto;
import com.leqiwl.novel.domain.entify.crawler.CrawlerRule;
import com.leqiwl.novel.enums.CrawlerTypeEnum;
import com.leqiwl.novel.service.CrawlerRuleService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.utils.UrlUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义监听器，统计处理request的成功数和失败数，同时将处理失败的request收集起来
 * @author 飞鸟不过江
 */
@Component
public class SpiderEventListener implements SpiderListener {
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger failCount = new AtomicInteger(0);

    private List<Request> failRequests = new CopyOnWriteArrayList<>();

    /**
     * 用于存放失败的request
     */
    private static final String FAIL_PREFIX = "fail_request";


    @Resource
    private RedissonClient redissonClient;

    @Resource
    private CrawlerRuleService crawlerRuleService;

    @Value("${spider.retryTimes:5}")
    private int retryTimes;


    public RMap<String,Request> getFailMap(){
        return redissonClient.getMap(getFailMapKey());
    }

    @Override
    public void onSuccess(Request request) {
        successCount.incrementAndGet();
        CrawlerRequestDto requestInfo = request.getExtra(RequestConst.REQUEST_INFO);
        if(null == requestInfo){
            return;
        }
        String countDownSpace = requestInfo.getCountDownSpace();
        if(StrUtil.isNotBlank(countDownSpace)){
            if(null == redissonClient){
                return;
            }
            RCountDownLatch latch = redissonClient.getCountDownLatch(countDownSpace);
            latch.countDown();
        }
        String url = request.getUrl();
        RMap<String, RetryRequest> retryMap = redissonClient.getMap(getFailMapKey());
        retryMap.remove(url);
    }

    @Override
    public void onError(Request request) {
        failRequests.add(request);
        failCount.incrementAndGet();
        String url = request.getUrl();
        CrawlerRequestDto requestInfo = request.getExtra(RequestConst.REQUEST_INFO);
        if(null == requestInfo){
            return;
        }
        Integer type = requestInfo.getType();
        if(CrawlerTypeEnum.LIST.getType().equals(type)){
            RMap<String, RetryRequest> retryMap = redissonClient.getMap(getFailMapKey());
            RetryRequest retryRequest = retryMap.get(url);
            if(null == retryRequest){
                retryRequest = new RetryRequest(0,request);
            }else{
                int retryTime = retryRequest.getRetryTime() + 1;
                if(retryTime > retryTimes){
                    retryMap.remove(url);
                    return;
                }
                retryRequest.setRetryTime(retryTime);
            }
            retryMap.put(url,retryRequest);
        }
    }

    private String getFailMapKey(){

        return  RedisKeyConst.spiderKeySpace + FAIL_PREFIX;
    }

    public AtomicInteger getSuccessCount() {
        return successCount;
    }

    public AtomicInteger getFailCount() {
        return failCount;
    }

    public List<Request> getFailRequests() {
        return failRequests;
    }


    /**
     * 根据规则id获取规则信息
     * @param ruleId
     * @return
     */
    private CrawlerRule getCrawlerInfo(String ruleId){
        if(null == ruleId){
            return null;
        }
        CrawlerRule crawlerRule = crawlerRuleService.getByRuleId(ruleId);
        if(StrUtil.isBlank(crawlerRule.getId())){
            return null;
        }
        return crawlerRule;
    }

    static class RetryRequest implements Serializable {

        public RetryRequest(int retryTime, Request request) {
            this.retryTime = retryTime;
            this.request = request;
        }

        private int retryTime;

        private Date createTime;

        private Request request;

        public int getRetryTime() {
            return retryTime;
        }

        public void setRetryTime(int retryTime) {
            this.retryTime = retryTime;
        }

        public Request getRequest() {
            return request;
        }

        public void setRequest(Request request) {
            this.request = request;
        }
    }
}

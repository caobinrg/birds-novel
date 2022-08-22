package com.leqiwl.novel.job.pip.downloader;

import cn.hutool.core.util.StrUtil;
import com.leqiwl.novel.config.sysconst.RedisKeyConst;
import com.leqiwl.novel.config.sysconst.RequestConst;
import com.leqiwl.novel.domain.dto.CrawlerRequestDto;
import com.leqiwl.novel.domain.entify.crawler.CrawlerRule;
import com.leqiwl.novel.enums.CrawlerTypeEnum;
import com.leqiwl.novel.service.CrawlerRuleService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Date;

/**
 * 自定义下载器，将下载失败的url记录到redis中
 * @author 飞鸟不过江
 */
@Slf4j
@Component
public class SpiderDownloader extends HttpClientDownloader {

    private static final String DOWNLOAD_START_MILLS = "download_start_mills";
    private static final String DOWNLOAD_EXPAND_MILLS = "download_expand_mills";


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


    public RMap<String,RetryRequest> getFailMap(){
        return redissonClient.getMap(getFailMapKey());
    }

    @Override
    public Page download(Request request, Task task) {
        request.putExtra(DOWNLOAD_START_MILLS, System.currentTimeMillis());
        return super.download(request, task);
    }

    @Override
    protected void onSuccess(Request request) {
        super.onSuccess(request);

        calcExpandMills(request);
        log.info("download expand: {} ms, url: {}", request.getExtra(DOWNLOAD_EXPAND_MILLS), request.getUrl());
        String url = request.getUrl();
        RMap<String, RetryRequest> retryMap = redissonClient.getMap(getFailMapKey());
        retryMap.remove(url);
    }

    @Override
    protected void onError(Request request) {
        super.onError(request);

        calcExpandMills(request);
        log.info("download error!!! expand: {} ms, url: {}", request.getExtra(DOWNLOAD_EXPAND_MILLS), request.getUrl());

        // 将下载失败的url记录到redis
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



    private String getFailMapKey(){
        return  RedisKeyConst.spiderKeySpace + FAIL_PREFIX;
    }


    /**
     * 计算下载耗费毫秒数
     * @param request
     */
    private void calcExpandMills(Request request) {
        long downloadEndMills = System.currentTimeMillis();
        Object downloadStartMills = request.getExtra(DOWNLOAD_START_MILLS);
        if(downloadStartMills != null) {
            long expandMills = downloadEndMills - Long.parseLong(downloadStartMills.toString());
            request.putExtra(DOWNLOAD_EXPAND_MILLS, expandMills);
        }
    }


    public static class RetryRequest implements Serializable {

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

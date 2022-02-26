package com.leqiwl.novel.job.job;

import com.leqiwl.novel.config.sysconst.RequestConst;
import com.leqiwl.novel.domain.dto.CrawlerRequestDto;
import com.leqiwl.novel.domain.entify.crawler.CrawlerRule;
import com.leqiwl.novel.job.pip.SpiderStartContainer;
import com.leqiwl.novel.job.pip.SpiderStartContainerFactory;
import com.leqiwl.novel.job.pip.downloader.SpiderDownloader;
import com.leqiwl.novel.job.pip.listener.SpiderEventListener;
import com.leqiwl.novel.service.CrawlerRuleService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Set;

/**
 * @author: 飞鸟不过江
 * @Date: 2022/2/25 15:16
 * @Description:
 */
@Slf4j
@Component
public class SpiderListFailJob {

    @Resource
    private SpiderDownloader spiderDownloader;

    @Autowired
    private SpiderStartContainerFactory spiderStartContainerFactory;

    @Resource
    private CrawlerRuleService crawlerRuleService;

    /**
     * 15分钟执行一次
     */
    @Scheduled(cron = "0 0/15 * * * ? ")
    private void configureTasks() {
        log.info("fail scheduled start");
        RMap<String, SpiderDownloader.RetryRequest> failMap = spiderDownloader.getFailMap();
        if(null == failMap || failMap.size() < 1){
            return;
        }
        Set<String> keySet = failMap.keySet();
        ArrayList<String> keys = new ArrayList<>(keySet);
        for (String key : keys) {
            SpiderDownloader.RetryRequest retryRequest = failMap.get(key);
            if(null == retryRequest){
                continue;
            }
            Request request = retryRequest.getRequest();
            if(null == request){
                continue;
            }
            CrawlerRequestDto requestInfo = request.getExtra(RequestConst.REQUEST_INFO);
            String ruleId = requestInfo.getRuleId();
            CrawlerRule crawlerRule = crawlerRuleService.getByRuleId(ruleId);
            int openStatus = crawlerRule.getOpenStatus();
            if(0 == openStatus){
                failMap.remove(key);
                continue;
            }
            SpiderStartContainer startContainer = spiderStartContainerFactory.getStartContainer(request);
            startContainer.addRequest(request);
            Integer spiderStatus = startContainer.getSpiderStatus();
            if(1 != spiderStatus){
                startContainer.spiderStart();
            }
        }
    }


}

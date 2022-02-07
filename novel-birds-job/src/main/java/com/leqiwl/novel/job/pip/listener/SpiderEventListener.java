package com.leqiwl.novel.job.pip.listener;

import cn.hutool.core.util.StrUtil;
import com.leqiwl.novel.config.sysconst.RequestConst;
import com.leqiwl.novel.domain.dto.CrawlerRequestDto;
import com.leqiwl.novel.domain.entify.crawler.CrawlerRule;
import com.leqiwl.novel.service.CrawlerRuleService;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.SpiderListener;

import javax.annotation.Resource;
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

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private CrawlerRuleService crawlerRuleService;

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
    }

    @Override
    public void onError(Request request) {
        failRequests.add(request);
        failCount.incrementAndGet();
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
}

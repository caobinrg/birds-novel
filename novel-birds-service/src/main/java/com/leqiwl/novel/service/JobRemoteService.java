package com.leqiwl.novel.service;

import cn.hutool.core.util.URLUtil;
import com.leqiwl.novel.common.util.UrlParseUtil;
import com.leqiwl.novel.config.sysconst.RequestConst;
import com.leqiwl.novel.domain.dto.CrawlerRequestDto;
import com.leqiwl.novel.domain.entify.Chapter;
import com.leqiwl.novel.domain.entify.crawler.CrawlerListRule;
import com.leqiwl.novel.domain.entify.crawler.CrawlerRule;
import com.leqiwl.novel.enums.CrawlerTypeEnum;
import com.leqiwl.novel.remote.SpiderContainerRemote;
import com.leqiwl.novel.remote.SpiderJobStartRemote;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RRemoteService;
import org.redisson.api.RedissonClient;
import org.redisson.api.RemoteInvocationOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Request;

import java.util.concurrent.TimeUnit;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2022/2/5 0005 12:32
 */
@Slf4j
@Service
public class JobRemoteService {

    @Autowired
    private RedissonClient redissonClient;


    public boolean startRule(CrawlerRule crawlerRule){
        CrawlerListRule listRule = crawlerRule.getListRule();
        String sourceUrl = listRule.getSourceUrl();
        sourceUrl = sourceUrl.replace(RequestConst.PAGE_REPLACE,listRule.getPageStartRule()+"");
        Request request = new Request(sourceUrl);
        CrawlerRequestDto requestInfo = CrawlerRequestDto.builder()
                .url(sourceUrl)
                .ruleId(crawlerRule.getRuleId())
                .type(CrawlerTypeEnum.LIST.getType())
                .build();
        request.putExtra(RequestConst.REQUEST_INFO,requestInfo);
        RRemoteService remoteService = redissonClient.getRemoteService();
        SpiderJobStartRemote spiderJobStartRemote = remoteService.get(SpiderJobStartRemote.class);
        if( spiderJobStartRemote.getStarStatus() == 0){
            spiderJobStartRemote.start();
            return true;
        }
        SpiderContainerRemote spiderContainer = remoteService.get(SpiderContainerRemote.class);
        spiderContainer.spiderJumpQueue(request);
        return true;
    }


    public boolean jumpGetContent(Chapter chapter){
        try {
            if(null == chapter){
                return false;
            }
            String pageUrl = chapter.getPageUrl();
            String chapterUrl =  UrlParseUtil.urlReduction(pageUrl,chapter.getChapterUrl());
            String ruleId = chapter.getRuleId();
            String chapterId = chapter.getChapterId();
            Request request = new Request(chapterUrl);
            CrawlerRequestDto requestInfo = CrawlerRequestDto.builder()
                    .url(chapterUrl)
                    .ruleId(ruleId)
                    .baseUrl(URLUtil.url(chapterUrl).getHost())
                    .novelId(chapter.getNovelId())
                    .chapterId(chapterId)
                    .novelName(chapter.getNovelName())
                    .countDownSpace(chapterId)
                    .type(CrawlerTypeEnum.CONTENT.getType())
                    .build();
            request.putExtra(RequestConst.REQUEST_INFO,requestInfo);
            RRemoteService remoteService = redissonClient.getRemoteService();
            SpiderContainerRemote spiderContainer = remoteService.get(SpiderContainerRemote.class,
                    RemoteInvocationOptions.defaults().noAck().noResult());
            spiderContainer.spiderJumpQueue(request);
            RCountDownLatch latch = redissonClient.getCountDownLatch(chapterId);
            latch.trySetCount(1);
            //3秒后自动解锁
            latch.await(3, TimeUnit.SECONDS);
            return true;
        }catch (Exception e){
            log.warn(e.getMessage(),e);
        }
        return false;
    }



}

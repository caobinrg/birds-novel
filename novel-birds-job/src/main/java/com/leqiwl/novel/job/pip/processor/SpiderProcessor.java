package com.leqiwl.novel.job.pip.processor;

import cn.hutool.core.util.StrUtil;
import com.leqiwl.novel.domain.dto.CrawlerRequestDto;
import com.leqiwl.novel.config.sysconst.RequestConst;
import com.leqiwl.novel.domain.entify.crawler.CrawlerRule;
import com.leqiwl.novel.job.config.SpiderConfig;
import com.leqiwl.novel.service.CrawlerRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import javax.annotation.Resource;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2021/12/26 0026 20:05
 */
@Slf4j
@Component
public class SpiderProcessor implements PageProcessor {

    @Resource
    private NovelProcessorFactory novelProcessorFactory;

    @Resource
    private CrawlerRuleService crawlerRuleService;

    @Resource
    private SpiderConfig spiderConfig;



    @Override
    public void process(Page page) {
        CrawlerRequestDto requestInfo =
                page.getRequest().getExtra(RequestConst.REQUEST_INFO);
        int type = requestInfo.getType();
        CrawlerRule crawlerInfo = getCrawlerInfo(requestInfo.getRuleId());
        if(null == crawlerInfo){
            return;
        }
        int openStatus = crawlerInfo.getOpenStatus();
        if(openStatus == 0){
            //规则已被关闭
            log.info("规则：{} 被关闭",crawlerInfo.getRuleName());
            return;
        }
        NovelProcessor novelProcessor = novelProcessorFactory.getProcessor(type);
        if(null == novelProcessor){
            return;
        }
        novelProcessor.process(page,requestInfo,crawlerInfo);
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


    @Override
    public Site getSite() {
        Site site = Site.me().setRetryTimes(spiderConfig.getRetryTimes())
                .setSleepTime(spiderConfig.getSleepTime())
                .setTimeOut(spiderConfig.getTimeOut());
        site.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        site.addHeader("Accept-Encoding", "gzip, deflate");
        site.addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
        site.addHeader("Cache-Control", "max-age=0");
        site.addHeader("Connection", "keep-alive");
//        site.addHeader("Cookie", "Hm_lvt_42e120beff2c918501a12c0d39a4e067=1566530194,1566819135,1566819342,1566963215; Hm_lpvt_42e120beff2c918501a12c0d39a4e067=1566963215");
//        site.addHeader("Host", "www.yousuu.com");
        site.addHeader("Upgrade-Insecure-Requests", "1");
        site.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36");

        return site;
    }
}

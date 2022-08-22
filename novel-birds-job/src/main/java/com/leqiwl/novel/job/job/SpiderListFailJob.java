package com.leqiwl.novel.job.job;

import cn.hutool.core.collection.CollectionUtil;
import com.leqiwl.novel.config.sysconst.RequestConst;
import com.leqiwl.novel.domain.dto.CrawlerRequestDto;
import com.leqiwl.novel.domain.entify.crawler.CrawlerListRule;
import com.leqiwl.novel.domain.entify.crawler.CrawlerRule;
import com.leqiwl.novel.enums.CrawlerTypeEnum;
import com.leqiwl.novel.job.pip.SpiderStartContainer;
import com.leqiwl.novel.job.pip.SpiderStartContainerFactory;
import com.leqiwl.novel.job.pip.downloader.SpiderDownloader;
import com.leqiwl.novel.service.CrawlerRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static us.codecraft.webmagic.utils.UrlUtils.getDomain;

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
        List<CrawlerRule> allRules = crawlerRuleService.getAll();
        List<CrawlerRule> collect = allRules.stream()
                .filter(rule -> rule.getOpenStatus() == 1 && rule.getInitStatus() == 1)
                .collect(Collectors.toList());
        if(CollectionUtil.isEmpty(collect)){
            return;
        }
        for (CrawlerRule crawlerRule : collect) {
            CrawlerListRule listRule = crawlerRule.getListRule();
            String sourceUrl = listRule.getSourceUrl();
            int pageStartRule = listRule.getPageStartRule();
            String url = sourceUrl.replace(RequestConst.PAGE_REPLACE,pageStartRule+"");
            Request request = new Request(url);
            CrawlerRequestDto requestInfo = CrawlerRequestDto.builder()
                    .url(url)
                    .ruleId(crawlerRule.getRuleId())
//                    .baseUrl("")
                    .type(CrawlerTypeEnum.LIST.getType())
                    .build();
            request.putExtra(RequestConst.REQUEST_INFO,requestInfo);
            SpiderStartContainer spiderStartContainer = spiderStartContainerFactory.getStartContainer(getDomain(url));
            spiderStartContainer.spiderResetDuplicateCheckByDomain(getDomain(url));
            spiderStartContainer.addRequest(request);
            spiderStartContainer.spiderStart();
        }
    }


}

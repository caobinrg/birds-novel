package com.leqiwl.novel.job.job;

import cn.hutool.core.collection.CollectionUtil;
import com.leqiwl.novel.config.sysconst.RequestConst;
import com.leqiwl.novel.domain.dto.CrawlerRequestDto;
import com.leqiwl.novel.domain.entify.crawler.CrawlerListRule;
import com.leqiwl.novel.domain.entify.crawler.CrawlerRule;
import com.leqiwl.novel.enums.CrawlerTypeEnum;
import com.leqiwl.novel.job.pip.SpiderStartContainer;
import com.leqiwl.novel.remote.SpiderJobStartRemote;
import com.leqiwl.novel.service.CrawlerRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: 飞鸟不过江
 * @Date: 2021/12/30 12:21
 * @Description: 采集启动,总线程
 */
@Slf4j
@Component
@Order(99)
public class SpiderStart implements ApplicationRunner, SpiderJobStartRemote {

    @Autowired
    private SpiderStartContainer spiderStartContainer;

    @Resource
    private CrawlerRuleService crawlerRuleService;

    private int status;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.start();
    }

    @Override
    public void start() {
        List<CrawlerRule> allRules = crawlerRuleService.getAll();
        List<CrawlerRule> collect = allRules.stream()
                .filter(rule -> rule.getOpenStatus() == 1)
                .collect(Collectors.toList());
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
            spiderStartContainer.addRequest(request);
        }
        if(CollectionUtil.isNotEmpty(collect)){
            spiderStartContainer.start();
            status = 1;
        }
    }

    @Override
    public int getSpiderStatus() {
        return this.status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

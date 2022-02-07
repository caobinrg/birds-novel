package com.leqiwl.novel.job.check;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.leqiwl.novel.domain.entify.crawler.CrawlerRule;
import com.leqiwl.novel.service.CrawlerRuleService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: 飞鸟不过江
 * @Date: 2021/12/30 15:57
 * @Description: 采集规则初始化
 */
@Component
@Order(20)
public class CrawlerRuleInitStart implements ApplicationRunner {

    @Resource
    private CrawlerRuleService crawlerRuleService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(!crawlerRuleService.hasData()){
            String s = ResourceUtil.readUtf8Str("crawlerRuleInit.json");
            List<CrawlerRule> crawlerRules = JSONUtil.toList(s, CrawlerRule.class);
            crawlerRuleService.saveAll(crawlerRules);
        }
    }
}

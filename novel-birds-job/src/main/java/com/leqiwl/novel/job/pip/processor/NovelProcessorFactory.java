package com.leqiwl.novel.job.pip.processor;

import com.leqiwl.novel.enums.CrawlerTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: 飞鸟不过江
 * @Date: 2021/12/27 09:55
 * @Description:
 */
@Component
public class NovelProcessorFactory {

    private static Map<Integer, NovelProcessor> NOVEL_PROCESSOR_MAP = new HashMap<>();

    @Autowired
    private void setProcessorStrategy(
           NovelListProcessor novelListProcessor,
           NovelInfoProcessor novelInfoProcessor,
           NovelContentProcessor novelContentProcessor
    ){
        NovelProcessorFactory.NOVEL_PROCESSOR_MAP.put(
                CrawlerTypeEnum.LIST.getType(),novelListProcessor);
        NovelProcessorFactory.NOVEL_PROCESSOR_MAP.put(
                CrawlerTypeEnum.DETAIL.getType(),novelInfoProcessor);
        NovelProcessorFactory.NOVEL_PROCESSOR_MAP.put(
                CrawlerTypeEnum.CONTENT.getType(),novelContentProcessor);
    }

    public NovelProcessor getProcessor(Integer crawlerType){
        NovelProcessor novelProcessor = NOVEL_PROCESSOR_MAP.get(crawlerType);
        return novelProcessor;
    }
}

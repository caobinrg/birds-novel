package com.leqiwl.novel.job.pip.processor;

import com.leqiwl.novel.domain.dto.CrawlerRequestDto;
import com.leqiwl.novel.domain.entify.crawler.CrawlerRule;
import us.codecraft.webmagic.Page;


/**
 * @author: 飞鸟不过江
 * @Date: 2021/12/27 09:48
 * @Description:
 */
public interface NovelProcessor {


    /**
     * 页面数据提取方法
     * @param page
     * @param requestInfo
     * @param crawlerInfo
     */
    void process(Page page, CrawlerRequestDto requestInfo, CrawlerRule crawlerInfo);


}

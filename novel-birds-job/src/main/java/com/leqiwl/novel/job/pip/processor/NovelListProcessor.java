package com.leqiwl.novel.job.pip.processor;

import cn.hutool.core.bean.BeanUtil;
import com.leqiwl.novel.common.util.UrlParseUtil;
import com.leqiwl.novel.domain.dto.CrawlerRequestDto;
import com.leqiwl.novel.domain.entify.crawler.CrawlerListRule;
import com.leqiwl.novel.domain.entify.crawler.CrawlerRule;
import com.leqiwl.novel.enums.CrawlerTypeEnum;
import com.leqiwl.novel.config.sysconst.DelayQueueConst;
import com.leqiwl.novel.config.sysconst.RequestConst;
import com.leqiwl.novel.service.CrawlerRuleService;
import com.leqiwl.novel.service.DelayQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: 飞鸟不过江
 * @Date: 2021/12/27 09:39
 * @Description: 列表处理
 */
@Component
@Slf4j
public class NovelListProcessor implements NovelProcessor {

    @Resource
    private CrawlerRuleService crawlerRuleService;

    @Resource
    private DelayQueueService<Request> delayQueueService;

    @Override
    public  void process(Page page, CrawlerRequestDto requestInfo, CrawlerRule crawlerInfo) {
        CrawlerListRule listRule = crawlerInfo.getListRule();
        if(null == listRule){
            return;
        }
        Html html = page.getHtml();
        List<Selectable> bookUrlNodes = html.xpath(listRule.getGetUrlListRule()).nodes();
        for (Selectable bookUrlNode : bookUrlNodes) {
            //将书籍详情页放入队列
            String novelUrl = bookUrlNode.xpath(listRule.getGetUrlRule()).toString();
            novelUrl = UrlParseUtil.urlReduction(page.getUrl().toString(),novelUrl);
            CrawlerRequestDto novelRequestInfo = CrawlerRequestDto.builder()
                    .baseUrl(requestInfo.getBaseUrl())
                    .ruleId(requestInfo.getRuleId())
                    .type(CrawlerTypeEnum.DETAIL.getType())
                    .url(novelUrl)
                    .build();
            Request novelRequest = new Request(novelUrl);
            novelRequest.putExtra(RequestConst.REQUEST_INFO,novelRequestInfo);
            page.addTargetRequest(novelRequest);
        }
        //将下一页放入队列
        pushNextListPage(page,requestInfo, crawlerInfo);
    }


    private  void pushNextListPage(Page page,CrawlerRequestDto requestInfo,CrawlerRule crawlerInfo) {
        CrawlerRequestDto nextRequestInfo = new CrawlerRequestDto();
        BeanUtil.copyProperties(requestInfo,nextRequestInfo);
        CrawlerListRule listRule = crawlerInfo.getListRule();
        String url = listRule.getSourceUrl();
        if(!url.contains(RequestConst.PAGE_REPLACE)){
            //单页任务
            //设置下次列表采集时间
            pushDelayMessage(nextRequestInfo, crawlerInfo);
            return;
        }
        Integer initStatus = crawlerInfo.getInitStatus();
        int currentPageNo = nextRequestInfo.getCurrentPageNo();
        int pageEndRule = 0;
        if(1 == initStatus){
            //初始化已完成后执行部分任务
            pageEndRule = listRule.getAfterInitPageNo();
        }else{
            //未初始化需要全量执行
            pageEndRule = listRule.getPageEndRule();
        }
        int nextPage = currentPageNo + 1;
        if(nextPage > pageEndRule){
            //初始化执行完毕
            if(0 == initStatus){
                crawlerRuleService.initFinish(crawlerInfo.getRuleId());
            }
            //设置下次列表采集时间
            pushDelayMessage(requestInfo, crawlerInfo);
            return;
        }
        String nextUrl = url.replace(RequestConst.PAGE_REPLACE, nextPage+"");
        nextRequestInfo.setUrl(nextUrl);
        nextRequestInfo.setCurrentPageNo(nextPage);
        Request nextPageRequest = new Request(nextUrl);
        nextPageRequest.putExtra(RequestConst.REQUEST_INFO,nextRequestInfo);
        page.addTargetRequest(nextPageRequest);
    }

    private void pushDelayMessage(CrawlerRequestDto requestInfo,
                                  CrawlerRule crawlerInfo) {
        CrawlerListRule listRule = crawlerInfo.getListRule();
        String url = listRule.getSourceUrl();
        int delayStartPage = listRule.getPageStartRule();
        if(delayStartPage <= 0){
            return;
        }
        long initAfterInterval = crawlerInfo.getInitAfterInterval();
        if(initAfterInterval < 15){
            initAfterInterval = 15;
        }
        String delayUrl = url.replace(RequestConst.PAGE_REPLACE,
                delayStartPage +"");
        CrawlerRequestDto delayRequestInfo = new CrawlerRequestDto();
        BeanUtil.copyProperties(requestInfo,delayRequestInfo);
        delayRequestInfo.setUrl(delayUrl);
        delayRequestInfo.setCurrentPageNo(delayStartPage);
        Request delayRequest = new Request(delayUrl);
        delayRequest.putExtra(RequestConst.REQUEST_INFO,delayRequestInfo);
        delayQueueService.pushData(delayRequest,
                initAfterInterval,
                DelayQueueConst.CRAWLER_LIST_QUEUE);
    }


}

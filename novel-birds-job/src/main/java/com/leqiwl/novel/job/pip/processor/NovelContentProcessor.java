package com.leqiwl.novel.job.pip.processor;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.leqiwl.novel.domain.dto.CrawlerRequestDto;
import com.leqiwl.novel.domain.entify.Content;
import com.leqiwl.novel.domain.entify.crawler.CrawlerContentRule;
import com.leqiwl.novel.domain.entify.crawler.CrawlerRule;
import com.leqiwl.novel.enums.CrawlerSaveTypeEnum;
import com.leqiwl.novel.service.ContentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: 飞鸟不过江
 * @Date: 2021/12/27 09:51
 * @Description: content 处理
 */
@Slf4j
@Component
public class NovelContentProcessor implements NovelProcessor{

    @Resource
    private ContentService contentService;

    @Override
    public void process(Page page, CrawlerRequestDto requestInfo, CrawlerRule crawlerInfo) {
        CrawlerContentRule contentRule = crawlerInfo.getContentRule();
        if(null == contentRule){
            page.setSkip(true);
            return;
        }
        Html html = page.getHtml();
        String chapterId = requestInfo.getChapterId();
        String novelId = requestInfo.getNovelId();
        Content content = contentService.getByNovelAndChapterId(novelId,chapterId);
        if(StrUtil.isNotBlank(content.getNovelId()) && !requestInfo.isRetry()){
            page.setSkip(true);
            return;
        }
        String name = html.xpath(contentRule.getNameRule()).toString();
        Selectable htmlXpath = html.xpath("");
        List<String> contentOutLabelRule = contentRule.getContentOutLabelRule();
        if(CollectionUtil.isNotEmpty(contentOutLabelRule)){
            for (String label : contentOutLabelRule) {
                String labelValue = "<"+label+"([\\s\\S]*?)"+label+">";
                htmlXpath = htmlXpath.replace(labelValue,"");
            }
        }
        String contentText = new Html(htmlXpath.toString()).xpath(contentRule.getContentTextRule()).toString();
        List<String> contentOutStrs = contentRule.getContentOutStr();
        if(CollectionUtil.isNotEmpty(contentOutStrs)){
            for (String contentOutStr : contentOutStrs) {
                contentText = contentText.replace(contentOutStr,"");
            }
        }
        content= Content.builder()
                .novelId(requestInfo.getNovelId())
                .novelName(requestInfo.getNovelName())
                .chapterId(chapterId)
                .name(name)
                .contentText(contentText)
                .relUrl(requestInfo.getUrl())
                .build();
        //章节内容持久化
        page.putField(CrawlerSaveTypeEnum.CONTENT.getType().toString(), content);
    }
}

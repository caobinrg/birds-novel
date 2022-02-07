package com.leqiwl.novel.admin;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.TokenizerEngine;
import cn.hutool.extra.tokenizer.Word;
import cn.hutool.http.HttpUtil;
import com.leqiwl.novel.config.sysconst.RequestConst;
import com.leqiwl.novel.domain.dto.CrawlerRequestDto;
import com.leqiwl.novel.domain.entify.crawler.CrawlerContentRule;
import com.leqiwl.novel.domain.entify.crawler.CrawlerDetailRule;
import com.leqiwl.novel.domain.entify.crawler.CrawlerListRule;
import com.leqiwl.novel.domain.entify.crawler.CrawlerRule;
import com.leqiwl.novel.enums.CrawlerTypeEnum;
import com.leqiwl.novel.service.ChapterService;
import com.leqiwl.novel.service.NovelService;
import com.leqiwl.novel.service.NovelTypeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: 飞鸟不过江
 * @Date: 2022/1/27 16:01
 * @Description:
 */
@Slf4j
@SpringBootTest(classes = AdminTestApplication.class)
@RunWith(SpringRunner.class)
public class CrawlerEditorTest {


    @Resource
    private NovelTypeService novelTypeService;

    @Resource
    private TokenizerEngine tokenizerEngine;

    @Test
    public void test() throws InterruptedException {

        CrawlerListRule listRule = CrawlerListRule.builder()
                .sourceUrl("https://www.xbiquge.la/fenlei/1_[pageNo].html")
                .pageStartRule(1)
                .pageEndRule(2)
                .afterInitPageNo(3)
                .getUrlListRule("//div[@class=\"l\"]/ul/li")
                .getUrlRule("//span[@class=\"s2\"]/a/@href")
                .build();

        CrawlerDetailRule detailRule = CrawlerDetailRule.builder()
                .nameRule("//meta[@property=\"og:title\"]/@content")
                .authorRule("//meta[@property=\"og:novel:author\"]/@content")
                .imageRule("//meta[@property=\"og:image\"]/@content")
                .imageSave(true)
//                .wordNumRule()
//                .updateStatusRule()
//                .scoreRule()
//                .scorePersonNumRule()
                .lastUpdateMarkRule("//div[@id=\"info\"]/p[3]/text()")
                .novelTypeRule("//meta[@property=\"og:novel:category\"]/@content")
                .keywordsRule("//meta[@property=\"keywords\"]/@content")
                .keywordsReplaceRule(new ArrayList<>())
                .descriptionRule("//meta[@property=\"description\"]/@content")
                .descriptionReplaceRule(CollectionUtil.newArrayList("新笔趣阁"))
                .introRule("//meta[@property=\"og:description\"]/@content")
                .introReplaceRule(CollectionUtil.newArrayList("新笔趣阁"))
                .chapterListRule("//div[@class=\"box_con\"]/div/dl/dd/")
                .chapterListSkipNoRule(0)
                .chapterNameRule("//a/text()")
                .chapterUrlRule("//a/@href")
                .build();

        CrawlerContentRule contentRule = CrawlerContentRule.builder()
                .nameRule("//h1/text()")
                .contentTextRule("//div[@id=\"content\"]/html()")
                .contentOutLabelRule(CollectionUtil.newArrayList("p"))
                .build();

        String baseUrl = listRule.getSourceUrl().replace(RequestConst.PAGE_REPLACE,listRule.getPageStartRule()+"");
        String sourceUrlHtmlStr = HttpUtil.get(baseUrl);
        Html sourceUrlHtml = new Html(sourceUrlHtmlStr);
        List<Selectable> bookUrlNodes = sourceUrlHtml.xpath(listRule.getGetUrlListRule()).nodes();
        if(CollectionUtil.isEmpty(bookUrlNodes)){
            log.info("书籍列表为空");
            return;
        }
        for (int i = 0; i < bookUrlNodes.size() &&  i < 5 ; i++) {
            Selectable bookUrlNode = bookUrlNodes.get(i);
            String novelUrl = bookUrlNode.xpath(listRule.getGetUrlRule()).toString();
            log.info("书籍url:{}",novelUrl);
        }
        log.info("......");
        String novelUrl = bookUrlNodes.get(0).xpath(listRule.getGetUrlRule()).toString();
        log.info("==============列表结束===============");
        Thread.sleep(8000);
        novelUrl =  urlReduction(baseUrl,novelUrl);
        log.info("信息页开始，url:{}",novelUrl);
        String novelInfoHtmlStr = HttpUtil.get(novelUrl);
        Html novelInfoHtml = new Html(novelInfoHtmlStr);
        String name = novelInfoHtml.xpath(detailRule.getNameRule()).toString();
        log.info("书籍名称:{}",name);
        String author = novelInfoHtml.xpath(detailRule.getAuthorRule()).toString();
        log.info("书籍作者:{}",author);
        String imageUrl = novelInfoHtml.xpath(detailRule.getImageRule()).toString();
        log.info("封面地址:{}",imageUrl);
        String lastUpdateMark = novelInfoHtml.xpath(detailRule.getLastUpdateMarkRule()).toString();
        log.info("最后更新标识:{}",lastUpdateMark);
        String novelType = getNovelType(novelInfoHtml, detailRule.getNovelTypeRule());
        log.info("书籍类型:{}",novelType);
        String keywords = novelInfoHtml.xpath(detailRule.getKeywordsRule()).toString();
        keywords = replaceByRules(keywords,detailRule.getKeywordsReplaceRule());
        log.info("关键词:{}",keywords);
        String description = novelInfoHtml.xpath(detailRule.getDescriptionRule()).toString();
        description = replaceByRules(description,detailRule.getDescriptionReplaceRule());
        log.info("描述:{}",description);
        String intro = novelInfoHtml.xpath(detailRule.getIntroRule()).toString();
        intro = replaceByRules(intro,detailRule.getIntroReplaceRule());
        log.info("简介:{}",intro);
        List<Selectable> chapterNodeList = novelInfoHtml.xpath(detailRule.getChapterListRule()).nodes();
        if(CollectionUtil.isEmpty(chapterNodeList) && chapterNodeList.size() > detailRule.getChapterListSkipNoRule() ){
            log.info("可用章节列表为空");
            return;
        }
        for (int i = detailRule.getChapterListSkipNoRule(); i < chapterNodeList.size() && i < 5 ; i++) {
            Selectable node = chapterNodeList.get(i);
            String chapterName = node.xpath(detailRule.getChapterNameRule()).toString();
            String chapterUrl = node.xpath(detailRule.getChapterUrlRule()).toString();
            log.info("章节名称:{}，章节url:{}",chapterName,chapterUrl);
        }
        log.info("......");
        String chapterUrl = chapterNodeList.get(detailRule.getChapterListSkipNoRule()).xpath(detailRule.getChapterUrlRule()).toString();
        log.info("==============信息结束===============");
        Thread.sleep(8000);
        chapterUrl = urlReduction(novelUrl, chapterUrl);
        log.info("内容页开始，url:{}",chapterUrl);
        String novelContentHtmlStr = HttpUtil.get(chapterUrl);
        Html novelContentHtml = new Html(novelContentHtmlStr);
        String chapterName = novelContentHtml.xpath(contentRule.getNameRule()).toString();
        log.info("内容页章节名称:{}",chapterName);
        Selectable htmlXpath = novelContentHtml.xpath("");
        List<String> contentOutLabelRule = contentRule.getContentOutLabelRule();
        if(CollectionUtil.isNotEmpty(contentOutLabelRule)){
            for (String label : contentOutLabelRule) {
                String labelValue = "<"+label+"([\\s\\S]*)"+label+">";
                htmlXpath = htmlXpath.replace(labelValue,"");
            }
        }
        String contentText = new Html(htmlXpath.toString()).xpath(contentRule.getContentTextRule()).toString();
        log.info("章节内容:{}",contentText);
        log.info("==============内容结束===============");
    }


    private String getNovelType(Html html, String novelTypeRule) {
        String sourceNovelType = html.xpath(novelTypeRule).toString();
        Result sourceTypeAnalyzerResult = tokenizerEngine.parse(sourceNovelType);
        List<String> sourceTypeWordList =
                CollUtil.newArrayList((Iterator<Word>) sourceTypeAnalyzerResult)
                        .stream()
                        .distinct()
                        .map(Word::getText)
                        .collect(Collectors.toList());
        String novelType = "";
        for (String sourceTypeWord : sourceTypeWordList) {
            Map<String, String> typeMap = novelTypeService.getTypeMap();
            novelType = typeMap.get(sourceTypeWord);
            if(StrUtil.isNotBlank(novelType)){
                return novelType;
            }
        }
        if(StrUtil.isBlank(novelType)){
            novelType = "其他";
        }
        return novelType;
    }


    private String replaceByRules(String sourceWord,List<String> rules){
        if(StrUtil.isBlank(sourceWord)){
            return "";
        }
        for (String rule : rules) {
            sourceWord = sourceWord.replaceAll(rule,"");
        }
        return sourceWord;
    }


    private String urlReduction(String baseUrl,String url){
        String start = "http";
        if(baseUrl.startsWith("https")){
            start = "https";
        }
        if(url.startsWith(start) || url.startsWith("//")){
            return url;
        }
        if(url.startsWith("/")){
            String host = URLUtil.url(baseUrl).getHost();
            url =  host + url;
        }else{
            url = baseUrl + url;
        }
        if(!url.startsWith(start) && !url.startsWith("//")){
            url = start + "://" + url;
        }
        return url;
    }

}

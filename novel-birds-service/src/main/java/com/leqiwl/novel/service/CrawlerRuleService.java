package com.leqiwl.novel.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.TokenizerEngine;
import cn.hutool.extra.tokenizer.Word;
import cn.hutool.http.HttpUtil;
import com.leqiwl.novel.common.util.UrlParseUtil;
import com.leqiwl.novel.config.sysconst.RequestConst;
import com.leqiwl.novel.domain.dto.CrawlerRuleEditInDto;
import com.leqiwl.novel.domain.dto.XpathTestInDto;
import com.leqiwl.novel.domain.dto.XpathTestOutDto;
import com.leqiwl.novel.domain.entify.crawler.CrawlerContentRule;
import com.leqiwl.novel.domain.entify.crawler.CrawlerDetailRule;
import com.leqiwl.novel.domain.entify.crawler.CrawlerListRule;
import com.leqiwl.novel.domain.entify.crawler.CrawlerRule;
import com.leqiwl.novel.repository.CrawlerRuleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: 飞鸟不过江
 * @Date: 2021/12/30 16:00
 * @Description:
 */
@Slf4j
@Service
public class CrawlerRuleService {

    @Resource
    private CrawlerRuleRepository crawlerRuleRepository;

    @Resource
    private NovelTypeService novelTypeService;

    @Resource
    private TokenizerEngine tokenizerEngine;

    @Resource
    private JobRemoteService jobRemoteService;

    @Cacheable(cacheNames = "rule#10m", key = "'all'")
    public List<CrawlerRule> getAll(){
        List<CrawlerRule> crawlerRules = crawlerRuleRepository.findAll();
        if(CollectionUtil.isNotEmpty(crawlerRules)){
            List<CrawlerRule> collect = crawlerRules.stream()
                    .filter(rule -> rule.getIsActive() == 1)
                    .collect(Collectors.toList());
            if(CollectionUtil.isEmpty(collect)){
                return new ArrayList<>();
            }
            return collect;
        }
        return new ArrayList<>();
    }

    @CachePut(cacheNames = "rule#10m", key = "'all'")
    public List<CrawlerRule> saveAll(List<CrawlerRule> rules){
       return crawlerRuleRepository.saveAll(rules);
    }


    @CacheEvict(cacheNames = "rule#10m", key = "'all'")
    public boolean initFinish(String ruleId){
        CrawlerRule rule = getByRuleId(ruleId);
        if(StrUtil.isNotBlank(rule.getId())){
            rule.setInitStatus(1);
            CrawlerRule save = crawlerRuleRepository.save(rule);
            if(null != save){
                return true;
            }
            return false;
        }
        return true;
    }


    @CacheEvict(cacheNames = "rule#10m", key = "'all'")
    public CrawlerRule saveRule(CrawlerRuleEditInDto crawlerRuleEditInDto){
        ParseCrawler parseCrawler = new ParseCrawler(crawlerRuleEditInDto).invoke();
        CrawlerListRule listRule = parseCrawler.getListRule();
        CrawlerDetailRule detailRule = parseCrawler.getDetailRule();
        CrawlerContentRule contentRule = parseCrawler.getContentRule();
        String ruleId = crawlerRuleEditInDto.getRuleId();
        CrawlerRule dbCrawlerRule = null;
        if(StrUtil.isNotBlank(ruleId)){
            dbCrawlerRule = crawlerRuleRepository.findByRuleId(ruleId);
        }
        if(StrUtil.isBlank(ruleId)){
            ruleId = IdUtil.simpleUUID();
        }
        CrawlerRule crawlerRule;
        Date now = new Date();
        if(null != dbCrawlerRule){
            crawlerRule = dbCrawlerRule;
            crawlerRule.setRuleName(crawlerRuleEditInDto.getRuleName());
            crawlerRule.setListRule(listRule);
            crawlerRule.setDetailRule(detailRule);
            crawlerRule.setContentRule(contentRule);
            crawlerRule.setUpdateTime(now);
        }else {
            crawlerRule = CrawlerRule.builder()
                    .ruleId(ruleId)
                    .ruleName(crawlerRuleEditInDto.getRuleName())
                    .listRule(listRule)
                    .detailRule(detailRule)
                    .contentRule(contentRule)
                    .createTime(now)
                    .updateTime(now)
                    .build();
        }
        CrawlerRule save = crawlerRuleRepository.save(crawlerRule);
        return save;
    }


    @CacheEvict(cacheNames = "rule#10m", key = "'all'")
    public boolean delRule(String ruleId){
        CrawlerRule rule = getByRuleId(ruleId);
        if(StrUtil.isNotBlank(rule.getId())){
            rule.setIsActive(0);
            CrawlerRule save = crawlerRuleRepository.save(rule);
            if(null != save){
                return true;
            }
            return false;
        }
        return true;
    }


    public boolean enableRule(String ruleId,Integer openStatus){
        CrawlerRule rule = getByRuleId(ruleId);
        if(StrUtil.isNotBlank(rule.getId())){
            rule.setOpenStatus(openStatus);
            CrawlerRule save = crawlerRuleRepository.save(rule);
            ((CrawlerRuleService)AopContext.currentProxy()).ruleRefresh();
            if(null != save){
                if(openStatus == 1){
                    jobRemoteService.startRule(save);
                }
                return true;
            }
            return false;
        }
        return true;
    }


    @CacheEvict(cacheNames = "rule#10m", key = "'all'",allEntries = true)
    public void ruleRefresh(){

    }


    public boolean hasData(){
        long count = crawlerRuleRepository.count();
        if(count>0){
            return true;
        }
        return false;
    }


    public CrawlerRule getByRuleId(String ruleId){
        List<CrawlerRule> all = ((CrawlerRuleService)AopContext.currentProxy()).getAll();
        if(CollectionUtil.isEmpty(all)){
            return new CrawlerRule();
        }
        CrawlerRule crawlerRule = all.stream()
                .filter(rule -> rule.getRuleId().equals(ruleId))
                .findFirst()
                .orElse(null);
        if(null == crawlerRule){
            return new CrawlerRule();
        }
        return crawlerRule;
    }


    public XpathTestOutDto xpathTest(XpathTestInDto dto){
        XpathTestOutDto xpathTestOutDto = new XpathTestOutDto();
        String sourceUrl = dto.getSourceUrl();
        String xpathText = dto.getXpathText();
        String sourceUrlHtmlStr = HttpUtil.get(sourceUrl);
        xpathTestOutDto.setPageResult(sourceUrlHtmlStr);
        Html sourceUrlHtml = new Html(sourceUrlHtmlStr);
        String s = sourceUrlHtml.xpath(xpathText).toString();
        xpathTestOutDto.setTextResult(s);
        return xpathTestOutDto;
    }




    public String ruleTest(CrawlerRuleEditInDto crawlerRuleEditInDto)  {
        StringBuilder result = new StringBuilder();
        try {
            CrawlerListRule listRule = new CrawlerListRule();
            BeanUtil.copyProperties(crawlerRuleEditInDto,listRule);
            CrawlerDetailRule detailRule = new CrawlerDetailRule();
            BeanUtil.copyProperties(crawlerRuleEditInDto,detailRule);
            CrawlerContentRule contentRule = new CrawlerContentRule();
            BeanUtil.copyProperties(crawlerRuleEditInDto,contentRule);
            contentRule.setNameRule(crawlerRuleEditInDto.getContentNameRule());

            result.append("==============测试开始===============\n");
            String baseUrl = listRule.getSourceUrl().replace(RequestConst.PAGE_REPLACE,listRule.getPageStartRule()+"");
            String sourceUrlHtmlStr = HttpUtil.get(baseUrl);
            Html sourceUrlHtml = new Html(sourceUrlHtmlStr);
            List<Selectable> bookUrlNodes = sourceUrlHtml.xpath(listRule.getGetUrlListRule()).nodes();
            if(CollectionUtil.isEmpty(bookUrlNodes)){
                result.append("书籍列表为空\n");
                return result.toString();
            }
            for (int i = 0; i < bookUrlNodes.size() &&  i < 5 ; i++) {
                Selectable bookUrlNode = bookUrlNodes.get(i);
                String novelUrl = bookUrlNode.xpath(listRule.getGetUrlRule()).toString();
                result.append(StrUtil.format("书籍url:{}\n", novelUrl));
            }
            result.append("......\n");
            String novelUrl = bookUrlNodes.get(0).xpath(listRule.getGetUrlRule()).toString();
            result.append("==============列表结束===============\n");
            Thread.sleep(5000);
            novelUrl =  UrlParseUtil.urlReduction(baseUrl,novelUrl);
            result.append(StrUtil.format("信息页开始，url:{}\n",novelUrl));
            String novelInfoHtmlStr = HttpUtil.get(novelUrl);
            Html novelInfoHtml = new Html(novelInfoHtmlStr);
            String name = novelInfoHtml.xpath(detailRule.getNameRule()).toString();
            result.append(StrUtil.format("书籍名称:{}\n",name));
            String author = novelInfoHtml.xpath(detailRule.getAuthorRule()).toString();
            result.append(StrUtil.format("书籍作者:{}\n",author));
            String imageUrl = novelInfoHtml.xpath(detailRule.getImageRule()).toString();
            result.append(StrUtil.format("封面地址:{}\n",imageUrl));
            String lastUpdateMark = novelInfoHtml.xpath(detailRule.getLastUpdateMarkRule()).toString();
            result.append(StrUtil.format("最后更新标识:{}\n",lastUpdateMark));
            String novelType = getNovelType(novelInfoHtml, detailRule.getNovelTypeRule());
            result.append(StrUtil.format("书籍类型:{}\n",novelType));
            String keywords = novelInfoHtml.xpath(detailRule.getKeywordsRule()).toString();
            keywords = replaceByRules(keywords,detailRule.getKeywordsReplaceRule());
            result.append(StrUtil.format("关键词:{}\n",keywords));
            String description = novelInfoHtml.xpath(detailRule.getDescriptionRule()).toString();
            description = replaceByRules(description,detailRule.getDescriptionReplaceRule());
            result.append(StrUtil.format("描述:{}\n",description));
            String intro = novelInfoHtml.xpath(detailRule.getIntroRule()).toString();
            intro = replaceByRules(intro,detailRule.getIntroReplaceRule());
            result.append(StrUtil.format("简介:{}\n",intro));
            List<Selectable> chapterNodeList = novelInfoHtml.xpath(detailRule.getChapterListRule()).nodes();
            if(CollectionUtil.isEmpty(chapterNodeList) || chapterNodeList.size() <= detailRule.getChapterListSkipNoRule() ){
                result.append("可用章节列表为空");
                return result.toString();
            }
            for (int i = detailRule.getChapterListSkipNoRule(); i < chapterNodeList.size() && i < 5 ; i++) {
                Selectable node = chapterNodeList.get(i);
                String chapterName = node.xpath(detailRule.getChapterNameRule()).toString();
                String chapterUrl = node.xpath(detailRule.getChapterUrlRule()).toString();
                result.append(StrUtil.format("章节名称:{}，章节url:{}\n",chapterName,chapterUrl));
            }
            result.append("......\n");
            String chapterUrl = chapterNodeList.get(detailRule.getChapterListSkipNoRule()).xpath(detailRule.getChapterUrlRule()).toString();
            result.append("==============信息结束===============\n");
            Thread.sleep(5000);
            chapterUrl = UrlParseUtil.urlReduction(novelUrl, chapterUrl);
            result.append(StrUtil.format("内容页开始，url:{}\n",chapterUrl));
            String novelContentHtmlStr = HttpUtil.get(chapterUrl);
            Html novelContentHtml = new Html(novelContentHtmlStr);
            String chapterName = novelContentHtml.xpath(contentRule.getNameRule()).toString();
            result.append(StrUtil.format("内容页章节名称:{}\n",chapterName));
            Selectable htmlXpath = novelContentHtml.xpath("");
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
            result.append(StrUtil.format("章节内容:{}\n",contentText));
            result.append("==============内容结束===============\n");
        }catch (Exception e){
            log.warn(e.getMessage(),e);
            result.append(StrUtil.format("ex:{}" , e.getMessage()));
        }finally {
            return result.toString();
        }
    }


    private String getNovelType(Html html, String novelTypeRule) {
        if(null == html || StrUtil.isBlank(novelTypeRule) ){
            return "";
        }
        String sourceNovelType = html.xpath(novelTypeRule).toString();
        if(null == sourceNovelType){
            sourceNovelType = "";
        }
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
        if(CollectionUtil.isNotEmpty(rules)){
            for (String rule : rules) {
                sourceWord = sourceWord.replaceAll(rule,"");
            }
        }
        return sourceWord;
    }




    private class ParseCrawler {
        private CrawlerRuleEditInDto crawlerRuleEditInDto;
        private CrawlerListRule listRule;
        private CrawlerDetailRule detailRule;
        private CrawlerContentRule contentRule;

        public ParseCrawler(CrawlerRuleEditInDto crawlerRuleEditInDto) {
            this.crawlerRuleEditInDto = crawlerRuleEditInDto;
        }

        public CrawlerListRule getListRule() {
            return listRule;
        }

        public CrawlerDetailRule getDetailRule() {
            return detailRule;
        }

        public CrawlerContentRule getContentRule() {
            return contentRule;
        }

        public ParseCrawler invoke() {
            listRule = new CrawlerListRule();
            BeanUtil.copyProperties(crawlerRuleEditInDto,listRule);
            detailRule = new CrawlerDetailRule();
            BeanUtil.copyProperties(crawlerRuleEditInDto,detailRule);
            contentRule = new CrawlerContentRule();
            BeanUtil.copyProperties(crawlerRuleEditInDto,contentRule);
            contentRule.setNameRule(crawlerRuleEditInDto.getContentNameRule());
            return this;
        }
    }
}

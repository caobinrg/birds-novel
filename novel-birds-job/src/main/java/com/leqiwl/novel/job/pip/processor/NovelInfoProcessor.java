package com.leqiwl.novel.job.pip.processor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.TokenizerEngine;
import cn.hutool.extra.tokenizer.Word;
import com.leqiwl.novel.common.util.NovelIdMarkUtil;
import com.leqiwl.novel.common.util.UrlParseUtil;
import com.leqiwl.novel.domain.dto.CrawlerRequestDto;
import com.leqiwl.novel.domain.entify.Chapter;
import com.leqiwl.novel.domain.entify.Novel;
import com.leqiwl.novel.domain.entify.crawler.CrawlerDetailRule;
import com.leqiwl.novel.domain.entify.crawler.CrawlerRule;
import com.leqiwl.novel.enums.CrawlerSaveTypeEnum;
import com.leqiwl.novel.enums.CrawlerTypeEnum;
import com.leqiwl.novel.enums.NovelUpdateStatusEnum;
import com.leqiwl.novel.config.sysconst.RequestConst;
import com.leqiwl.novel.service.ChapterService;
import com.leqiwl.novel.service.NovelService;
import com.leqiwl.novel.service.NovelTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import javax.annotation.Resource;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: 飞鸟不过江
 * @Date: 2021/12/27 09:51
 * @Description: info 处理
 */
@Slf4j
@Component
public class NovelInfoProcessor implements NovelProcessor{


    @Resource
    private NovelService novelService;

    @Resource
    private NovelTypeService novelTypeService;

    @Resource
    private ChapterService chapterService;

    @Resource
    private TokenizerEngine tokenizerEngine;

    @Override
    public void process(Page page, CrawlerRequestDto requestInfo, CrawlerRule crawlerInfo) {
        CrawlerDetailRule detailRule = crawlerInfo.getDetailRule();
        if(null == detailRule){
            page.setSkip(true);
            return;
        }
        Html html = page.getHtml();
        String name = html.xpath(detailRule.getNameRule()).toString();
        String author = html.xpath(detailRule.getAuthorRule()).toString();
        String novelIdMark = NovelIdMarkUtil.getNovelIdMark(name, author);
        String lastUpdateMark = html.xpath(detailRule.getLastUpdateMarkRule()).toString();
        Novel novel = novelService.getByIdMark(novelIdMark);
        if(null != novel && null != novel.getId()){
            if(novel.getLastUpdateMark().equals(lastUpdateMark) || !crawlerInfo.getRuleId().equals(novel.getRuleId())){
                //novel 存在 未更新 规则非源站
                // todo 基准站换源
                page.setSkip(true);
                return;
            }
        }
        if(null == novel || null == novel.getId()){
            novel = null;
        }
        novel = getNovelInfo(page,requestInfo,crawlerInfo,html,novel);
        //书籍信息持久化
        page.putField(CrawlerSaveTypeEnum.DETAIL.getType().toString(), novel);
    }

    private Novel getNovelInfo(Page page, CrawlerRequestDto requestInfo,
                               CrawlerRule crawlerInfo,Html html,Novel novel){
        CrawlerDetailRule detailRule = crawlerInfo.getDetailRule();
        String name = html.xpath(detailRule.getNameRule()).toString();
        List<Selectable> chapterNodeList = html.xpath(detailRule.getChapterListRule()).nodes();
        String novelId  = getNovelId(novel);
        String relUrl = requestInfo.getUrl();
        requestInfo.setNovelId(novelId);
        requestInfo.setNovelName(name);
        long getChapterListStart = System.currentTimeMillis();
        //获取章节信息，并将章节url推入队列
        List<Chapter> chapterList = getContentUrl(page, requestInfo, detailRule, name, chapterNodeList,novel,novelId);
        long getChapterListEnd = System.currentTimeMillis();
        log.info("数据获取chapterList---:novel:{},耗时:{}ms",novelId,getChapterListEnd-getChapterListStart);
        long generateStart = System.currentTimeMillis();
        novel = generateNovel(page,crawlerInfo, html, novel, name, novelId, relUrl, chapterList);
        long generateEnd = System.currentTimeMillis();
        log.info("数据获取novel:{},耗时:{}ms",novelId,generateEnd-generateStart);
        return novel;
    }

    private Novel generateNovel(Page page,CrawlerRule crawlerInfo, Html html, Novel novel,
                                String name, String novelId, String relUrl,
                                List<Chapter> chapterList) {
        CrawlerDetailRule detailRule = crawlerInfo.getDetailRule();
        //更新标识
        String lastUpdateMark = html.xpath(detailRule.getLastUpdateMarkRule()).toString();
        Date now = new Date();
        //最新章节
        int infoChapterSize = 5;
        if(chapterList.size()<=5){
            infoChapterSize = chapterList.size();
        }
        List<Chapter> novelChapters = CollectionUtil.reverseNew(chapterList).subList(0, infoChapterSize);
        //字数
        String wordNum = "0";
        if(StrUtil.isNotBlank(detailRule.getWordNumRule())){
            wordNum = html.xpath(detailRule.getWordNumRule()).toString();
        }
        //更新状态
        String updateStatus = NovelUpdateStatusEnum.UPDATE_ING.getName();
        if(StrUtil.isNotBlank(detailRule.getUpdateStatusRule())){
            String sourceUpdateStatus = html.xpath(detailRule.getUpdateStatusRule()).toString();
            if(StrUtil.isNotBlank(sourceUpdateStatus)
                    && sourceUpdateStatus.contains("完")
                    && !sourceUpdateStatus.contains("未")){
                updateStatus = NovelUpdateStatusEnum.COMPLETE.getName();
            }
        }
        if(null != novel){
            //书籍已存在，不需要重新获取其他信息
            novel.setChapterList(novelChapters);
            novel.setWordNum(wordNum);
            novel.setLastUpdateMark(lastUpdateMark);
            novel.setUpdateStatus(updateStatus);
            novel.setUpdateTime(now);
            return novel;
        }
        //作者
        String author = html.xpath(detailRule.getAuthorRule()).toString();
        //唯一标识
        String novelIdMark = NovelIdMarkUtil.getNovelIdMark(name, author);
        //小说图片
        String imageUrl = html.xpath(detailRule.getImageRule()).toString();
        if(detailRule.isImageSave() && StrUtil.isNotBlank(imageUrl)){
            String suf = StrUtil.subSuf(imageUrl, imageUrl.lastIndexOf("."));
            //图片持久化
            page.putField(CrawlerSaveTypeEnum.IMG.getType().toString(), imageUrl);
            page.putField(CrawlerSaveTypeEnum.IMG_PATH.getType().toString(), novelIdMark + suf);

        }

        //评分处理
        String score = RandomUtil.randomDouble(0.1,10,1, RoundingMode.FLOOR) + "";
        if(StrUtil.isNotBlank(detailRule.getScoreRule())){
            score = html.xpath(detailRule.getScoreRule()).toString();
        }
        //评分人数
        String scorePersonNum = RandomUtil.randomInt(1,9999)+"";
        if(StrUtil.isNotBlank(detailRule.getScorePersonNumRule())){
            scorePersonNum = html.xpath(detailRule.getScorePersonNumRule()).toString();
        }
        String novelType = getNovelType(html, detailRule);
        //关键字处理
        String keywords = html.xpath(detailRule.getKeywordsRule()).toString();
        keywords = replaceByRules(keywords,detailRule.getKeywordsReplaceRule());
        //描述处理
        String description = html.xpath(detailRule.getDescriptionRule()).toString();
        description = replaceByRules(description,detailRule.getDescriptionReplaceRule());
        //简介处理
        String intro = html.xpath(detailRule.getIntroRule()).toString();
        intro = replaceByRules(intro,detailRule.getIntroReplaceRule());
        return Novel.builder()
                .novelId(novelId)
                .idMark(novelIdMark)
                .name(name)
                .author(author)
                .image(imageUrl)
                .wordNum(wordNum)
                .updateStatus(updateStatus)
                .score(score)
                .scorePersonNum(scorePersonNum)
                .lastUpdateMark(lastUpdateMark)
                .novelType(novelType)
                .keywords(keywords)
                .description(description)
                .intro(intro)
                .chapterList(novelChapters)
                .relUrl(relUrl)
                .ruleId(crawlerInfo.getRuleId())
                .createTime(now)
                .updateTime(now)
                .build();
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

    private String getNovelType(Html html, CrawlerDetailRule detailRule) {
        String sourceNovelType = html.xpath(detailRule.getNovelTypeRule()).toString();
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

    private String getNovelId(Novel novel){
        if(null == novel){
            return RandomUtil.randomNumbers(11);
        }
        return novel.getNovelId();
    }

    private List<Chapter>  getContentUrl(Page page, CrawlerRequestDto requestInfo,
                              CrawlerDetailRule detailRule,
                               String name,List<Selectable> chapterNodeList,Novel novel,String novelId) {
        Map<String, Chapter> chapterMap = new HashMap<>();
        List<Chapter> chapterList = new ArrayList<>();
        if(null != novel && null != novel.getChapterList()){
            chapterList = chapterService.findByNovelId(novelId);
            chapterMap = chapterList.stream().collect(Collectors.toMap(Chapter::getIdMark, Chapter -> Chapter ,(v1, v2) -> v1));
            if(chapterList.size() != chapterMap.size()){
                //todo 处理重复章节
            }
        }
        //跳过章节
        chapterNodeList = CollectionUtil.sub(chapterNodeList,
                detailRule.getChapterListSkipNoRule(),
                chapterNodeList.size());
        List<Chapter> chapterSaveList = new ArrayList<>();
        for (int i = 0; i < chapterNodeList.size(); i++) {
            Selectable node = chapterNodeList.get(i);
            String chapterName = node.xpath(detailRule.getChapterNameRule()).toString();
            String chapterUrl = node.xpath(detailRule.getChapterUrlRule()).toString();
            chapterUrl = UrlParseUtil.urlReduction(page.getUrl().toString(),chapterUrl);
            String idMark = NovelIdMarkUtil.getChapterIdMark(name,chapterName);
            Chapter dbChapter = chapterMap.get(idMark);
            if(null != dbChapter){
                //章节已存在
                Integer chapterIndex = dbChapter.getChapterIndex();
                if(i != chapterIndex){
                    //需要重建索引
                    dbChapter.setChapterIndex(i);
                    chapterSaveList.add(dbChapter);
                }
                continue;
            }
            //章节不存在
            String chapterId = RandomUtil.randomNumbers(8);
            Chapter chapter = Chapter.builder()
                    .novelId(novelId)
                    .chapterId(chapterId)
                    .chapterIndex(i)
                    .idMark(idMark)
                    .chapterName(chapterName)
                    .novelName(requestInfo.getNovelName())
                    .chapterUrl(chapterUrl)
                    .pageUrl(page.getUrl().toString())
                    .ruleId(requestInfo.getRuleId())
                    .build();
            chapterList.add(chapter);
            //章节页推入采集列表
            pushContentPage(chapterUrl,page,requestInfo,chapterId);
            chapterSaveList.add(chapter);
        }
        //章节信息持久化
        page.putField(CrawlerSaveTypeEnum.CHAPTER.getType().toString(), chapterSaveList);
        chapterList = chapterList.stream()
                .sorted(Comparator.comparing(Chapter::getChapterIndex))
                .collect(Collectors.toList());
        return chapterList;
    }

    private void pushContentPage(String url,Page page,
                                 CrawlerRequestDto requestInfo,String chapterId){
        CrawlerRequestDto requestDto = new CrawlerRequestDto();
        BeanUtil.copyProperties(requestInfo,requestDto);
        requestDto.setChapterId(chapterId);
        requestDto.setUrl(url);
        requestDto.setType(CrawlerTypeEnum.CONTENT.getType());
        Request contentRequest = new Request(url);
        contentRequest.putExtra(RequestConst.REQUEST_INFO,requestDto);
        page.addTargetRequest(contentRequest);
    }

}

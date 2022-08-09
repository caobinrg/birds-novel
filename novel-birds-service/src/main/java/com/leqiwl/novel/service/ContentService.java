package com.leqiwl.novel.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.leqiwl.novel.common.util.EntityToDtoUtil;
import com.leqiwl.novel.domain.dto.ContentOutDto;
import com.leqiwl.novel.domain.entify.Chapter;
import com.leqiwl.novel.domain.entify.Content;
import com.leqiwl.novel.repository.ContentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: 飞鸟不过江
 * @Date: 2021/12/27 11:21
 * @Description:
 */
@Slf4j
@Service
public class ContentService {

    @Resource
    private ContentRepository contentRepository;

    @Resource
    private ChapterService chapterService;

    @Resource
    private JobRemoteService jobRemoteService;


    @Value("${contentError.textError}")
    private String contentTextError;

    @Value("${contentError.textLoad}")
    private String contentTextLoad;

    @Value("${contentError.nameError}")
    private String contentNameError;

    public Content save(Content content){
        return contentRepository.save(content);
    }

    public Content getByNovelAndChapterId(String novelId,String chapterId){
        Content content = contentRepository.getContentByNovelIdAndChapterId(novelId, chapterId);
        if(null == content){
            return new Content();
        }
        return content;
    }

    @Cacheable(cacheNames = "viewContent#2m", key = "#novelId+'-'+#chapterId")
    public ContentOutDto getViewContentInfo(String novelId,String chapterId) {
        List<Chapter> chapterList = chapterService.findByNovelId(novelId);
        String lastChapterId = "";
        String nextChapterId = "";
        int currentIndex = -1;
        Chapter chapter;
        if(CollectionUtil.isNotEmpty(chapterList)){
            int size = chapterList.size();
            for (int i = 0; i < size ; i++) {
                Chapter dbChapter = chapterList.get(i);
                if(dbChapter.getChapterId().equals(chapterId)){
                    currentIndex = i;
                    break;
                }
            }
            if(currentIndex > 0){
                lastChapterId = chapterList.get(currentIndex - 1).getChapterId();
            }
            if(currentIndex > -1 && size > (currentIndex + 1)){
                nextChapterId = chapterList.get(currentIndex + 1).getChapterId();
            }
        }
        ContentOutDto contentOutDto = new ContentOutDto();
        contentOutDto.setLastChapterId(lastChapterId);
        contentOutDto.setNextChapterId(nextChapterId);
        if(currentIndex > -1){
            // 匹配到信息
            chapter = chapterList.get(currentIndex);
            Content content = getByNovelAndChapterId(novelId, chapterId);
            if(null == content || StrUtil.isBlank(content.getChapterId()) ){
                //插队重新采集，重新获取
               if(jobRemoteService.jumpGetContent(chapter)){
                   content = getByNovelAndChapterId(novelId, chapterId);
               }
            }
            if(null != content && StrUtil.isNotBlank(content.getChapterId())){
                //找到章节内容
                contentOutDto = EntityToDtoUtil.parseDataWithUrl(content,contentOutDto);
                contentOutDto.setHasDataFlag(true);
                String contentText = content.getContentText();
                if(contentText.contains("重新刷新页面")){
                    jobRemoteService.jumpGetContentAsync(chapter);
                }
            }else{
                //未找到章节内容
                if(null != chapter && StrUtil.isNotBlank(chapter.getChapterName())){
                    contentOutDto.setName(chapter.getChapterName());
                    contentOutDto = EntityToDtoUtil.parseDataWithUrl(chapter,contentOutDto);
                }
                contentOutDto.setContentText(contentTextLoad);
            }
        }
        if(StrUtil.isBlank(contentOutDto.getName())){
            //未找到任何匹配信息
            contentOutDto.setName(contentNameError);
            contentOutDto.setContentText(contentTextError);
        }
        return contentOutDto;
    }



}

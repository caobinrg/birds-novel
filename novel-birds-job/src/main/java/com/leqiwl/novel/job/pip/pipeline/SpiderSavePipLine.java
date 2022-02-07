package com.leqiwl.novel.job.pip.pipeline;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.leqiwl.novel.domain.entify.Chapter;
import com.leqiwl.novel.domain.entify.Content;
import com.leqiwl.novel.domain.entify.Novel;
import com.leqiwl.novel.enums.CrawlerSaveTypeEnum;
import com.leqiwl.novel.service.ChapterService;
import com.leqiwl.novel.service.ContentService;
import com.leqiwl.novel.service.NovelService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: 飞鸟不过江
 * @Date: 2021/12/27 16:49
 * @Description: implements Pipeline 数据持久化
 */
@Slf4j
@Component
public class SpiderSavePipLine implements Pipeline {

    @Resource
    private NovelService novelService;

    @Resource
    private ChapterService chapterService;

    @Resource
    private ContentService contentService;

    @Value("${novelImagePath}")
    private String novelImagePath;

    @Value("${location.image}")
    private String baseImagePath;

    @SneakyThrows
    @Override
    public void process(ResultItems resultItems, Task task) {

        String imgUrl = resultItems.get(CrawlerSaveTypeEnum.IMG.getType().toString());
        String imgName = "";
        boolean downImage = false;
        if(StrUtil.isNotBlank(imgUrl)){
            try {
                imgName = resultItems.get(CrawlerSaveTypeEnum.IMG_PATH.getType().toString());
                if(!novelImagePath.endsWith("/")){
                    novelImagePath = novelImagePath + "/";
                }
                String path = ResourceUtils.getURL(novelImagePath + baseImagePath + imgName).getPath();
                HttpUtil.downloadFile(imgUrl, FileUtil.file(path));
                downImage = true;
            }catch (Exception e){
                log.info("image:{}持久化失败",imgName);
                log.error(e.getMessage(),e);
                downImage = false;
            }
        }
        Object novelInfo = resultItems.get(CrawlerSaveTypeEnum.DETAIL.getType().toString());
        if(null != novelInfo){
            Novel novel = (Novel) novelInfo;
            if(StrUtil.isNotBlank(novel.getNovelId())){
                if(StrUtil.isNotBlank(imgUrl) && downImage){
                    novel.setImagePath("/"+baseImagePath + imgName);
                }
                long saveStart = System.currentTimeMillis();
                novelService.save(novel);
                long saveEnd = System.currentTimeMillis();
                log.info("持久化novel---novel:{}持久化耗时:{}ms",novel.getNovelId(),(saveEnd-saveStart));
            }
        }
        List<Object> chapterInfo = resultItems.get(CrawlerSaveTypeEnum.CHAPTER.getType().toString());
        if(CollectionUtil.isNotEmpty(chapterInfo)){
            List<Chapter> collect = chapterInfo.stream().map(item -> (Chapter) item).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(collect)){
                String novelId = collect.get(0).getNovelId();
                long saveStart = System.currentTimeMillis();
                chapterService.save(collect,novelId);
                long saveEnd = System.currentTimeMillis();
                log.info("持久化chapters---novel:{},chapters持久化耗时:{}ms",novelId,(saveEnd-saveStart));
            }
        }
        Object contentInfo = resultItems.get(CrawlerSaveTypeEnum.CONTENT.getType().toString());
        if(null != contentInfo){
            Content content = (Content) contentInfo;
            if(StrUtil.isNotBlank(content.getChapterId())){
                long saveStart = System.currentTimeMillis();
                contentService.save(content);
                long saveEnd = System.currentTimeMillis();
                log.info("持久化content:---content:{}持久化耗时:{}ms",content.getChapterId(),(saveEnd-saveStart));
            }
        }
    }
}

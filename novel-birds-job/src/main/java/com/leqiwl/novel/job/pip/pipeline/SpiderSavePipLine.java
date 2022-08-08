package com.leqiwl.novel.job.pip.pipeline;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.leqiwl.novel.config.sysconst.RequestConst;
import com.leqiwl.novel.domain.dto.CrawlerRequestDto;
import com.leqiwl.novel.domain.entify.Chapter;
import com.leqiwl.novel.domain.entify.Content;
import com.leqiwl.novel.domain.entify.Novel;
import com.leqiwl.novel.enums.CrawlerSaveTypeEnum;
import com.leqiwl.novel.enums.CrawlerTypeEnum;
import com.leqiwl.novel.service.ChapterService;
import com.leqiwl.novel.service.ContentService;
import com.leqiwl.novel.service.NovelService;
import com.leqiwl.novel.service.TopicAndQueuePushService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import us.codecraft.webmagic.Request;
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

    @Resource
    private TopicAndQueuePushService topicAndQueuePushService;


    @Value("${novelImagePath}")
    private String novelImagePath;

    @Value("${location.image}")
    private String baseImagePath;

    @Value("${baiduPush.siteBaseUrl}")
    private String siteBaseURL;

    @SneakyThrows
    @Override
    public void process(ResultItems resultItems, Task task) {
        Request request = resultItems.getRequest();
        CrawlerRequestDto requestInfo = request.getExtra(RequestConst.REQUEST_INFO);
        if(null == requestInfo){
            return;
        }
        Integer type = requestInfo.getType();
        if(CrawlerTypeEnum.DETAIL.getType().equals(type)){
            //详情页
            saveDetail(resultItems);
        }
        if(CrawlerTypeEnum.CONTENT.getType().equals(type)){
            //内容页
            saveContent(resultItems);
        }
    }


    private void  saveDetail (ResultItems resultItems) throws InterruptedException {
        SaveImage saveImage = new SaveImage(resultItems).invoke();
        String imgUrl = saveImage.getImgUrl();
        String imgName = saveImage.getImgName();
        boolean downImage = saveImage.isDownImage();
        SaveNovel saveNovel = new SaveNovel(resultItems, imgUrl, imgName, downImage).invoke();
        String novelId = saveNovel.getNovelId();
        boolean novelIdClash = saveNovel.isNovelIdClash();
        if(StrUtil.isBlank(novelId)){
            return;
        }
        saveChapter(resultItems, novelId, novelIdClash);
    }

    private void saveChapter(ResultItems resultItems, String novelId, boolean novelIdClash) {
        if(novelIdClash){
            //id冲突，不保存章节数据
           return;
        }
        List<Object> chapterInfo = resultItems.get(CrawlerSaveTypeEnum.CHAPTER.getType().toString());
        if(CollectionUtil.isNotEmpty(chapterInfo)){
            List<Chapter> collect = chapterInfo.stream().map(item -> (Chapter) item).collect(Collectors.toList());
//            if(novelIdClash){
//                for (Chapter chapter : collect) {
//                    chapter.setNovelId(novelId);
//                }
//            }
            if(CollectionUtil.isNotEmpty(collect)){
                long saveStart = System.currentTimeMillis();
                chapterService.save(collect,novelId);
                long saveEnd = System.currentTimeMillis();
                log.info("持久化chapters---novel:{},chapters持久化耗时:{}ms",novelId,(saveEnd-saveStart));
            }
        }
    }


    private void  saveContent (ResultItems resultItems){
        Object contentInfo = resultItems.get(CrawlerSaveTypeEnum.CONTENT.getType().toString());
        if(null != contentInfo){
            Content content = (Content) contentInfo;
            if(StrUtil.isNotBlank(content.getChapterId())){
                long saveStart = System.currentTimeMillis();
                contentService.save(content);
                long saveEnd = System.currentTimeMillis();
                log.info("持久化content:---content:{}持久化耗时:{}ms",content.getChapterId(),(saveEnd-saveStart));
                topicAndQueuePushService.sendSaveUrl(siteBaseURL + content.getNovelId() + "/" + content.getChapterId() + ".html");
            }
        }
    }


    private class SaveImage {
        private ResultItems resultItems;
        private String imgUrl;
        private String imgName;
        private boolean downImage;

        public SaveImage(ResultItems resultItems) {
            this.resultItems = resultItems;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public String getImgName() {
            return imgName;
        }

        public boolean isDownImage() {
            return downImage;
        }

        public SaveImage invoke() {
            imgUrl = resultItems.get(CrawlerSaveTypeEnum.IMG.getType().toString());
            imgName = "";
            downImage = false;
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
                }
            }
            return this;
        }
    }

    private class SaveNovel {
        private ResultItems resultItems;
        private String imgUrl;
        private String imgName;
        private boolean downImage;
        private String novelId;
        private boolean novelIdClash;

        public SaveNovel(ResultItems resultItems, String imgUrl, String imgName, boolean downImage) {
            this.resultItems = resultItems;
            this.imgUrl = imgUrl;
            this.imgName = imgName;
            this.downImage = downImage;
        }

        public String getNovelId() {
            return novelId;
        }

        public boolean isNovelIdClash() {
            return novelIdClash;
        }

        public SaveNovel invoke() throws InterruptedException {
            Object novelInfo = resultItems.get(CrawlerSaveTypeEnum.DETAIL.getType().toString());
            novelId = "";
            novelIdClash = false;
            if(null != novelInfo){
                Novel novel = (Novel) novelInfo;
                if(StrUtil.isNotBlank(novel.getNovelId())){
                    novelId = novel.getNovelId();
                    if(StrUtil.isNotBlank(imgUrl) && downImage){
                        novel.setImagePath("/"+baseImagePath + imgName);
                    }
                    long saveStart = System.currentTimeMillis();
                    novel = novelService.save(novel);
                    if(!novelId.equals(novel.getNovelId())){
                        log.info("持久化novel---novel:{},id冲突，持久化失败",novel.getNovelId());
                        novelIdClash = true;
                    }
                    novelId = novel.getNovelId();
                    long saveEnd = System.currentTimeMillis();
                    log.info("持久化novel---novel:{}持久化耗时:{}ms",novel.getNovelId(),(saveEnd-saveStart));
                    //持久化完成后 推送url到队列，供后续进行 搜索引擎 推送
                    topicAndQueuePushService.sendSaveUrl(siteBaseURL + novelId + ".html");
                }
            }
            return this;
        }
    }
}

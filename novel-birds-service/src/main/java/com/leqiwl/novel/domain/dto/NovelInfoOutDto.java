package com.leqiwl.novel.domain.dto;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.leqiwl.novel.common.util.face.IDtoParseUrl;
import com.leqiwl.novel.config.WebMappingUrlConfig;
import com.leqiwl.novel.domain.entify.Chapter;
import com.leqiwl.novel.util.ImagePathCheckUtil;
import lombok.Data;
import java.util.Collections;
import java.util.List;

/**
 * @author: 飞鸟不过江
 * @Date: 2021/12/30 13:56
 * @Description:
 */
@Data
public class NovelInfoOutDto implements IDtoParseUrl {

    private String novelId;
    /**
     * 标识
     */
    private String idMark;

    /**
     * 小说名称
     */
    private String name;
    /**
     * 小说作者
     */
    private String author;
    /**
     * 小说图片
     */
    private String image;

    /**
     * 图片本地位置
     */
    private String imagePath;
    /**
     * 小说字数(万字)
     */
    private String wordNum;
    /**
     * 小说状态
     */
    private String updateStatus;
    /**
     * 小说评分
     */
    private String score;
    /**
     * 评分人数
     */
    private String scorePersonNum;
    /**
     * 更新标识
     */
    private String lastUpdateMark;

    /**
     * 小说类型
     */
    private String novelType;

    private String setType;
    /**
     * 小说标签，以逗号,分割
     */
    private String keywords;

    /**
     * 描述
     */
    private String description;

    private List<String> tags;
    /**
     * 小说简介
     */
    private String intro;

    /**
     * 章节列表(存储最新5章)
     */
    private List<Chapter> chapterList;

    private String novelUrl;


    public void setNovelType(String novelType) {
        this.novelType = novelType;
        if(StrUtil.isNotBlank(novelType)){
        this.tags = Collections.singletonList(novelType);
        }
    }
    public void setImage(String image) {
        if(StrUtil.isBlank(this.image)){
            this.image = image;
        }
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
        if(ImagePathCheckUtil.check(imagePath)){
          this.image = imagePath;
        }
    }

    public void setChapterList(List<Chapter> chapterList) {
        if(CollectionUtil.isNotEmpty(chapterList)){
            for (Chapter chapter : chapterList) {
                chapter.setChapterUrl(WebMappingUrlConfig.instance().getPage()
                        + "/" + novelId
                        + "/" + chapter.getChapterId()
                        + ".html");
            }
        }
        this.chapterList = chapterList;
    }

    @Override
    public void parseUrl() {
        if(StrUtil.isNotBlank(this.novelId)){
            this.novelUrl = WebMappingUrlConfig.instance().getPage() + "/" + this.novelId   + ".html";
        }
    }
}

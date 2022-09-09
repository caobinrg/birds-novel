package com.leqiwl.novel.domain.dto;

import cn.hutool.core.util.StrUtil;
import com.leqiwl.novel.common.util.face.IDtoParseUrl;
import com.leqiwl.novel.config.WebMappingUrlConfig;
import com.leqiwl.novel.util.ImagePathCheckUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Collections;
import java.util.List;

/**
 * @author: 飞鸟不过江
 * @Date: 2022/1/10 10:50
 * @Description: novel 统计对象
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class NovelConverOutDto implements IDtoParseUrl {

    @Id
    private String id;

    @Indexed(unique=true)
    private String novelId;

    /**
     * 标识
     */
    @Indexed(unique=true)
    private String idMark;

    /**
     * 小说名称
     */
    private String name;
    /**
     * 小说作者
     */
    @Indexed
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
     * 小说类型
     */
    private String novelType;


    private List<String> tags;

    /**
     * 小说标签，以逗号,分割
     */
    private String keywords;

    /**
     * 小说简介
     */
    private String intro;
    /**
     * 小说评分
     */
    private String score;
    /**
     * 评分人数
     */
    private String scorePersonNum;


    @Builder.Default
    private Long readNum = 0L;

    @Builder.Default
    private Long starNum = 0L;

    @Builder.Default
    private Long clickNum = 0L;

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
//    public void setImagePath(String imagePath) {
//        this.imagePath = imagePath;
//        if(ImagePathCheckUtil.check(imagePath)){
//            this.image = imagePath;
//        }
//    }

    @Override
    public void parseUrl() {
        if(StrUtil.isNotBlank(this.novelId)){
            this.novelUrl = WebMappingUrlConfig.instance().getPage() + "/" + this.novelId   + ".html";
        }
    }

}

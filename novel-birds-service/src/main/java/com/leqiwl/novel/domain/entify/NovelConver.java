package com.leqiwl.novel.domain.entify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author: 飞鸟不过江
 * @Date: 2022/1/10 10:50
 * @Description: novel 统计对象
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Document(collection = "novelConver")
@JsonIgnoreProperties(ignoreUnknown = true)
public class NovelConver {

    @Id
    private String id;

    @Indexed(unique=true,name = "novelConver_novelId")
    private String novelId;

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
     * 小说类型
     */
    private String novelType;

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

    @Indexed(name = "novelConver_readNum")
    @Builder.Default
    private Long readNum = 0L;

    @Indexed(name = "novelConver_starNum")
    @Builder.Default
    private Long starNum = 0L;

    @Indexed(name = "novelConver_clickNum")
    @Builder.Default
    private Long clickNum = 0L;

    private Date createTime;

    private Date updateTime;

    @Builder.Default
    private int isActive = 1;

}

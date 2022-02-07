package com.leqiwl.novel.domain.entify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author 飞鸟不过江
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Document(collection = "novel")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Novel implements Serializable {

    @Id
    private String id;

    @Indexed(unique=true,name = "novel_novelId")
    private String novelId;

    /**
     * 标识
     */
    @Indexed(unique=true,name = "novel_idMark")
    private String idMark;

    /**
     * 小说名称
     */
    private String name;
    /**
     * 小说作者
     */
    @Indexed(name = "novel_author")
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

    /**
     * 小说标签，以逗号,分割
     */
    private String keywords;

    /**
     * 描述
     */
    private String description;

    /**
     * 小说简介
     */
    private String intro;
    /**
     * 章节列表(存储最新5章)
     */
    private List<Chapter> chapterList;

    /**
     * 原始url
     */
    private String relUrl;

    /**
     * 规则id
     */
    private String ruleId;

    private Date createTime;

    private Date updateTime;

    @Builder.Default
    private int isActive = 1;

}
package com.leqiwl.novel.domain.entify.crawler;

import com.leqiwl.novel.enums.CrawlerTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2021/12/26 0026 19:31
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CrawlerDetailRule {

    /**
     * 小说名称
     */
    private String nameRule;
    /**
     * 小说作者
     */
    private String authorRule;

    /**
     * 小说图片
     */
    private String imageRule;

    /**
     * 是否保存到本地
     */
    @Builder.Default
    private boolean imageSave = false;
    /**
     * 小说字数(万字)
     */
    private String wordNumRule;
    /**
     * 小说状态
     */
    private String updateStatusRule;
    /**
     * 小说评分
     */
    private String scoreRule;
    /**
     * 评分人数
     */
    private String scorePersonNumRule;
    /**
     * 更新标识
     */
    private String lastUpdateMarkRule;

    /**
     * 小说类型
     */
    private String novelTypeRule;


    /**
     * 小说标签，以逗号,分割
     */
    private String keywordsRule;


    private List<String> keywordsReplaceRule;

    /**
     * 小说描述
     */
    private String descriptionRule;


    private List<String> descriptionReplaceRule;

    /**
     * 小说简介
     */
    private String introRule;

    private List<String> introReplaceRule;

    /**
     * 章节列表
     */
    private String chapterListRule;

    /**
     * 跳过数量
     */
    @Builder.Default
    private int chapterListSkipNoRule = 0;

    private String chapterNameRule;

    private String chapterUrlRule;

    /**
     * 基准站
     */
    @Builder.Default
    private Integer basicMark = 0;

    /**
     * 类型
     */
    @Builder.Default
    private Integer type = CrawlerTypeEnum.DETAIL.getType();
}

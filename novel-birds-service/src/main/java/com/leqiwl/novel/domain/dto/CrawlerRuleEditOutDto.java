package com.leqiwl.novel.domain.dto;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author: 飞鸟不过江
 * @Date: 2022/1/29 20:07
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrawlerRuleEditOutDto {


    private String ruleId;


    @NotBlank(message = "ruleName不能为空")
    private String ruleName;

    /**
     * 初始化完成后执行列表采集间隔时间（单位分钟）
     * 最小配置为 5分钟，小于5分钟按照默认5分钟计算
     */
    @Builder.Default
    private long initAfterInterval = 15;

    /** list */
    @NotBlank(message = "sourceUrl不能为空")
    private String sourceUrl;

    @Builder.Default
    private int pageStartRule = 1;

    @Builder.Default
    private int pageEndRule = 1;

    /**
     * 初始化后采集页数
     */
    @Builder.Default
    @NotNull(message = "sourceUrl不能为空")
    private Integer afterInitPageNo = 3;

    @NotBlank(message = "getUrlListRule不能为空")
    private String getUrlListRule;

    @NotBlank(message = "getUrlRule不能为空")
    private String getUrlRule;

    /** detail */

    /**
     * 小说名称
     */
    @NotBlank(message = "nameRule不能为空")
    private String nameRule;
    /**
     * 小说作者
     */
    @NotBlank(message = "authorRule不能为空")
    private String authorRule;

    /**
     * 小说图片
     */
    @NotBlank(message = "imageRule不能为空")
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
    @NotBlank(message = "lastUpdateMarkRule不能为空")
    private String lastUpdateMarkRule;

    /**
     * 小说类型
     */
    @NotBlank(message = "novelTypeRule不能为空")
    private String novelTypeRule;


    /**
     * 小说标签，以逗号,分割
     */
    @NotBlank(message = "keywordsRule不能为空")
    private String keywordsRule;


    private String keywordsReplaceRule;

    public void setKeywordsReplaceRule(List<String> setKeywordsReplaceRuleList) {
        if(CollectionUtil.isNotEmpty(setKeywordsReplaceRuleList)){
            keywordsReplaceRule = CollectionUtil.join(setKeywordsReplaceRuleList,",");
        }
    }

    /**
     * 小说描述
     */
    @NotBlank(message = "descriptionRule不能为空")
    private String descriptionRule;


    private String descriptionReplaceRule;

    public void setDescriptionReplaceRule(List<String> descriptionReplaceRuleList) {
        if(CollectionUtil.isNotEmpty(descriptionReplaceRuleList)){
            descriptionReplaceRule = CollectionUtil.join(descriptionReplaceRuleList,",");
        }
    }

    /**
     * 小说简介
     */
    @NotBlank(message = "introRule不能为空")
    private String introRule;

    private String introReplaceRule;

    public void setIntroReplaceRule(List<String> introReplaceRuleList) {
        if(CollectionUtil.isNotEmpty(introReplaceRuleList)){
            introReplaceRule = CollectionUtil.join(introReplaceRuleList,",");
        }
    }

    /**
     * 章节列表
     */
    @NotBlank(message = "chapterListRule不能为空")
    private String chapterListRule;

    /**
     * 跳过数量
     */
    @Builder.Default
    private int chapterListSkipNoRule = 0;

    @NotBlank(message = "chapterNameRule不能为空")
    private String chapterNameRule;

    @NotBlank(message = "chapterUrlRule不能为空")
    private String chapterUrlRule;



    /** content */

    @NotBlank(message = "contentNameRule不能为空")
    private String contentNameRule;

    @NotBlank(message = "contentTextRule不能为空")
    private String contentTextRule;

    private String contentOutStr;

    public void setContentOutStr(List<String> list) {
        if(CollectionUtil.isNotEmpty(list)){
            contentOutStr = CollectionUtil.join(list,",");
        }

    }

    private String contentOutLabelRule;

    public void setContentOutLabelRule(List<String> contentOutLabelRuleList) {
        if(CollectionUtil.isNotEmpty(contentOutLabelRuleList)){
            contentOutLabelRule = CollectionUtil.join(contentOutLabelRuleList,",");
        }
    }

}

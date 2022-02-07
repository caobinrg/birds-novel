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
public class CrawlerRuleEditInDto {


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


    private List<String> keywordsReplaceRule;

    public void setKeywordsReplaceRule(String setKeywordsReplaceRuleStr) {
        if(StrUtil.isNotBlank(setKeywordsReplaceRuleStr)){
            this.keywordsReplaceRule = CollectionUtil.newArrayList(setKeywordsReplaceRuleStr.split(","));
        }
    }

    /**
     * 小说描述
     */
    @NotBlank(message = "descriptionRule不能为空")
    private String descriptionRule;


    private List<String> descriptionReplaceRule;

    public void setDescriptionReplaceRule(String descriptionReplaceRuleStr) {
        if(StrUtil.isNotBlank(descriptionReplaceRuleStr)){
            this.descriptionReplaceRule = CollectionUtil.newArrayList(descriptionReplaceRuleStr.split(","));
        }
    }

    /**
     * 小说简介
     */
    @NotBlank(message = "introRule不能为空")
    private String introRule;

    private List<String> introReplaceRule;

    public void setIntroReplaceRule(String introReplaceRuleStr) {
        if(StrUtil.isNotBlank(introReplaceRuleStr)){
            this.introReplaceRule = CollectionUtil.newArrayList(introReplaceRuleStr.split(","));
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

    private List<String> contentOutStr;

    public void setContentOutStr(String str) {
        if(StrUtil.isNotBlank(str)){
            this.contentOutStr = CollectionUtil.newArrayList(str.split(","));
        }

    }



    private List<String> contentOutLabelRule;

    public void setContentOutLabelRule(String contentOutLabelRuleStr) {
        if(StrUtil.isNotBlank(contentOutLabelRuleStr)){
            this.contentOutLabelRule = CollectionUtil.newArrayList(contentOutLabelRuleStr.split(","));
        }
    }

}

package com.leqiwl.novel.domain.entify.crawler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2021/12/26 0026 19:25
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "crawlerRule")
public class CrawlerRule {

    @Id
    private String id;

    @Indexed(unique = true)
    private String ruleId;

    private String ruleName;

    private CrawlerListRule listRule;

    private CrawlerDetailRule detailRule;

    private CrawlerContentRule contentRule;

    /**
     * 初始化状态
     */
    @Builder.Default
    private Integer initStatus = 0;


    /**
     * 初始化完成后执行列表采集间隔时间（单位分钟）
     * 最小配置为 15分钟，小于15分钟按照默认15分钟计算
     */
    @Builder.Default
    private long initAfterInterval = 15;

    /**
     * 打开状态
     */
    @Builder.Default
    private int openStatus = 0;

    @Builder.Default
    private int isActive = 1;


    private Date createTime;

    private Date updateTime;
}

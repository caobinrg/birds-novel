package com.leqiwl.novel.domain.entify.crawler;

import com.leqiwl.novel.enums.CrawlerTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2021/12/26 0026 19:31
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CrawlerListRule {

    private String sourceUrl;

    @Builder.Default
    private int pageStartRule = 1;

    @Builder.Default
    private int pageEndRule = 1;

    /**
     * 初始化后采集页数
     */
    @Builder.Default
    private Integer afterInitPageNo = 3;


    private String getUrlListRule;

    private String getUrlRule;

    @Builder.Default
    private Integer type = CrawlerTypeEnum.LIST.getType();
}

package com.leqiwl.novel.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2021/12/26 0026 20:02
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CrawlerRequestDto implements Serializable {

    private String url;

    private String ruleId;

    private String baseUrl;

    @Builder.Default
    private int currentPageNo = 1;

    private String novelId;

    private String chapterId;

    private String novelName;

    private Integer type;

    private String  countDownSpace;

    private boolean jump;
}

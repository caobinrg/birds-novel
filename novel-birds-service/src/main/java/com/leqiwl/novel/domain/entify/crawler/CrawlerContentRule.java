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
public class CrawlerContentRule {

    private String nameRule;

    private String contentTextRule;

    private List<String> contentOutStr;

    private List<String> contentOutLabelRule;

    @Builder.Default
    private Integer type = CrawlerTypeEnum.CONTENT.getType();;

}

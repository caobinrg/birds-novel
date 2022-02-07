package com.leqiwl.novel.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @author: 飞鸟不过江
 * @Date: 2022/1/29 20:07
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class XpathTestInDto {

    @NotBlank(message = "sourceUrl不能为空")
    private String sourceUrl;

    @NotBlank(message = "xpathText不能为空")
    private String xpathText;

}

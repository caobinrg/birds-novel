package com.leqiwl.novel.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author: 飞鸟不过江
 * @Date: 2022/1/4 20:09
 * @Description:
 */
@Data
public class NovelSetInDto {

    @NotBlank(message = "novelId不能为空")
    private String novelId;

    @NotBlank(message = "setType不能为空")
    private String setType;
}

package com.leqiwl.novel.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author: 飞鸟不过江
 * @Date: 2021/12/30 13:56
 * @Description:
 */
@Data
public class NovelInfoInDto {


    @NotBlank(message = "novelId不能为空")
    private String novelId;

}

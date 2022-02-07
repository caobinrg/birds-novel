package com.leqiwl.novel.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2022/1/6 0006 23:26
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CookieReadHisDto {

    @NotBlank(message = "novelId不能为空")
    private String novelId;

    private String chapterId;

}

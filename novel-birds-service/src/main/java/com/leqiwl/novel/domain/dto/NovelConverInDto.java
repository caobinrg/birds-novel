package com.leqiwl.novel.domain.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * @author: 飞鸟不过江
 * @Date: 2022/1/10 10:50
 * @Description: novel 统计对象
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class NovelConverInDto {

    @NotNull(message = "rankType 不能为空")
    private Integer rankType;

}

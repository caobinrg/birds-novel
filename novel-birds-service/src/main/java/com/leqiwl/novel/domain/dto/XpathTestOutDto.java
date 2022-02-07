package com.leqiwl.novel.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: 飞鸟不过江
 * @Date: 2022/1/29 20:07
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class XpathTestOutDto {

    private String pageResult;

    private String textResult;

}

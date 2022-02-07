package com.leqiwl.novel.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * @author: 飞鸟不过江
 * @Date: 2021/12/30 13:56
 * @Description:
 */
@Data
public class NovelInfoByNameOutDto {

    private int pageNo = 1;

    private int pageSize = 5;

    private long total;

    private List<NovelInfoOutDto> data;

}

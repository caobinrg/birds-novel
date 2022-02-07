package com.leqiwl.novel.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author: 飞鸟不过江
 * @Date: 2021/12/30 13:56
 * @Description:
 */
@Data
public class NovelInfoByNameInDto {

    private int pageNo = 0;

    private int pageSize = 5;

    @NotBlank(message = "novelName不能为空")
    private String novelName;

    public void setPageNo(int pageNo) {
        if(pageNo>0){
            this.pageNo = pageNo - 1;
        }

    }
}

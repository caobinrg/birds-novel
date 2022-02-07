package com.leqiwl.novel.domain.dto;

import lombok.Data;

/**
 * @author: 飞鸟不过江
 * @Date: 2021/12/30 13:56
 * @Description:
 */
@Data
public class NovelInfoBySetTypeInDto {

    private int pageNo = 0;

    private int pageSize = 5;

    private String setType;

    public void setPageNo(int pageNo) {
        if(pageNo>0){
            this.pageNo = pageNo - 1;
        }
    }
}

package com.leqiwl.novel.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: 飞鸟不过江
 * @Date: 2022/1/11 11:20
 * @Description:
 */
@Data
public class NovelIdTopicDto implements Serializable {

    private String messageId;

    private String novelId;

}

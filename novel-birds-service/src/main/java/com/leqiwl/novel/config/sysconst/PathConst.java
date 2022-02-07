package com.leqiwl.novel.config.sysconst;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2022/1/5 0005 1:18
 */
@Component
public class PathConst {

    @Value("${novelImagePath}")
    private String containerNovelImagePath;

    public static String novelImagePath;

    @Value("${location.image}")
    private String containerBaseImagePath;

    public static String baseImagePath;

    @PostConstruct
    public void setData(){
        novelImagePath = containerNovelImagePath;
        baseImagePath = containerBaseImagePath;
    }

}

package com.leqiwl.novel.common.enums;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2021/12/31 0031 0:22
 */
public enum  ClientTypeEnum {

    PC(1,"explorer"),
    MOBILE(2,"mobile")
        ;
    ClientTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    private Integer type;
    private String name;

    public String getName() {
        return name;
    }

    public Integer getType() {
        return type;
    }

}

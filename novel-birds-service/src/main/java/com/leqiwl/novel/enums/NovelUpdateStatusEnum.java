package com.leqiwl.novel.enums;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2021/12/26 0026 19:45
 */
public enum NovelUpdateStatusEnum {

    UPDATE_ING(1, "连载"),
    COMPLETE(2, "完结"),
    ;
    private Integer status;
    private String name;

    NovelUpdateStatusEnum(int status, String name) {
        this.status = status;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Integer getStatus() {
        return status;
    }

    public static String getName(Integer type){
        NovelUpdateStatusEnum[] values = values();
        for (NovelUpdateStatusEnum novelUpdateStatusEnum : values) {
            if(novelUpdateStatusEnum.status.equals(type)){
                return novelUpdateStatusEnum.getName();
            }
        }
        return null;
    }

    public static Integer getType(Integer name){
        NovelUpdateStatusEnum[] values = values();
        for (NovelUpdateStatusEnum novelUpdateStatusEnum : values) {
            if(novelUpdateStatusEnum.name.equals(name)){
                return novelUpdateStatusEnum.status;
            }
        }
        return null;
    }

    public static NovelUpdateStatusEnum getByType(Integer type){
        NovelUpdateStatusEnum[] values = values();
        for (NovelUpdateStatusEnum novelUpdateStatusEnum : values) {
            if(novelUpdateStatusEnum.status.equals(type)){
                return novelUpdateStatusEnum;
            }
        }
        return null;
    }


}

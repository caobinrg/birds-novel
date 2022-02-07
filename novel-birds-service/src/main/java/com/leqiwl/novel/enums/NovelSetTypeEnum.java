package com.leqiwl.novel.enums;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2021/12/26 0026 19:45
 */
public enum NovelSetTypeEnum {

    SH(1, "书荒推荐"),
    ZZ(2, "站长推荐"),
    ;
    private Integer type;
    private String name;

    NovelSetTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Integer getType() {
        return type;
    }

    public static String getName(Integer type){
        NovelSetTypeEnum[] values = values();
        for (NovelSetTypeEnum crawlerTypeEnum : values) {
            if(crawlerTypeEnum.type.equals(type)){
                return crawlerTypeEnum.getName();
            }
        }
        return null;
    }

    public static Integer getType(Integer name){
        NovelSetTypeEnum[] values = values();
        for (NovelSetTypeEnum crawlerTypeEnum : values) {
            if(crawlerTypeEnum.name.equals(name)){
                return crawlerTypeEnum.type;
            }
        }
        return null;
    }

    public static NovelSetTypeEnum getByType(Integer type){
        NovelSetTypeEnum[] values = values();
        for (NovelSetTypeEnum crawlerTypeEnum : values) {
            if(crawlerTypeEnum.type.equals(type)){
                return crawlerTypeEnum;
            }
        }
        return null;
    }


}

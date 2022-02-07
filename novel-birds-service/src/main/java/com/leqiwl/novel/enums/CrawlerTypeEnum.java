package com.leqiwl.novel.enums;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2021/12/26 0026 19:45
 */
public enum CrawlerTypeEnum {

    LIST(1, "列表页"),
    DETAIL(2, "详情页"),
    CHAPTER(3, "章节页"),
    CONTENT(4, "内容页"),
    ;
    private Integer type;
    private String name;

    CrawlerTypeEnum(int type, String name) {
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
        CrawlerTypeEnum[] values = values();
        for (CrawlerTypeEnum crawlerTypeEnum : values) {
            if(crawlerTypeEnum.type.equals(type)){
                return crawlerTypeEnum.getName();
            }
        }
        return null;
    }

    public static Integer getType(Integer name){
        CrawlerTypeEnum[] values = values();
        for (CrawlerTypeEnum crawlerTypeEnum : values) {
            if(crawlerTypeEnum.name.equals(name)){
                return crawlerTypeEnum.type;
            }
        }
        return null;
    }

    public static CrawlerTypeEnum getByType(Integer type){
        CrawlerTypeEnum[] values = values();
        for (CrawlerTypeEnum crawlerTypeEnum : values) {
            if(crawlerTypeEnum.type.equals(type)){
                return crawlerTypeEnum;
            }
        }
        return null;
    }


}

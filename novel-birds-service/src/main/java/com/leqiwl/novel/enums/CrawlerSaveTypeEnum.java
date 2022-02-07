package com.leqiwl.novel.enums;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2021/12/26 0026 19:45
 */
public enum CrawlerSaveTypeEnum {
    DETAIL(2, "详情信息"),
    CHAPTER(3, "章节信息"),
    CONTENT(4, "内容信息"),
    IMG(5, "图片信息"),
    IMG_PATH(6, "图片信息"),
    ;
    private Integer type;
    private String name;

    CrawlerSaveTypeEnum(int type, String name) {
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
        CrawlerSaveTypeEnum[] values = values();
        for (CrawlerSaveTypeEnum crawlerSaveTypeEnum : values) {
            if(crawlerSaveTypeEnum.type.equals(type)){
                return crawlerSaveTypeEnum.getName();
            }
        }
        return null;
    }

    public static Integer getType(Integer name){
        CrawlerSaveTypeEnum[] values = values();
        for (CrawlerSaveTypeEnum crawlerSaveTypeEnum : values) {
            if(crawlerSaveTypeEnum.name.equals(name)){
                return crawlerSaveTypeEnum.type;
            }
        }
        return null;
    }

    public static CrawlerSaveTypeEnum getByType(Integer type){
        CrawlerSaveTypeEnum[] values = values();
        for (CrawlerSaveTypeEnum crawlerSaveTypeEnum : values) {
            if(crawlerSaveTypeEnum.type.equals(type)){
                return crawlerSaveTypeEnum;
            }
        }
        return null;
    }


}

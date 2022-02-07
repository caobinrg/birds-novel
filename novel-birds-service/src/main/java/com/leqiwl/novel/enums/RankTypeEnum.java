package com.leqiwl.novel.enums;

/**
 * @author: 飞鸟不过江
 * @Date: 2022/1/10 11:20
 * @Description:
 */
public enum  RankTypeEnum {
    Click(0,"点击榜","clickNum"),
    Read(1,"阅读榜","readNum"),
    STAR(2,"收藏榜","starNum"),
    ;
    private Integer type;
    private String name;
    private String column;

    RankTypeEnum(int type, String name,String column) {
        this.type = type;
        this.name = name;
        this.column = column;
    }

    public String getName() {
        return name;
    }

    public Integer getType() {
        return type;
    }

    public String getColumn(){
        return column;
    }

    public static String getName(Integer type){
        RankTypeEnum[] values = values();
        for (RankTypeEnum rankTypeEnum : values) {
            if(rankTypeEnum.type.equals(type)){
                return rankTypeEnum.getName();
            }
        }
        return null;
    }

    public static Integer getType(Integer name){
        RankTypeEnum[] values = values();
        for (RankTypeEnum rankTypeEnum : values) {
            if(rankTypeEnum.name.equals(name)){
                return rankTypeEnum.type;
            }
        }
        return null;
    }

    public static RankTypeEnum getByType(Integer type){
        RankTypeEnum[] values = values();
        for (RankTypeEnum rankTypeEnum : values) {
            if(rankTypeEnum.type.equals(type)){
                return rankTypeEnum;
            }
        }
        return null;
    }
}

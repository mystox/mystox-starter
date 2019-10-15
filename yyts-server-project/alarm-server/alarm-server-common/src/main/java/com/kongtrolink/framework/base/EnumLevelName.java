package com.kongtrolink.framework.base;

/**
 * @Auther: liudd
 * @Date: 2019/9/24 15:26
 * @Description:
 */
public enum  EnumLevelName {

    ONE("1", "一级告警"),
    TWO("2", "二级告警"),
    THE("3", "三级告警"),
    FOUR("4", "四级告警"),
    FIVE("5", "五级告警"),
    SIX("6", "六级告警"),
    SEV("7", "七级告警"),
    NIG("8", "八级告警");

    private String level;
    private String name;

    EnumLevelName(String level, String name) {
        this.level = level;
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public static EnumLevelName getByLevel(String level){
        for(EnumLevelName enumLevelName : EnumLevelName.values()){
            if(enumLevelName.getLevel().equals(level)){
                return enumLevelName;
            }
        }
        return null;
    }

    public static String getNameByLevel(String level){
        EnumLevelName enumLevelName = getByLevel(level);
        if(null == enumLevelName){
            return null;
        }
        return enumLevelName.getName();
    }
}

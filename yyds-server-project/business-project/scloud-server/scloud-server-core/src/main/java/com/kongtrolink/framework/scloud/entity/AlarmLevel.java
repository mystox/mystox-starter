package com.kongtrolink.framework.scloud.entity;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/14 14:28
 * @Description: 告警等级表，对应告警模块enterpriseLevel类
 */
public class AlarmLevel {
    private String id;
    private String code;                //告警规则编码
    private String name;
    private String enterpriseCode;
    private String serverCode;
    private Integer level;
    private String levelName;
    private String color;
    private Date updateTime;
    private FacadeView operator;                    //最后操作用户
    private String levelType;                       //企业告警等级类型（系统/手动）
    private String state;                           //启用，禁用

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public FacadeView getOperator() {
        return operator;
    }

    public void setOperator(FacadeView operator) {
        this.operator = operator;
    }

    public String getLevelType() {
        return levelType;
    }

    public void setLevelType(String levelType) {
        this.levelType = levelType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public static AlarmLevel getLevel(Integer level, List<AlarmLevel> alarmLevelList){
        if(null != alarmLevelList){
            for(AlarmLevel alarmLevel : alarmLevelList){
                if(alarmLevel.getLevel() <= level){
                    return alarmLevel;
                }
            }
        }
        AlarmLevel alarmLevel = new AlarmLevel();
        alarmLevel.setColor("#DB001B");
        String levelName = "八级告警";
        switch (level){
            case 8:
                levelName =  "八级告警";
                break;
            case 7:
                levelName =  "七级告警";
                break;
            case 6:
                levelName =  "六级告警";
                break;
            case 5:
                levelName =  "五级告警";
                break;
            case 4:
                levelName =  "四级告警";
                break;
            case 3:
                levelName =  "三级告警";
                break;
            case 2:
                levelName =  "二级告警";
                break;
            case 1:
                levelName =  "一级告警";
                break;
        }
        alarmLevel.setLevelName(levelName);
        return alarmLevel;
    }
}

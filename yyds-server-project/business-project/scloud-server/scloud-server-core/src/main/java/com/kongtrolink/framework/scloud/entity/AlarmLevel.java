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

    public static String getLevelName(Integer level, List<AlarmLevel> alarmLevelList){
        if(null != alarmLevelList){
            for(AlarmLevel alarmLevel : alarmLevelList){
                if(alarmLevel.getLevel() <= level){
                    return alarmLevel.getLevelName();
                }
            }
        }
        switch (level){
            case 8:
                return "八级告警";
            case 7:
                return "七级告警";
            case 6:
                return "六级告警";
            case 5:
                return "五级告警";
            case 4:
                return "四级告警";
            case 3:
                return "三级告警";
            case 2:
                return "二级告警";
            case 1:
                return "一级告警";
            default:
                return "无对应等级";
        }
    }
}

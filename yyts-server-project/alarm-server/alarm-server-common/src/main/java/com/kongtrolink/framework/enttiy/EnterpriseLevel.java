package com.kongtrolink.framework.enttiy;

import com.kongtrolink.framework.base.Contant;

import java.util.Date;

/**
 * @Auther: liudd
 * @Date: 2019/9/20 10:00
 * @Description:企业告警等级
 */
public class EnterpriseLevel {

    private String id;
    private String enterpriseCode;
    private String serverCode;
    private String level;
    private String levelName;
    private String color;
    private Date updateTime;
    private String defaultLevel = Contant.NO;       //默认告警等级

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getDefaultLevel() {
        return defaultLevel;
    }

    public void setDefaultLevel(String defaultLevel) {
        this.defaultLevel = defaultLevel;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}

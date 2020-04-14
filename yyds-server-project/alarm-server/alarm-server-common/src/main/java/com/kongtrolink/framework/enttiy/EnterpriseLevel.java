package com.kongtrolink.framework.enttiy;

import com.kongtrolink.framework.base.FacadeView;
import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/20 10:00
 * @Description:企业告警等级
 */
public class EnterpriseLevel {

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
    //前端展示字段
    private List<Integer> levels;        //等级列表
    private List<String> levelNames;     //等级名称列表
    private List<String> colors;        //颜色列表

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public List<Integer> getLevels() {
        return levels;
    }

    public void setLevels(List<Integer> levels) {
        this.levels = levels;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLevelNames() {
        return levelNames;
    }

    public void setLevelNames(List<String> levelNames) {
        this.levelNames = levelNames;
    }

    public List<String> getColors() {
        return colors;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }

    public FacadeView getOperator() {
        return operator;
    }

    public void setOperator(FacadeView operator) {
        this.operator = operator;
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

    public String getLevelType() {
        return levelType;
    }

    public void setLevelType(String levelType) {
        this.levelType = levelType;
    }

    @Override
    public String toString() {
        return "EnterpriseLevel{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", enterpriseCode='" + enterpriseCode + '\'' +
                ", serverCode='" + serverCode + '\'' +
                ", level=" + level +
                ", levelName='" + levelName + '\'' +
                ", color='" + color + '\'' +
                ", operator=" + operator +
                ", levelType='" + levelType + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}

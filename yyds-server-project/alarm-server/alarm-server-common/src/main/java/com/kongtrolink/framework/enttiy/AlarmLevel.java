package com.kongtrolink.framework.enttiy;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.FacadeView;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/11 14:59
 * @Description:
 */
public class AlarmLevel {

    private String id;                          //id
    private String entDevCode;                  //企业，设备等级
    private String enterpriseCode;
    private String enterpriseName;
    private String serverCode;
    private String serverName;
    private String deviceType;                  //设备类型名称
    private String deviceModel;                 //设备型号
    private String sourceLevel;                 //源等级
    private String targetLevel;                 //目标等级，一条自定义只包含一个目标等级，否则不好判定目标等级重复定义
    private String targetLevelName;             //目标等级名称
    private String color;                       //告警颜色，在确定目标等级时，确定颜色
    private String generate = Contant.SYSTEM;   //产生类型
    private Date updateTime;                    //创建时间
    private FacadeView operator;                //操作人员

    //前端展示列表
    private List<String> sourceLevelList;
    private List<String> targetLevelList;
    private List<String> targetLevelNameList;
    private List<String> colorList;

    public FacadeView getOperator() {
        return operator;
    }

    public void setOperator(FacadeView operator) {
        this.operator = operator;
    }

    public List<String> getSourceLevelList() {
        return sourceLevelList;
    }

    public void setSourceLevelList(List<String> sourceLevelList) {
        this.sourceLevelList = sourceLevelList;
    }

    public List<String> getTargetLevelList() {
        return targetLevelList;
    }

    public void setTargetLevelList(List<String> targetLevelList) {
        this.targetLevelList = targetLevelList;
    }

    public List<String> getTargetLevelNameList() {
        return targetLevelNameList;
    }

    public void setTargetLevelNameList(List<String> targetLevelNameList) {
        this.targetLevelNameList = targetLevelNameList;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public List<String> getColorList() {
        return colorList;
    }

    public void setColorList(List<String> colorList) {
        this.colorList = colorList;
    }

    public String getEntDevCode() {
        return entDevCode;
    }

    public void setEntDevCode(String entDevCode) {
        this.entDevCode = entDevCode;
    }

    public void initEntDevCode(){
        this.entDevCode = enterpriseCode + serverCode + deviceType + deviceModel;
    }

    public AlarmLevel() {
    }

    public AlarmLevel(String enterpriseCode, String serverCode, String deviceType, String deviceModel) {
        this.enterpriseCode = enterpriseCode;
        this.serverCode = serverCode;
        this.deviceType = deviceType;
        this.deviceModel = deviceModel;
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

    public String getGenerate() {
        return generate;
    }

    public void setGenerate(String generate) {
        this.generate = generate;
    }

    public String getTargetLevelName() {
        return targetLevelName;
    }

    public void setTargetLevelName(String targetLevelName) {
        this.targetLevelName = targetLevelName;
    }

    public String getSourceLevel() {
        return sourceLevel;
    }

    public void setSourceLevel(String sourceLevel) {
        this.sourceLevel = sourceLevel;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getTargetLevel() {
        return targetLevel;
    }

    public void setTargetLevel(String targetLevel) {
        this.targetLevel = targetLevel;
    }
}

package com.kongtrolink.framework.query;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.Paging;
import com.kongtrolink.framework.enttiy.AlarmLevel;

import java.util.Date;

/**
 * @Auther: liudd
 * @Date: 2019/9/11 15:06
 * @Description:
 */
public class AlarmLevelQuery extends Paging {

    private String id;                 //id
    private String enterpriseCode;
    private String enterpriseName;
    private String serverCode;
    private String serverName;
    private String deviceType;              //设备类型名称
    private String deviceModel;              //设备型号
    private Integer sourceLevel;     //源等级
    private Integer targetLevel;    //目标等级
    private String targetLevelName;         //目标等级名称
    private String color;           //目标等级对应的颜色
    private Date beginTime;
    private Date endTime;
    private String generate;                //产生类型
    private String entDevCode;                  //企业，设备等级
    private String operatorName;        //操作用户姓名

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
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

    public String getEntDevCode() {
        return entDevCode;
    }

    public void setEntDevCode(String entDevCode) {
        this.entDevCode = entDevCode;
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

    public static AlarmLevelQuery entity2Query(AlarmLevel alarmLevel){
        if(null == alarmLevel){
            return null;
        }
        AlarmLevelQuery levelQuery = new AlarmLevelQuery();
        levelQuery.setEnterpriseCode(alarmLevel.getEnterpriseCode());
        levelQuery.setServerCode(alarmLevel.getServerCode());
        levelQuery.setDeviceType(alarmLevel.getDeviceType());
        levelQuery.setDeviceModel(alarmLevel.getDeviceModel());
        levelQuery.setSourceLevel(alarmLevel.getSourceLevel());
        levelQuery.setTargetLevel(alarmLevel.getTargetLevel());
        levelQuery.setTargetLevelName(alarmLevel.getTargetLevelName());
        return levelQuery;
    }

    public Integer getSourceLevel() {
        return sourceLevel;
    }

    public void setSourceLevel(Integer sourceLevel) {
        this.sourceLevel = sourceLevel;
    }

    public Integer getTargetLevel() {
        return targetLevel;
    }

    public void setTargetLevel(Integer targetLevel) {
        this.targetLevel = targetLevel;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

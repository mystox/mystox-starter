package com.kongtrolink.query;

import com.kongtrolink.base.Paging;
import com.kongtrolink.enttiy.AlarmLevel;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/11 15:06
 * @Description:
 */
public class AlarmLevelQuery extends Paging {

    private String id;                 //id
    private String uniqueCode;      //所属企业
    private String service;
    private String deviceType;
    private String deviceName;
    private String sourceLevel;     //源等级
    private String targetLevel;    //目标等级
    private String color;           //目标等级对应的颜色
    private Date beginTime;
    private Date endTime;

    public static AlarmLevelQuery entity2Query(AlarmLevel alarmLevel){
        if(null == alarmLevel){
            return null;
        }
        AlarmLevelQuery levelQuery = new AlarmLevelQuery();
        levelQuery.setUniqueCode(alarmLevel.getUniqueCode());
        levelQuery.setService(alarmLevel.getService());
        levelQuery.setDeviceType(alarmLevel.getDeviceType());
        levelQuery.setDeviceName(alarmLevel.getDeviceName());
        levelQuery.setSourceLevel(alarmLevel.getSourceLevel());
        levelQuery.setTargetLevel(alarmLevel.getTargetLevel());
        return levelQuery;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSourceLevel() {
        return sourceLevel;
    }

    public void setSourceLevel(String sourceLevel) {
        this.sourceLevel = sourceLevel;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
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

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public String getTargetLevel() {
        return targetLevel;
    }

    public void setTargetLevel(String targetLevel) {
        this.targetLevel = targetLevel;
    }
}

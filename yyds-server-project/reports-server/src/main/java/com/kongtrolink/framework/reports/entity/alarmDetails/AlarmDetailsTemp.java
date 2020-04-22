package com.kongtrolink.framework.reports.entity.alarmDetails;

import java.util.Date;

/**
 * \* @Author: mystox
 * \* Date: 2020/3/14 17:40
 * \* Description:
 * \
 */
public class AlarmDetailsTemp {
    private String id;
    private Integer year;
    private Integer month;
    private Date tempDate;
    private String province; //省
    private String municipality; //市
    private String county; //县|区
    private String stationName; //站点名称
    private String operationState; //站点状态
    private String stationId; //站点id|站点编号
    private String stationType;//站点类型
    private String alarmLevel; //告警等级
    private String alarmName; //告警名称
    private String alarmState; //告警状态： 历史告警
    private Date startTime; //告警开始时间
    private Date recoveryTime; //恢复时间
    private Double duration; //时长（分钟）
    private String fsuManufactory; //fsu 厂家
    private Boolean deleteFlag = false;


    public String getFsuManufactory() {
        return fsuManufactory;
    }

    public void setFsuManufactory(String fsuManufactory) {
        this.fsuManufactory = fsuManufactory;
    }

    public Boolean getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Date getTempDate() {
        return tempDate;
    }

    public void setTempDate(Date tempDate) {
        this.tempDate = tempDate;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getOperationState() {
        return operationState;
    }

    public void setOperationState(String operationState) {
        this.operationState = operationState;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getStationType() {
        return stationType;
    }

    public void setStationType(String stationType) {
        this.stationType = stationType;
    }

    public String getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(String alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public String getAlarmState() {
        return alarmState;
    }

    public void setAlarmState(String alarmState) {
        this.alarmState = alarmState;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getRecoveryTime() {
        return recoveryTime;
    }

    public void setRecoveryTime(Date recoveryTime) {
        this.recoveryTime = recoveryTime;
    }
}
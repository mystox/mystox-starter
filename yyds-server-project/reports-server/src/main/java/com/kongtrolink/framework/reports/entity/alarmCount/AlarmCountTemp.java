package com.kongtrolink.framework.reports.entity.alarmCount;

import java.util.Date;

/**
 * \* @Author: mystox
 * \* Date: 2020/2/23 19:19
 * \* Description:
 * \
 */
public class AlarmCountTemp {
    private String id;
    private Integer year;
    private Integer month;
    private Date tempDate;
    private String province; //省
    private String municipality; //市
    private String county; //县|区
    private String stationName; //站点名称
    private String stationType;//站点类型
    private String alarmLevel;
    private String alarmState; //告警状态： 历史告警
    private Long alarmCount; //告警总数
    private Long alarmRecoveryCount; //告警恢复数
    private Boolean deleteFlag = false;



    public Date getTempDate() {
        return tempDate;
    }

    public void setTempDate(Date tempDate) {
        this.tempDate = tempDate;
    }

    public Integer getYear() {
        return year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Boolean getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getAlarmState() {
        return alarmState;
    }

    public void setAlarmState(String alarmState) {
        this.alarmState = alarmState;
    }

    public Long getAlarmCount() {
        return alarmCount;
    }

    public void setAlarmCount(Long alarmCount) {
        this.alarmCount = alarmCount;
    }

    public Long getAlarmRecoveryCount() {
        return alarmRecoveryCount;
    }

    public void setAlarmRecoveryCount(Long alarmRecoveryCount) {
        this.alarmRecoveryCount = alarmRecoveryCount;
    }
}
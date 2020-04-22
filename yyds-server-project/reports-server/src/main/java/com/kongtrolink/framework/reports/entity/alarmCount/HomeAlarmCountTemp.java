package com.kongtrolink.framework.reports.entity.alarmCount;

import java.util.Date;

/**
 * \* @Author: Mag
 * \* Date: 2020年4月8日 09:42:03
 * \* Description:
 * \ 首页需求：根据站点 按天统计历史告警数量
 */
public class HomeAlarmCountTemp {

    private String id;
    private String time; //统计日期
    private long tempDate;//统计日期
    private String province; //省
    private String municipality; //市
    private String county; //县|区
    private String stationName; //站点名称
    private String operationState; //站点状态
    private String stationId; //站点id
    private String stationType;//站点类型
    private String alarmLevel; //告警等级
    private Long alarmCount; //告警总数

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getTempDate() {
        return tempDate;
    }

    public void setTempDate(long tempDate) {
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

    public Long getAlarmCount() {
        return alarmCount;
    }

    public void setAlarmCount(Long alarmCount) {
        this.alarmCount = alarmCount;
    }
}
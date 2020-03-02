package com.kongtrolink.framework.reports.entity.alarmCategory;

import java.util.Date;

public class AlarmCategoryTemp {

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
    private String alarmState; //告警状态： 历史告警、实时告警
    private Long alarmCount; //告警总数
    private String fsuManufactory; //生产厂家


    private Long fsuOffline; //fsu 离线告警
    private Long smokeSensation; // 烟感
    private Long sensirion; // 温湿度
    private Long switchPower; // 开关电源
    private Long battery; // 蓄电池
    private Long infrared ; // 红外设备
    private Long gateMagnetism; // 门磁
    private Long waterImmersion; // 水浸
    private Long airConditioning; // 空调

    private Boolean deleteFlag = false;


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

    public String getFsuManufactory() {
        return fsuManufactory;
    }

    public void setFsuManufactory(String fsuManufactory) {
        this.fsuManufactory = fsuManufactory;
    }

    public Long getFsuOffline() {
        return fsuOffline;
    }

    public void setFsuOffline(Long fsuOffline) {
        this.fsuOffline = fsuOffline;
    }

    public Long getSmokeSensation() {
        return smokeSensation;
    }

    public void setSmokeSensation(Long smokeSensation) {
        this.smokeSensation = smokeSensation;
    }

    public Long getSensirion() {
        return sensirion;
    }

    public void setSensirion(Long sensirion) {
        this.sensirion = sensirion;
    }

    public Long getSwitchPower() {
        return switchPower;
    }

    public void setSwitchPower(Long switchPower) {
        this.switchPower = switchPower;
    }

    public Long getBattery() {
        return battery;
    }

    public void setBattery(Long battery) {
        this.battery = battery;
    }

    public Long getInfrared() {
        return infrared;
    }

    public void setInfrared(Long infrared) {
        this.infrared = infrared;
    }

    public Long getGateMagnetism() {
        return gateMagnetism;
    }

    public void setGateMagnetism(Long gateMagnetism) {
        this.gateMagnetism = gateMagnetism;
    }

    public Long getWaterImmersion() {
        return waterImmersion;
    }

    public void setWaterImmersion(Long waterImmersion) {
        this.waterImmersion = waterImmersion;
    }

    public Long getAirConditioning() {
        return airConditioning;
    }

    public void setAirConditioning(Long airConditioning) {
        this.airConditioning = airConditioning;
    }

    public Boolean getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
    }
}
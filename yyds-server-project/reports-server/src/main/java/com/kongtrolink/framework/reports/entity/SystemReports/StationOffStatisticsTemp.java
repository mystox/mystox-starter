package com.kongtrolink.framework.reports.entity.SystemReports;

import java.util.Date;

/**
 * \* @Author: mystox
 * \* Date: 2020/3/3 10:52
 * \* Description:
 * \
 */
public class StationOffStatisticsTemp {
    private String id;
    private Integer year;
    private Integer month;
    private Date tempDate;
    private String province; //省
    private String municipality; //市
    private String county; //县|区
    private String stationName; //站点名称
    private String stationId; //站点id 站点编号
    private String stationType;//站点类型
    private String operationState; //站点状态
    private String fsuManufactory; //fsu 厂家
    private Double sumDuration; //停电时长
    private Integer times; //停电次数
    private Double avgDuration;//平均站点停电时长（分钟）


    private Boolean deleteFlag = false;

    public Boolean getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
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

    public String getFsuManufactory() {
        return fsuManufactory;
    }

    public void setFsuManufactory(String fsuManufactory) {
        this.fsuManufactory = fsuManufactory;
    }

    public Double getSumDuration() {
        return sumDuration;
    }

    public void setSumDuration(Double sumDuration) {
        this.sumDuration = sumDuration;
    }

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    public Double getAvgDuration() {
        return avgDuration;
    }

    public void setAvgDuration(Double avgDuration) {
        this.avgDuration = avgDuration;
    }
}
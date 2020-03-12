package com.kongtrolink.framework.reports.entity.SystemReports;

import java.util.Date;

/**
 * \* @Author: mystox
 * \* Date: 2020/3/5 15:49
 * \* Description:
 * \
 */
public class StationBreakTemp {
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
//    private Integer stationCount; //站点总数
//    private Integer maintenanceCount; //交维站点数
    private Double avgDay; //日普通站平均断站时长(分钟)
    private Double avgMonth; //月累计普通站平均断站时长(分钟)

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
//    public Integer getStationCount() {
//        return stationCount;
//    }
//
//    public void setStationCount(Integer stationCount) {
//        this.stationCount = stationCount;
//    }
//
//    public Integer getMaintenanceCount() {
//        return maintenanceCount;
//    }
//
//    public void setMaintenanceCount(Integer maintenanceCount) {
//        this.maintenanceCount = maintenanceCount;
//    }

    public Double getAvgDay() {
        return avgDay;
    }

    public void setAvgDay(Double avgDay) {
        this.avgDay = avgDay;
    }

    public Double getAvgMonth() {
        return avgMonth;
    }

    public void setAvgMonth(Double avgMonth) {
        this.avgMonth = avgMonth;
    }
}
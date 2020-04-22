package com.kongtrolink.framework.reports.entity.SystemReports;

import java.util.Date;

/**
 * \* @Author: mystox
 * \* Date: 2020/3/5 16:14
 * \* Description:
 * \
 */
public class ElectricCountTemp {
    private String id;
    private Integer year;
    private Integer month;
    private Date tempDate;
    private String province; //省
    private String municipality; //市
    private String county; //县|区
    private String stationId; //站点编码
    private String stationName; //站点名称
    private String stationType;//站点类型
    private String fsuManufactory; //fsu 厂家
    private String operationState; //站点状态
    private Date startTime;
    private Date endTime;
    private Double sMobileElecCount;
    private Double sUnicomElecCount;
    private Double sTelecomElecCount;
    private Double eMobileElecCount;
    private Double eUnicomElecCount;
    private Double eTelecomElecCount;


    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getFsuManufactory() {
        return fsuManufactory;
    }

    public void setFsuManufactory(String fsuManufactory) {
        this.fsuManufactory = fsuManufactory;
    }

    public String getOperationState() {
        return operationState;
    }

    public void setOperationState(String operationState) {
        this.operationState = operationState;
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Double getsMobileElecCount() {
        return sMobileElecCount;
    }

    public void setsMobileElecCount(Double sMobileElecCount) {
        this.sMobileElecCount = sMobileElecCount;
    }

    public Double getsUnicomElecCount() {
        return sUnicomElecCount;
    }

    public void setsUnicomElecCount(Double sUnicomElecCount) {
        this.sUnicomElecCount = sUnicomElecCount;
    }

    public Double getsTelecomElecCount() {
        return sTelecomElecCount;
    }

    public void setsTelecomElecCount(Double sTelecomElecCount) {
        this.sTelecomElecCount = sTelecomElecCount;
    }

    public Double geteMobileElecCount() {
        return eMobileElecCount;
    }

    public void seteMobileElecCount(Double eMobileElecCount) {
        this.eMobileElecCount = eMobileElecCount;
    }

    public Double geteUnicomElecCount() {
        return eUnicomElecCount;
    }

    public void seteUnicomElecCount(Double eUnicomElecCount) {
        this.eUnicomElecCount = eUnicomElecCount;
    }

    public Double geteTelecomElecCount() {
        return eTelecomElecCount;
    }

    public void seteTelecomElecCount(Double eTelecomElecCount) {
        this.eTelecomElecCount = eTelecomElecCount;
    }
}
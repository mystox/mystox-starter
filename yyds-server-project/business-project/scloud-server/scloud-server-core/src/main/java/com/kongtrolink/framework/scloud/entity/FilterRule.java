package com.kongtrolink.framework.scloud.entity;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/3/5 14:48
 * @Description:
 */
public class FilterRule {

    private String id;
    private String name;
    private FacadeView creator;
    private String remarks;
    private Date updateTime;

    private boolean siteBased; // 条件是否基于站点
    private List<String> siteId;
    private List<String> tierCode;
    private String alarmName;
    private List<String> alarmLevel;
    private List<String> deviceTypeCode;
    private String reportStartTime;
    private String reportEndTime;
    private String recoverStartTime;
    private String recoverEndTime;
    private Boolean state;

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FacadeView getCreator() {
        return creator;
    }

    public void setCreator(FacadeView creator) {
        this.creator = creator;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isSiteBased() {
        return siteBased;
    }

    public void setSiteBased(boolean siteBased) {
        this.siteBased = siteBased;
    }

    public List<String> getSiteId() {
        return siteId;
    }

    public void setSiteId(List<String> siteId) {
        this.siteId = siteId;
    }

    public List<String> getTierCode() {
        return tierCode;
    }

    public void setTierCode(List<String> tierCode) {
        this.tierCode = tierCode;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public List<String> getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(List<String> alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public List<String> getDeviceTypeCode() {
        return deviceTypeCode;
    }

    public void setDeviceTypeCode(List<String> deviceTypeCode) {
        this.deviceTypeCode = deviceTypeCode;
    }

    public String getReportStartTime() {
        return reportStartTime;
    }

    public void setReportStartTime(String reportStartTime) {
        this.reportStartTime = reportStartTime;
    }

    public String getReportEndTime() {
        return reportEndTime;
    }

    public void setReportEndTime(String reportEndTime) {
        this.reportEndTime = reportEndTime;
    }

    public String getRecoverStartTime() {
        return recoverStartTime;
    }

    public void setRecoverStartTime(String recoverStartTime) {
        this.recoverStartTime = recoverStartTime;
    }

    public String getRecoverEndTime() {
        return recoverEndTime;
    }

    public void setRecoverEndTime(String recoverEndTime) {
        this.recoverEndTime = recoverEndTime;
    }
}

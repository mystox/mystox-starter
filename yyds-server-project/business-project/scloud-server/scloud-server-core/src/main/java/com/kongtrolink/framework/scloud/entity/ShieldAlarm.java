package com.kongtrolink.framework.scloud.entity;

import java.util.Date;

/**
 * @Auther: liudd
 * @Date: 2020/3/4 15:33
 * @Description: 被屏蔽的告警
 */
public class ShieldAlarm {
    private String id;
    private String ruleId;
    private String alarmId;
    private Integer alarmLevel;
    private String signalId;
    private String deviceCode;
    private Date treport;
    //前端展示数据
    private String signalName;
    private String deviceName;
    private String siteName;
    private String siteAddress;

    public String getSignalName() {
        return signalName;
    }

    public void setSignalName(String signalName) {
        this.signalName = signalName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteAddress() {
        return siteAddress;
    }

    public void setSiteAddress(String siteAddress) {
        this.siteAddress = siteAddress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getSignalId() {
        return signalId;
    }

    public void setSignalId(String signalId) {
        this.signalId = signalId;
    }

    public Integer getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(Integer alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public Date getTreport() {
        return treport;
    }

    public void setTreport(Date treport) {
        this.treport = treport;
    }
}

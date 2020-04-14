package com.kongtrolink.framework.scloud.entity;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/13 13:48
 * @Description: 告警重定义规则
 */
public class RedefineRule {

    private String id;
    private String name;
    private List<String> siteCodeList;
    private String deviceType;			//设备类型
    private List<String> cntbIdList;
    private Integer alarmLevel;
    private String reason;
    private FacadeView creator;
    private Date updateTime;
    private boolean enabled = false;
    private List<String> siteInfoList;
    private List<String> signalInfoList;

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

    public List<String> getSiteCodeList() {
        return siteCodeList;
    }

    public void setSiteCodeList(List<String> siteCodeList) {
        this.siteCodeList = siteCodeList;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public List<String> getCntbIdList() {
        return cntbIdList;
    }

    public void setCntbIdList(List<String> cntbIdList) {
        this.cntbIdList = cntbIdList;
    }

    public Integer getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(Integer alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public FacadeView getCreator() {
        return creator;
    }

    public void setCreator(FacadeView creator) {
        this.creator = creator;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getSiteInfoList() {
        return siteInfoList;
    }

    public void setSiteInfoList(List<String> siteInfoList) {
        this.siteInfoList = siteInfoList;
    }

    public List<String> getSignalInfoList() {
        return signalInfoList;
    }

    public void setSignalInfoList(List<String> signalInfoList) {
        this.signalInfoList = signalInfoList;
    }
}

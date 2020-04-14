package com.kongtrolink.framework.scloud.entity.model;

import java.io.Serializable;

/**
 * 综合机房-历史告警统计表前端显示模型
 * Created by Eric on 2018/11/12.
 */
public class RoomHistoryAlarmListModel implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 4783590471127124904L;
    private String alarmNo; //告警序列号
    private String alarmName;
    private String level;
    private String value;
    private String siteTierString;  //站点层级
    private String siteName;
    private String deviceName;
    private String reportTime;  //开始时间
    private String recoverTime; //恢复时间
    private String checkState;  //确认状态

    public String getAlarmNo() {
        return alarmNo;
    }

    public void setAlarmNo(String alarmNo) {
        this.alarmNo = alarmNo;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSiteTierString() {
        return siteTierString;
    }

    public void setSiteTierString(String siteTierString) {
        this.siteTierString = siteTierString;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getReportTime() {
        return reportTime;
    }

    public void setReportTime(String reportTime) {
        this.reportTime = reportTime;
    }

    public String getRecoverTime() {
        return recoverTime;
    }

    public void setRecoverTime(String recoverTime) {
        this.recoverTime = recoverTime;
    }

    public String getCheckState() {
        return checkState;
    }

    public void setCheckState(String checkState) {
        this.checkState = checkState;
    }
}

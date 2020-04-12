package com.kongtrolink.framework.scloud.query;

/**
 *
 * Created by Eric on 2018/11/12.
 */
public class RoomHistoryAlarmQuery extends Paging {
    /**
     *
     */
    private static final long serialVersionUID = 61924235997663038L;
    private int siteId;
    private String alarmLevel;  // 告警级别
    private String deviceTypeCode;// 设备类型编码
    private Long startTime;
    private Long endTime;

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public String getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(String alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public String getDeviceTypeCode() {
        return deviceTypeCode;
    }

    public void setDeviceTypeCode(String deviceTypeCode) {
        this.deviceTypeCode = deviceTypeCode;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }
}

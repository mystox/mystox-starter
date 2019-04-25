package com.kongtrolink.framework.execute.module.model;

/**
 * @author fengw
 * 内部服务上报告警信息
 * 新建文件 2019-4-22 18:49:57
 */
public class RedisAlarm {
    private String serialNo;
    private String id;
    private String fsuId;
    private String deviceId;
    private String alarmTime;
    private int alarmLevel;
    private String alarmFlag;
    private String alarmDesc;

    private String startTime;
    private String endTime;
    private String value;
    private boolean highFrequency;
    private long lastReportTime;
    private boolean startReported;
    private boolean endReported;
    private int reportCount;

    public static final String BEGIN = "BEGIN";
    public static final String END = "END";

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFsuId() {
        return fsuId;
    }

    public void setFsuId(String fsuId) {
        this.fsuId = fsuId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAlarmTime() {
        if (this.alarmFlag == BEGIN) {
            return startTime;
        } else if (this.alarmFlag == END) {
            return endTime;
        } else {
            return "";
        }
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public int getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(int alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public String getAlarmFlag() {
        return alarmFlag;
    }

    public void setAlarmFlag(String alarmFlag) {
        this.alarmFlag = alarmFlag;
    }

    public void setAlarmFlag(boolean alarmFlag) {
        if (alarmFlag) {
            this.alarmFlag = BEGIN;
        } else {
            this.alarmFlag = END;
        }
    }

    public String getAlarmDesc() {
        return alarmDesc;
    }

    public void setAlarmDesc(String alarmDesc) {
        this.alarmDesc = alarmDesc;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isHighFrequency() {
        return highFrequency;
    }

    public void setHighFrequency(boolean highFrequency) {
        this.highFrequency = highFrequency;
    }

    public long getLastReportTime() {
        return lastReportTime;
    }

    public void setLastReportTime(long lastReportTime) {
        this.lastReportTime = lastReportTime;
    }

    public boolean isStartReported() {
        return startReported;
    }

    public void setStartReported(boolean startReported) {
        this.startReported = startReported;
    }

    public boolean isEndReported() {
        return endReported;
    }

    public void setEndReported(boolean endReported) {
        this.endReported = endReported;
    }

    public int getReportCount() {
        return reportCount;
    }

    public void setReportCount(int reportCount) {
        this.reportCount = reportCount;
    }

    public String getCompleteDesc() {
        return alarmDesc + "(触发值：" + value + ")" + (highFrequency ? "(高频)" : "");
    }
}

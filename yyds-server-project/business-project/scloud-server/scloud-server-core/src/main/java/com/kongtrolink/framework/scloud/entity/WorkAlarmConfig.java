package com.kongtrolink.framework.scloud.entity;

import java.util.Date;

/**
 * 告警工单配置记录表
 * 系统需要任务定时派单，延迟接单提醒，延迟回单提醒。如果直接扫描告警表，数据量太大影响性能。
 * 所以在收到告警上报时，将相关信息插入到一张新表中，任务定时扫描新表。
 * 当工单回单结束后，删除该记录(业务需要，可以不删除)
 * 当对应设备已经有一条数据时，不在产生新数据.
 * 由于需要再任务重获取集合并且数据量不算大，将所有企业的该数据放在一张表中存储
 */
public class WorkAlarmConfig {

    private String id;                                  //id
    private String uniqueCode;                          //企业编码
    private String workConfigId;                        //工单配置id
    private Date sendTime;                          //派单时间
    private String sendType;                        //派单类型
    private String siteCode;
    private String siteName;
    private String deviceCode;
    private String deviceName;
    private String deviceType;
    private String alarmName;
    private Integer alarmLevel;
    private String alarmState;
    private Date treport;
    private String alarmKey;                             //告警key

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
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

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public Integer getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(Integer alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public String getAlarmState() {
        return alarmState;
    }

    public void setAlarmState(String alarmState) {
        this.alarmState = alarmState;
    }

    public Date getTreport() {
        return treport;
    }

    public void setTreport(Date treport) {
        this.treport = treport;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public String getAlarmKey() {
        return alarmKey;
    }

    public void setAlarmKey(String alarmKey) {
        this.alarmKey = alarmKey;
    }

    public String getWorkConfigId() {
        return workConfigId;
    }

    public void setWorkConfigId(String workConfigId) {
        this.workConfigId = workConfigId;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getSendType() {
        return sendType;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }

    public WorkAlarm createWorkAlarm(){
        WorkAlarm workAlarm = new WorkAlarm();
        workAlarm.setAlarmKey(this.getAlarmKey());
        workAlarm.setAlarmName(this.getAlarmName());
        workAlarm.setLevel(this.getAlarmLevel());
        workAlarm.setState(this.getAlarmState());
        workAlarm.setTreport(this.getTreport());
        return workAlarm;
    }
}

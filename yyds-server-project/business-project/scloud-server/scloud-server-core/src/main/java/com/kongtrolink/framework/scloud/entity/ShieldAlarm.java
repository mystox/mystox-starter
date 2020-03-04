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
    private String deviceId;
    private String signalId;
    private String alarmLevel;
    private Date treport;

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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSignalId() {
        return signalId;
    }

    public void setSignalId(String signalId) {
        this.signalId = signalId;
    }

    public String getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(String alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public Date getTreport() {
        return treport;
    }

    public void setTreport(Date treport) {
        this.treport = treport;
    }

    public void initInfo(ShieldRule shieldRule, Alarm alarm){
        this.ruleId = shieldRule.getId();
        this.alarmId = alarm.getId();
        this.deviceId = alarm.getDeviceId();
        this.signalId = alarm.getSignalId();
        this.alarmLevel = alarm.getTargetLevelName();
        this.treport = alarm.getTreport();
    }
}

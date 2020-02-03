package com.kongtrolink.framework.gateway.iaiot.core.rec.info;

/**
 * 告警点信息
 * Created by Mag on 2019/10/14.
 */
public class AlarmPoint {

    private String id;//	String	否	告警点ID
    private int enable;//	number	否	告警使能
    private int delay;//	number	否	告警延时
    private int recoverDelay;//	number	否	告警恢复延时
    private int threshold;//	number	否	告警门限值
    private int level;//	number	否	告警等级
    private int hysteresis;//	number	否	告警回差
    private int coDataPointId;//	number	否	关联数据点的ID
    private String alarmDesc;//	string	否	告警时描述
    private String normalDesc;//	string	否	告警恢复时描述

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getRecoverDelay() {
        return recoverDelay;
    }

    public void setRecoverDelay(int recoverDelay) {
        this.recoverDelay = recoverDelay;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getHysteresis() {
        return hysteresis;
    }

    public void setHysteresis(int hysteresis) {
        this.hysteresis = hysteresis;
    }

    public int getCoDataPointId() {
        return coDataPointId;
    }

    public void setCoDataPointId(int coDataPointId) {
        this.coDataPointId = coDataPointId;
    }

    public String getAlarmDesc() {
        return alarmDesc;
    }

    public void setAlarmDesc(String alarmDesc) {
        this.alarmDesc = alarmDesc;
    }

    public String getNormalDesc() {
        return normalDesc;
    }

    public void setNormalDesc(String normalDesc) {
        this.normalDesc = normalDesc;
    }
}

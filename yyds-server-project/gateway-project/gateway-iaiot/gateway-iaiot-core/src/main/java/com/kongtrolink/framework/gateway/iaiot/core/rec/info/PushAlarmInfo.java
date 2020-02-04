package com.kongtrolink.framework.gateway.iaiot.core.rec.info;

/**
 * 告警详情
 * Created by Mag on 2019/10/14.
 */
public class PushAlarmInfo {

    private int serialNo;//	number	否	告警序列号
    private String  deviceId;//	number	否	告警所属设备的ID
    private String  id;//	number	否	告警点的ID
    private long  time;//	number	否	告警时间
    private int  level;//	number	否	告警等级
    private int  flag;//	number	否	告警标志（0：结束，1：开始）
    private String  desc;//	string	否	告警描述
    private double  eventValue;//	number	否	告警触发值

    public int getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double getEventValue() {
        return eventValue;
    }

    public void setEventValue(double eventValue) {
        this.eventValue = eventValue;
    }
}

package com.kongtrolink.framework.gateway.tower.entity.rec.info;

/**
 * 告警详情
 * Created by Mag on 2019/10/14.
 */
public class PushAlarmInfo {

    private int serialNo;//	number	否	告警序列号
    private int  deviceId;//	number	否	告警所属设备的ID
    private int  id;//	number	否	告警点的ID
    private int  time;//	number	否	告警时间
    private int  level;//	number	否	告警等级
    private int  flag;//	number	否	告警标志（0：结束，1：开始）
    private String  desc;//	string	否	告警描述
    private int  eventValue;//	number	否	告警触发值

    public int getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
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

    public int getEventValue() {
        return eventValue;
    }

    public void setEventValue(int eventValue) {
        this.eventValue = eventValue;
    }
}

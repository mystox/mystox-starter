package com.kongtrolink.framework.core.entity;

import java.util.Date;

/**
 * Created by mystoxlol on 2019/3/20, 9:53.
 * company: kongtrolink
 * description:
 * update record:
 */
public class Alarm {

    private String id;
    private String alarmId;         //告警点id
    private Double value;           //告警值
    private byte link;              //告警所处环节(1-开始；2-开始报文上报成功；4-结束报文；8结束报文上报成功)
    private Date updateTime;        //跟新时间

    public byte getLink() {
        return link;
    }

    public void setLink(byte link) {
        this.link = link;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}

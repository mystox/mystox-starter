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
    private Boolean hasBegin;       //是否有告警开始报文
    private Boolean beginReport;    //告警开始是否上报成功
    private Boolean hasEnd;         //是否有告警结束报文
    private Boolean endReort;        //告警结束是否上报成功
    private Date updateTime;        //跟新时间

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

    public Boolean getHasBegin() {
        return hasBegin;
    }

    public void setHasBegin(Boolean hasBegin) {
        this.hasBegin = hasBegin;
    }

    public Boolean getBeginReport() {
        return beginReport;
    }

    public void setBeginReport(Boolean beginReport) {
        this.beginReport = beginReport;
    }

    public Boolean getHasEnd() {
        return hasEnd;
    }

    public void setHasEnd(Boolean hasEnd) {
        this.hasEnd = hasEnd;
    }

    public Boolean getEndReort() {
        return endReort;
    }

    public void setEndReort(Boolean endReort) {
        this.endReort = endReort;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}

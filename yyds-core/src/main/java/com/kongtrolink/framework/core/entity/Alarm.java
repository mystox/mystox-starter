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
    private float value;            //告警值
    private byte link;              //告警所处环节(1-开始；2-开始报文上报成功；4-结束报文；8结束报文上报成功,16延迟产生，32延迟消除)
    private Date tReport;           //产生时间
    private Date tRecover;          //消除时间
    private int delay;              //告警产生延时(单位为秒)
    private long beginDelayFT;      //告警产生延迟第一次时间
    private int recoverDelay;       //告警恢复延时
    private long recoverDelayFT;     //告警消除下一次时间
    private int num;                //告警序列号，终端内唯一
    private String desc ;            //告警描述

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getRecoverDelayFT() {
        return recoverDelayFT;
    }

    public void setRecoverDelayFT(long recoverDelayFT) {
        this.recoverDelayFT = recoverDelayFT;
    }

    public long getBeginDelayFT() {
        return beginDelayFT;
    }

    public void setBeginDelayFT(long beginDelayFT) {
        this.beginDelayFT = beginDelayFT;
    }

    public int getRecoverDelay() {
        return recoverDelay;
    }

    public void setRecoverDelay(int recoverDelay) {
        this.recoverDelay = recoverDelay;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

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

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public Date gettReport() {
        return tReport;
    }

    public void settReport(Date tReport) {
        this.tReport = tReport;
    }

    public Date gettRecover() {
        return tRecover;
    }

    public void settRecover(Date tRecover) {
        this.tRecover = tRecover;
    }
}

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
    private String sn;                  //设备sn
    private String dev;                 //设备id,注册和保存告警需要使用
    private String alarmId;             //告警点id
    private float value;                //告警值
    private byte link;                  //告警所处环节(1-开始；2-开始报文上报成功；4-结束报文；8结束报文上报成功,16延迟产生，32延迟消除)
    private Date tReport;               //产生时间
    private Date tRecover;              //消除时间
    private Integer delay;              //告警产生延时(单位为秒)
    private Long beginDelayFT;          //告警产生延迟第一次时间
    private Integer recoverDelay;       //告警恢复延时
    private Long recoverDelayFT;        //告警消除下一次时间
    private int num;                    //告警序列号，终端内唯一
    private byte highRate;              //是否高频
    private Integer coType;             //关联数据点类型

    public Integer getCoType() {
        return coType;
    }

    public void setCoType(Integer coType) {
        this.coType = coType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getDev() {
        return dev;
    }

    public void setDev(String dev) {
        this.dev = dev;
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

    public byte getLink() {
        return link;
    }

    public void setLink(byte link) {
        this.link = link;
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

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public Long getBeginDelayFT() {
        return beginDelayFT;
    }

    public void setBeginDelayFT(Long beginDelayFT) {
        this.beginDelayFT = beginDelayFT;
    }

    public Integer getRecoverDelay() {
        return recoverDelay;
    }

    public void setRecoverDelay(Integer recoverDelay) {
        this.recoverDelay = recoverDelay;
    }

    public Long getRecoverDelayFT() {
        return recoverDelayFT;
    }

    public void setRecoverDelayFT(Long recoverDelayFT) {
        this.recoverDelayFT = recoverDelayFT;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public byte getHighRate() {
        return highRate;
    }

    public void setHighRate(byte highRate) {
        this.highRate = highRate;
    }
}

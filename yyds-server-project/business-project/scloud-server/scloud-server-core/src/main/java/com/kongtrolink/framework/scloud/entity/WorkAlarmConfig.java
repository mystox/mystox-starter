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
    private String alarmKey;                             //告警key
    private String workConfigId;                        //工单配置id
    private Date sendTime;                          //派单时间
    private String sendType;                        //派单类型

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
}

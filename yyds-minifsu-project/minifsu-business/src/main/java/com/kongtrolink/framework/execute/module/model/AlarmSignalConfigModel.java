package com.kongtrolink.framework.execute.module.model;

/**
 * Created by mystoxlol on 2019/3/31, 18:53.
 * company: kongtrolink
 * description: 告警点配置表
 * update record:
 */
public class AlarmSignalConfigModel {
    private String id;
    private String monitorId; //告警点id
    private Integer type; //告警点类型
    private String coId;//关联数据点ID
    private Integer coType; //关联数据点类型
    private Boolean enable; //告警使能
    private Double threshold;//门限值
    private Integer thresholdFlag; //门限值标志
    private Integer level;//告警等级
    private Float hystersis; //告警回差;
    private Integer delay; //告警产生延迟
    private Integer recoverDelay; //告警恢复延时
    private Integer repeatDelay; //告警重复延时
    private String alarmDesc; //告警描述


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(String monitorId) {
        this.monitorId = monitorId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCoId() {
        return coId;
    }

    public void setCoId(String coId) {
        this.coId = coId;
    }

    public Integer getCoType() {
        return coType;
    }

    public void setCoType(Integer coType) {
        this.coType = coType;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public Integer getThresholdFlag() {
        return thresholdFlag;
    }

    public void setThresholdFlag(Integer thresholdFlag) {
        this.thresholdFlag = thresholdFlag;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Float getHystersis() {
        return hystersis;
    }

    public void setHystersis(Float hystersis) {
        this.hystersis = hystersis;
    }

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public Integer getRecoverDelay() {
        return recoverDelay;
    }

    public void setRecoverDelay(Integer recoverDelay) {
        this.recoverDelay = recoverDelay;
    }

    public Integer getRepeatDelay() {
        return repeatDelay;
    }

    public void setRepeatDelay(Integer repeatDelay) {
        this.repeatDelay = repeatDelay;
    }

    public String getAlarmDesc() {
        return alarmDesc;
    }

    public void setAlarmDesc(String alarmDesc) {
        this.alarmDesc = alarmDesc;
    }
}

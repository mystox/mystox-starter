package com.kongtrolink.framework.core.entity;

/**
 * @Auther: liudd
 * @Date: 2019/3/26 13:13
 * @Description:告警点实体类
 */
public class AlarmSignal {

    private String id;
    private String alarmId;         //告警点id（比如判定温感设备下的温度过高，超高，过低，超低告警）
    private String cntbDeviceId;    //设备表id
    private String signalId;        //信号点Id
    private String type;            //告警类型
    private String coId;            //数据点id
    private int coType;             //关联数据点类型
    private boolean enable;         //告警使能
    private double threshold;       //阈值
    private int thresholdFlag;      //门限值标志
    private int level;              //告警等级
    private float hystersis;        //告警回差
    private int delay;              //告警产生延时
    private int recoverDelay;       //告警恢复延时
    private int repeatDelay;        //告警重复延时
    private int alarmDesc;          //告警描述
    private int normalDesc;         //正常时描述

    public String getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }

    public String getCntbDeviceId() {
        return cntbDeviceId;
    }

    public void setCntbDeviceId(String cntbDeviceId) {
        this.cntbDeviceId = cntbDeviceId;
    }

    public String getSignalId() {
        return signalId;
    }

    public void setSignalId(String signalId) {
        this.signalId = signalId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCoId() {
        return coId;
    }

    public void setCoId(String coId) {
        this.coId = coId;
    }

    public int getCoType() {
        return coType;
    }

    public void setCoType(int coType) {
        this.coType = coType;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public int getThresholdFlag() {
        return thresholdFlag;
    }

    public void setThresholdFlag(int thresholdFlag) {
        this.thresholdFlag = thresholdFlag;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public float getHystersis() {
        return hystersis;
    }

    public void setHystersis(float hystersis) {
        this.hystersis = hystersis;
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

    public int getRepeatDelay() {
        return repeatDelay;
    }

    public void setRepeatDelay(int repeatDelay) {
        this.repeatDelay = repeatDelay;
    }

    public int getAlarmDesc() {
        return alarmDesc;
    }

    public void setAlarmDesc(int alarmDesc) {
        this.alarmDesc = alarmDesc;
    }

    public int getNormalDesc() {
        return normalDesc;
    }

    public void setNormalDesc(int normalDesc) {
        this.normalDesc = normalDesc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @auther: liudd
     * @date: 2019/3/27 9:44
     * 功能描述:根据目标信号点实时值，判定是否应该产生告警
     */
    public boolean isAlarm(AlarmSignal tarSignal){
        if(null == tarSignal){
            return false;
        }

        return false;
    }
}

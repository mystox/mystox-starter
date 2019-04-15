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
    private Integer coType;         //关联数据点类型
    private Boolean enable;         //告警使能
    private double threshold;       //阈值
    private int thresholdFlag;  //门限值标志
    private Integer level;          //告警等级
    private Float hystersis;        //告警回差
    private int delay;          //告警产生延时(单位为秒)
    private int recoverDelay;   //告警恢复延时
    private Integer repeatDelay;    //告警重复延时
    private Integer alarmDesc;      //告警描述
    private Integer normalDesc;     //正常时描述
    //高频过滤，暂时所有告警点都有
    private int highRateI = 30*60;      //高频过滤间隔highrateinteval（单位为秒）
    private int highRateT = 6;      //高频过滤规定间隔内，允许的告警次数
    private long highRateFT;        //高频过滤第一次告警时间
    private int highRateC;      //高频过滤告警次数

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

    public Integer getRepeatDelay() {
        return repeatDelay;
    }

    public void setRepeatDelay(Integer repeatDelay) {
        this.repeatDelay = repeatDelay;
    }

    public Integer getAlarmDesc() {
        return alarmDesc;
    }

    public void setAlarmDesc(Integer alarmDesc) {
        this.alarmDesc = alarmDesc;
    }

    public Integer getNormalDesc() {
        return normalDesc;
    }

    public void setNormalDesc(Integer normalDesc) {
        this.normalDesc = normalDesc;
    }

    public int getHighRateI() {
        return highRateI;
    }

    public void setHighRateI(int highRateI) {
        this.highRateI = highRateI;
    }

    public int getHighRateT() {
        return highRateT;
    }

    public void setHighRateT(int highRateT) {
        this.highRateT = highRateT;
    }

    public long getHighRateFT() {
        return highRateFT;
    }

    public void setHighRateFT(long highRateFT) {
        this.highRateFT = highRateFT;
    }

    public int getHighRateC() {
        return highRateC;
    }

    public void setHighRateC(int highRateC) {
        this.highRateC = highRateC;
    }
}

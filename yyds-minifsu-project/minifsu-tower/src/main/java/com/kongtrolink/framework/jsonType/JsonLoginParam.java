package com.kongtrolink.framework.jsonType;

/**
 * @author fengw
 * 终端注册参数信息
 * 新建文件 2019-4-16 10:31:13
 * */
public class JsonLoginParam {
    //fsuId
    private String fsuId;
    //铁塔平台心跳周期(秒)
    private int heartbeatInterval;
    //心跳超时次数上限
    private int heartbeatTimeoutLimit;
    //注册次数上限
    private int loginLimit;
    //注册间隔(秒)
    private int loginInterval;
    //单条告警上报次数上限
    private int alarmReportLimit;
    //告警上报间隔(秒)
    private int alarmReportInterval;
    //历史数据保存间隔(分钟)
    private int dataSaveInterval;

    public String getFsuId() {
        return fsuId;
    }

    public void setFsuId(String fsuId) {
        this.fsuId = fsuId;
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    public int getHeartbeatTimeoutLimit() {
        return heartbeatTimeoutLimit;
    }

    public void setHeartbeatTimeoutLimit(int heartbeatTimeoutLimit) {
        this.heartbeatTimeoutLimit = heartbeatTimeoutLimit;
    }

    public int getLoginLimit() {
        return loginLimit;
    }

    public void setLoginLimit(int loginLimit) {
        this.loginLimit = loginLimit;
    }

    public int getLoginInterval() {
        return loginInterval;
    }

    public void setLoginInterval(int loginInterval) {
        this.loginInterval = loginInterval;
    }

    public int getAlarmReportLimit() {
        return alarmReportLimit;
    }

    public void setAlarmReportLimit(int alarmReportLimit) {
        this.alarmReportLimit = alarmReportLimit;
    }

    public int getAlarmReportInterval() {
        return alarmReportInterval;
    }

    public void setAlarmReportInterval(int alarmReportInterval) {
        this.alarmReportInterval = alarmReportInterval;
    }

    public int getDataSaveInterval() {
        return dataSaveInterval;
    }

    public void setDataSaveInterval(int dataSaveInterval) {
        this.dataSaveInterval = dataSaveInterval;
    }
}

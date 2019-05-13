package com.kongtrolink.framework.execute.module.model;

/**
 * @author fengw
 * 历史数据信息
 * 新建文件 2019-5-8 14:02:07
 */
public class HisData {
    private String fsuId;
    private String deviceId;
    private String signalId;
    private String value;
    private long time;

    public String getFsuId() {
        return fsuId;
    }

    public void setFsuId(String fsuId) {
        this.fsuId = fsuId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSignalId() {
        return signalId;
    }

    public void setSignalId(String signalId) {
        this.signalId = signalId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

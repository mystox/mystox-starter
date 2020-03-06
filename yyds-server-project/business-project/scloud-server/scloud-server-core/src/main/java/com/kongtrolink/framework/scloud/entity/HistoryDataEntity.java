package com.kongtrolink.framework.scloud.entity;

import java.io.Serializable;
import java.util.Map;

/**
 * 存放历史数据
 */
public class HistoryDataEntity implements Serializable {
    private static final long serialVersionUID = -7100654366177772785L;

    private long time;
//    private String fsuCode; //单个企业deviceCode能确定唯一 那就不需要这个字段了
    private String deviceCode;
    private Map<String,Object> value;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public Map<String, Object> getValue() {
        return value;
    }

    public void setValue(Map<String, Object> value) {
        this.value = value;
    }
}

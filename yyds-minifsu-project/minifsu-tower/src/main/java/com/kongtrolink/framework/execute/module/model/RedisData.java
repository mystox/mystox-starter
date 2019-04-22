package com.kongtrolink.framework.execute.module.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fengw
 * Redis中存储的实时数据
 * 新建文件 2019-4-19 09:15:18
 */
public class RedisData {
    private String deviceId;
    private Map<String, Object> values;

    public RedisData() {
        values = new HashMap<>();
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public void setValues(Map<String, Object> values) {
        this.values = values;
    }
}

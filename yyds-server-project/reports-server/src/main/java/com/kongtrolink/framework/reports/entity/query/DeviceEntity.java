package com.kongtrolink.framework.reports.entity.query;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * \* @Author: mystox
 * \* Date: 2020/3/10 9:55
 * \* Description:
 * \
 */
public class DeviceEntity {
    @JSONField(name = "sn")
    private String deviceId;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
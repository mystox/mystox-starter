package com.kongtrolink.framework.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
public class AssetManagementConfig {

    public int getDeviceGetInterval() {
        return deviceGetInterval;
    }

    @Value("${taskInterval.deviceGet}")
    private int deviceGetInterval;


}

package com.kongtrolink.framework.api;

import com.kongtrolink.framework.mqtt.entity.MqttMsg;

/**
 * Created by mystoxlol on 2019/8/15, 13:31.
 * company: kongtrolink
 * description:
 * update record:
 */

public interface LocalService {
    String hello(MqttMsg mqttMsg);
}

package com.kongtrolink.framework.mqtt.service;

/**
 * Created by mystoxlol on 2019/8/13, 9:26.
 * company: kongtrolink
 * description:
 * update record:
 */
public interface MqttReceiver {

    public String receive(String topic, String payload);
}

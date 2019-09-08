package com.kongtrolink.framework.mqtt.service;

import com.kongtrolink.framework.entity.MqttMsg;
import com.kongtrolink.framework.entity.MqttResp;

/**
 * Created by mystoxlol on 2019/8/13, 9:26.
 * company: kongtrolink
 * description:
 * update record:
 */
public interface MqttReceiver {

    public MqttResp receive(String topic, MqttMsg payload);
}

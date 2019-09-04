package com.kongtrolink.framework.mqtt.service;

/**
 * Created by mystoxlol on 2019/8/19, 8:25.
 * company: kongtrolink
 * description:
 * update record:
 */
public interface MqttSender {

    void sendToMqtt(String serverCode, String operaCode,
                    String payload);

    void sendToMqtt(String serverCode, String operaCode,
                    int qos,
                    String payload);

    String sendToMqttSyn(String serverCode, String operaCode,
                    int qos,
                    String payload);
    String sendToMqttSyn2(String serverCode, String operaCode,
                    int qos,
                    String payload);
}

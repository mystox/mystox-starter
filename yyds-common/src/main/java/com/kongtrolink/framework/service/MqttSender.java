package com.kongtrolink.framework.service;

import com.kongtrolink.framework.entity.MsgResult;

import java.util.concurrent.TimeUnit;

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

    MsgResult sendToMqttSyn(String serverCode, String operaCode,
                            String payload);
    MsgResult sendToMqttSyn(String serverCode, String operaCode,
                         int qos, String payload, long timeout, TimeUnit timeUnit);

}

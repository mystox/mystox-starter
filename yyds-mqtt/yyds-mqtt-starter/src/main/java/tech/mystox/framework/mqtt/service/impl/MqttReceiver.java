package tech.mystox.framework.mqtt.service.impl;

import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.mqtt.service.IMqttSender;

/**
 * MQTT receiver adapter that keeps the existing MQTT sender integration.
 */
public class MqttReceiver extends MessageBusReceiverSupport {

    /**
     * 注入发送MQTT的Bean
     */
    private final IMqttSender iMqttSender;

    public MqttReceiver(IaContext iaContext, IMqttSender iMqttSender) {
        super(iaContext);
        this.iMqttSender = iMqttSender;
    }

    @Override
    protected void publishAck(String ackTopic, int qos, String payload) throws Exception {
        iMqttSender.sendToMqtt(ackTopic, qos, payload);
    }
}

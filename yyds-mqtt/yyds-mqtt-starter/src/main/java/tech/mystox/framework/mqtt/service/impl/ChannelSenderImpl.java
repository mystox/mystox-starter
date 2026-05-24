package tech.mystox.framework.mqtt.service.impl;

import tech.mystox.framework.config.IaConf;
import tech.mystox.framework.core.IaENV;
import tech.mystox.framework.mqtt.service.IMqttSender;

/**
 * MQTT sender adapter that keeps the existing IMqttSender integration.
 */
public class ChannelSenderImpl extends MessageBusSenderSupport {

    private final IMqttSender mqttSender;

    public ChannelSenderImpl(IaENV iaEnv, IaConf iaConf, IMqttSender iMqttSender) {
        super(iaEnv, iaConf);
        this.mqttSender = iMqttSender;
    }

    @Override
    protected void publishToBus(String topic, String payload) throws Exception {
        mqttSender.sendToMqtt(topic, payload);
    }

    @Override
    protected void publishToBus(String topic, int qos, String payload) throws Exception {
        mqttSender.sendToMqtt(topic, qos, payload);
    }
}

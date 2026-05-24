package tech.mystox.framework.mqtt.service.impl;

import tech.mystox.framework.mqtt.service.IMqttSender;
import tech.mystox.framework.mqtt.service.MessageBusChannel;
import tech.mystox.framework.mqtt.service.MessageBusListener;
import tech.mystox.framework.mqtt.service.MessageBusTransport;

/**
 * Message bus facade for the existing MQTT Spring Integration implementation.
 */
public class MqttMessageBusTransport implements MessageBusTransport {

    private final IMqttSender mqttSender;
    private final ChannelHandlerSub requestHandler;
    private final ChannelHandlerAck ackHandler;

    public MqttMessageBusTransport(IMqttSender mqttSender,
                                   ChannelHandlerSub requestHandler,
                                   ChannelHandlerAck ackHandler) {
        this.mqttSender = mqttSender;
        this.requestHandler = requestHandler;
        this.ackHandler = ackHandler;
    }

    @Override
    public void publish(String topic, int qos, String payload) throws Exception {
        mqttSender.sendToMqtt(topic, qos, payload);
    }

    @Override
    public void subscribe(MessageBusChannel channel, String topic, int qos, MessageBusListener listener) {
        subscribe(channel, topic, qos);
    }

    @Override
    public void subscribe(MessageBusChannel channel, String topic, int qos) {
        if (MessageBusChannel.ACK.equals(channel)) {
            ackHandler.addSubTopic(topic, qos);
        } else {
            requestHandler.addSubTopic(topic, qos);
        }
    }

    @Override
    public void unsubscribe(MessageBusChannel channel, String... topics) {
        if (MessageBusChannel.ACK.equals(channel)) {
            ackHandler.removeSubTopic(topics);
        } else {
            requestHandler.removeSubTopic(topics);
        }
    }

    @Override
    public boolean isSubscribed(MessageBusChannel channel, String topic) {
        if (MessageBusChannel.ACK.equals(channel)) {
            return ackHandler.isExists(topic);
        }
        return requestHandler.isExists(topic);
    }

    @Override
    public void start() {
        // Existing MQTT adapters are started during handler initialization.
    }

    @Override
    public void stop() {
        // Existing MQTT lifecycle is still owned by Spring Integration.
    }
}

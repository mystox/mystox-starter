package tech.mystox.framework.mqtt.service.impl;

import tech.mystox.framework.config.IaConf;
import tech.mystox.framework.core.IaENV;
import tech.mystox.framework.mqtt.service.MessageBusTransport;

/**
 * Sender implementation for non-MQTT message bus transports.
 */
public class MessageBusSender extends MessageBusSenderSupport {

    private final MessageBusTransport transport;

    public MessageBusSender(IaENV iaEnv, IaConf iaConf, MessageBusTransport transport) {
        super(iaEnv, iaConf);
        this.transport = transport;
    }

    @Override
    protected void publishToBus(String topic, String payload) throws Exception {
        transport.publish(topic, 2, payload);
    }

    @Override
    protected void publishToBus(String topic, int qos, String payload) throws Exception {
        transport.publish(topic, qos, payload);
    }
}

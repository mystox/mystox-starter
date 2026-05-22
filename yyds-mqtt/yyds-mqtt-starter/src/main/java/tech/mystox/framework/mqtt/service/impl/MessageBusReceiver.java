package tech.mystox.framework.mqtt.service.impl;

import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.mqtt.service.MessageBusTransport;

/**
 * Receiver implementation for non-MQTT message bus transports.
 */
public class MessageBusReceiver extends MessageBusReceiverSupport {

    private final MessageBusTransport transport;

    public MessageBusReceiver(IaContext iaContext, MessageBusTransport transport) {
        super(iaContext);
        this.transport = transport;
    }

    @Override
    protected void publishAck(String ackTopic, int qos, String payload) throws Exception {
        transport.publish(ackTopic, qos, payload);
    }
}

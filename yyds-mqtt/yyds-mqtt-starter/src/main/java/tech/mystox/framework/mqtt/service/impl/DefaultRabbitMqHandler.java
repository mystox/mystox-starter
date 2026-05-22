package tech.mystox.framework.mqtt.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.core.IaENV;
import tech.mystox.framework.mqtt.service.ExecutorRunner;
import tech.mystox.framework.mqtt.service.MessageBusChannel;
import tech.mystox.framework.mqtt.service.MessageBusListener;
import tech.mystox.framework.mqtt.service.MessageBusTransport;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

/**
 * RabbitMQ handler that wires the generic message bus protocol to the RabbitMQ transport.
 */
public class DefaultRabbitMqHandler extends MqttHandler {
    private static final String MQTT_RECEIVED_TOPIC = "mqtt_receivedTopic";

    private final MessageBusTransport transport;
    private final MessageBusReceiverSupport mqttReceiver;
    private final ExecutorRunner executorRunner;
    private final MessageBusListener requestListener;
    private final MessageBusListener ackListener;

    public DefaultRabbitMqHandler(IaContext iaContext) {
        super(iaContext.getIaENV());
        IaENV iaENV = iaContext.getIaENV();
        Properties properties = iaENV.getConf().getMqMsgProperties();
        this.transport = new RabbitMqTransport(iaENV, properties);

        int payloadLimit = getInt(properties, 1024 * 1024, "rabbitmq.payload.limit", "messageBus.payload.limit");
        MessageBusSender sender = new MessageBusSender(iaENV, iaENV.getConf(), transport);
        sender.setMqttPayloadLimit(payloadLimit);
        MessageBusReceiver receiver = new MessageBusReceiver(iaContext, transport);
        receiver.setMqttPayloadLimit(payloadLimit);
        this.mqttSenderImpl = sender;
        this.mqttReceiver = receiver;
        this.executorRunner = new ExecutorRunner(this.mqttSenderImpl);
        this.requestListener = (topic, payload, headers) ->
                this.mqttReceiver.messageReceiver(buildSpringMessage(topic, payload, headers));
        this.ackListener = (topic, payload, headers) ->
                this.mqttSenderImpl.messageReceiver(buildSpringMessage(topic, payload, headers));
    }

    public ExecutorRunner getExecutorRunner() {
        return executorRunner;
    }

    @Override
    public void addSubTopic(String topic, int qos) {
        transport.subscribe(MessageBusChannel.REQUEST, topic, qos, requestListener);
    }

    @Override
    public void removeSubTopic(String... topics) {
        transport.unsubscribe(MessageBusChannel.REQUEST, topics);
    }

    @Override
    public void removeAckSubTopic(String... topics) {
        transport.unsubscribe(MessageBusChannel.ACK, topics);
    }

    @Override
    public boolean isAckExists(String topic) {
        return transport.isSubscribed(MessageBusChannel.ACK, topic);
    }

    @Override
    public boolean isExists(String topic) {
        return transport.isSubscribed(MessageBusChannel.REQUEST, topic);
    }

    @Override
    public void addAckTopic(String topic, int qos) {
        transport.subscribe(MessageBusChannel.ACK, topic, qos, ackListener);
    }

    public void stop() {
        transport.stop();
    }

    public void startConsumers() {
        transport.start();
    }

    private Message<String> buildSpringMessage(String topic, String payload, Map<String, Object> sourceHeaders) {
        Map<String, Object> headers = new HashMap<>();
        if (sourceHeaders != null) {
            headers.putAll(sourceHeaders);
        }
        headers.put("id", UUID.randomUUID());
        headers.put("timestamp", System.currentTimeMillis());
        headers.put(MQTT_RECEIVED_TOPIC, topic);
        headers.put("mqtt_duplicate", false);
        return new GenericMessage<>(payload, new MessageHeaders(headers));
    }

    private int getInt(Properties properties, int defaultValue, String... keys) {
        if (properties == null || keys == null) {
            return defaultValue;
        }
        for (String key : keys) {
            String value = properties.getProperty(key);
            if (StringUtils.isNotBlank(value)) {
                return Integer.parseInt(value);
            }
        }
        return defaultValue;
    }
}

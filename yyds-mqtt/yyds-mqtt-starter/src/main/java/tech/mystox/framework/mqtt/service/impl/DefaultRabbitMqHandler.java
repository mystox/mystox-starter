package tech.mystox.framework.mqtt.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import tech.mystox.framework.config.IaConf;
import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.core.IaENV;
import tech.mystox.framework.mqtt.service.ExecutorRunner;
import tech.mystox.framework.mqtt.service.IMqttSender;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RabbitMQ transport adapter that keeps the existing MQTT-shaped topic and payload protocol.
 */
public class DefaultRabbitMqHandler extends MqttHandler {
    private static final Logger logger = LoggerFactory.getLogger(DefaultRabbitMqHandler.class);
    private static final String MQTT_RECEIVED_TOPIC = "mqtt_receivedTopic";

    private final IaENV iaENV;
    private final String exchangeName;
    private final String queuePrefix;
    private final int queueExpires;
    private final CachingConnectionFactory connectionFactory;
    private final RabbitAdmin rabbitAdmin;
    private final RabbitTemplate rabbitTemplate;
    private final SimpleMessageListenerContainer consumerContainer;
    private final SimpleMessageListenerContainer ackContainer;
    private final String instanceId;
    private Queue serviceSubQueue;
    private Queue serviceAckQueue;
    private final Map<String, Queue> subQueues = new ConcurrentHashMap<>();
    private final Map<String, Queue> ackQueues = new ConcurrentHashMap<>();
    private final MqttReceiver mqttReceiver;
    private final ExecutorRunner executorRunner;

    public DefaultRabbitMqHandler(IaContext iaContext) {
        super(iaContext.getIaENV());
        this.iaENV = iaContext.getIaENV();
        Properties properties = iaENV.getConf().getMqMsgProperties();
        this.exchangeName = getProperty(properties, "rabbitmq.exchange", "yyds.rpc");
        this.queuePrefix = getProperty(properties, "rabbitmq.queuePrefix", "yyds");
        this.queueExpires = getInt(properties, "rabbitmq.queueExpires", 600000);
        this.connectionFactory = createConnectionFactory(properties);
        this.rabbitAdmin = new RabbitAdmin(connectionFactory);
        this.rabbitAdmin.declareExchange(ExchangeBuilder.topicExchange(exchangeName).durable(true).build());
        this.rabbitTemplate = new RabbitTemplate(connectionFactory);
        this.instanceId = sanitizeQueuePart(iaENV.getConf().getMyId());
        this.consumerContainer = createContainer(properties);
        this.ackContainer = createContainer(properties);

        IMqttSender sender = createSender();
        this.mqttSenderImpl = new ChannelSenderImpl(iaENV, iaENV.getConf(), sender);
        this.mqttReceiver = new MqttReceiver(iaContext, sender);
        this.executorRunner = new ExecutorRunner(this.mqttSenderImpl);

        this.consumerContainer.setMessageListener(message -> {
            String topic = getReceivedTopic(message);
            this.mqttReceiver.messageReceiver(buildSpringMessage(topic, message.getBody()));
        });
        this.ackContainer.setMessageListener(message ->
                this.mqttSenderImpl.messageReceiver(buildSpringMessage(
                        getReceivedTopic(message), message.getBody())));
        this.consumerContainer.afterPropertiesSet();
        this.ackContainer.afterPropertiesSet();
    }

    public ExecutorRunner getExecutorRunner() {
        return executorRunner;
    }

    @Override
    public void addSubTopic(String topic, int qos) {
        addQueue(topic, subQueues, consumerContainer, false);
    }

    @Override
    public void removeSubTopic(String... topics) {
        removeQueue(subQueues, consumerContainer, topics);
    }

    @Override
    public void removeAckSubTopic(String... topics) {
        removeQueue(ackQueues, ackContainer, topics);
    }

    @Override
    public boolean isAckExists(String topic) {
        return ackQueues.containsKey(topic);
    }

    @Override
    public boolean isExists(String topic) {
        return subQueues.containsKey(topic);
    }

    @Override
    public void addAckTopic(String topic, int qos) {
        addQueue(topic, ackQueues, ackContainer, true);
    }

    public void stop() {
        consumerContainer.stop();
        ackContainer.stop();
        connectionFactory.destroy();
    }

    private IMqttSender createSender() {
        return new IMqttSender() {
            @Override
            public void sendToMqtt(String data) {
            }

            @Override
            public void sendToMqtt(String topic, String payload) {
                publish(topic, payload);
            }

            @Override
            public void sendToMqtt(String topic, int qos, String payload) {
                publish(topic, payload);
            }
        };
    }

    private void publish(String topic, String payload) {
        String routingKey = toRoutingKey(topic);
        rabbitTemplate.convertAndSend(exchangeName, routingKey, payload, message -> {
            message.getMessageProperties().setHeader(MQTT_RECEIVED_TOPIC, topic);
            return message;
        });
        logger.debug("RabbitMQ message sent to [{}] via [{}]", topic, routingKey);
    }

    public void startConsumers() {
        if (serviceSubQueue != null && !consumerContainer.isRunning()) {
            consumerContainer.start();
        }
        if (serviceAckQueue != null && !ackContainer.isRunning()) {
            ackContainer.start();
        }
    }

    private void addQueue(String topic, Map<String, Queue> queues, SimpleMessageListenerContainer container, boolean ackQueue) {
        queues.computeIfAbsent(topic, item -> {
            String routingKey = toRoutingKey(item);
            Queue queue = serviceQueue(ackQueue);
            TopicExchange exchange = new TopicExchange(exchangeName, true, false);
            Binding binding = BindingBuilder.bind(queue).to(exchange).with(routingKey);
            declareQueueIfNecessary(container, queue, ackQueue);
            rabbitAdmin.declareBinding(binding);
            logger.debug("RabbitMQ queue [{}] bound to [{}]", queue.getName(), routingKey);
            return queue;
        });
    }

    private Queue serviceQueue(boolean ackQueue) {
        if (ackQueue) {
            if (serviceAckQueue == null) {
                serviceAckQueue = QueueBuilder.nonDurable(queueName("ack")).expires(queueExpires).build();
            }
            return serviceAckQueue;
        }
        if (serviceSubQueue == null) {
            serviceSubQueue = QueueBuilder.nonDurable(queueName("sub")).expires(queueExpires).build();
        }
        return serviceSubQueue;
    }

    private void declareQueueIfNecessary(SimpleMessageListenerContainer container, Queue queue, boolean ackQueue) {
        boolean alreadyDeclared = ackQueue
                ? ackQueues.containsValue(queue)
                : subQueues.containsValue(queue);
        if (alreadyDeclared) {
            return;
        }
        rabbitAdmin.declareQueue(queue);
        container.addQueues(queue);
        logger.info("RabbitMQ queue [{}] declared", queue.getName());
    }

    private void removeQueue(Map<String, Queue> queues, SimpleMessageListenerContainer container, String... topics) {
        for (String topic : topics) {
            Queue queue = queues.remove(topic);
            if (queue == null || StringUtils.isBlank(queue.getName())) {
                continue;
            }
            container.removeQueues(queue);
            rabbitAdmin.deleteQueue(queue.getName());
            logger.info("RabbitMQ queue [{}] removed for topic [{}]", queue.getName(), topic);
        }
    }

    private SimpleMessageListenerContainer createContainer(Properties properties) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setAmqpAdmin((AmqpAdmin) rabbitAdmin);
        container.setAutoDeclare(true);
        container.setMissingQueuesFatal(false);
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setPrefetchCount(getInt(properties, "rabbitmq.prefetch", 50));
        return container;
    }

    private CachingConnectionFactory createConnectionFactory(Properties properties) {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(getProperty(properties, "spring.rabbitmq.host", "rabbitmq.host", "127.0.0.1"));
        factory.setPort(getInt(properties, "spring.rabbitmq.port", "rabbitmq.port", 5672));
        factory.setUsername(getProperty(properties, "spring.rabbitmq.username", "rabbitmq.username", "guest"));
        factory.setPassword(getProperty(properties, "spring.rabbitmq.password", "rabbitmq.password", "guest"));
        factory.setVirtualHost(getProperty(properties, "spring.rabbitmq.virtual-host", "rabbitmq.virtualHost", "/"));
        return factory;
    }

    private Message<String> buildSpringMessage(String topic, byte[] body) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("id", UUID.randomUUID());
        headers.put("timestamp", System.currentTimeMillis());
        headers.put(MQTT_RECEIVED_TOPIC, topic);
        headers.put("mqtt_duplicate", false);
        return new GenericMessage<>(new String(body, StandardCharsets.UTF_8), new MessageHeaders(headers));
    }

    private String queueName(String topic) {
        IaConf conf = iaENV.getConf();
        String serverCode = String.join(".",
                conf.getGroupCode(),
                conf.getServerName(),
                conf.getServerVersion(),
                conf.getSequence() == null ? "default" : String.valueOf(conf.getSequence()));
        return queuePrefix + "." + serverCode + "." + instanceId + "." + Integer.toHexString(topic.hashCode());
    }

    private String sanitizeQueuePart(String value) {
        if (StringUtils.isBlank(value)) {
            return UUID.randomUUID().toString().replace("-", "");
        }
        return value.replaceAll("[^A-Za-z0-9_-]", "");
    }

    private String toRoutingKey(String topic) {
        String value = StringUtils.removeStart(topic, "/").replace("/", ".");
        return value.replace("+", "#");
    }

    private String toTopic(String routingKey) {
        if (StringUtils.isBlank(routingKey)) {
            return "";
        }
        return "/" + routingKey.replace(".", "/");
    }

    private String getReceivedTopic(org.springframework.amqp.core.Message message) {
        Object topic = message.getMessageProperties().getHeaders().get(MQTT_RECEIVED_TOPIC);
        if (topic != null && StringUtils.isNotBlank(topic.toString())) {
            return topic.toString();
        }
        return toTopic(message.getMessageProperties().getReceivedRoutingKey());
    }

    private String getProperty(Properties properties, String key, String defaultValue) {
        if (properties == null) {
            return defaultValue;
        }
        return properties.getProperty(key, defaultValue);
    }

    private String getProperty(Properties properties, String primaryKey, String fallbackKey, String defaultValue) {
        if (properties == null) {
            return defaultValue;
        }
        String primaryValue = properties.getProperty(primaryKey);
        if (StringUtils.isNotBlank(primaryValue)) {
            return primaryValue;
        }
        return properties.getProperty(fallbackKey, defaultValue);
    }

    private int getInt(Properties properties, String key, int defaultValue) {
        String value = getProperty(properties, key, String.valueOf(defaultValue));
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }

    private int getInt(Properties properties, String primaryKey, String fallbackKey, int defaultValue) {
        String value = getProperty(properties, primaryKey, fallbackKey, String.valueOf(defaultValue));
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }
}

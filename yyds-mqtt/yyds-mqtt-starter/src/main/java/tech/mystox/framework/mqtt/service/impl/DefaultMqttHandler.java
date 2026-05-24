package tech.mystox.framework.mqtt.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import tech.mystox.framework.config.IaConf;
import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.core.IaENV;
import tech.mystox.framework.mqtt.config.MqttConfigInstance;
import tech.mystox.framework.mqtt.config.MultiMqttMessageHandler;
import tech.mystox.framework.mqtt.config.MyMqttPahoMessageHandler;
import tech.mystox.framework.mqtt.service.ExecutorRunner;
import tech.mystox.framework.mqtt.service.IMqttSender;
import tech.mystox.framework.mqtt.service.MessageBusChannel;
import tech.mystox.framework.mqtt.service.MessageBusTransport;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

/**
 * Created by mystox on 2022/4/29, 11:16.
 * company:
 * description:
 * update record:
 */
public class DefaultMqttHandler extends MqttHandler {
    private static final byte[] WILL_DATA;

    static {
        WILL_DATA = "offline".getBytes();
    }

    private final IaENV iaENV;
    private MqttReceiver messageBusReceiver;
    private IMqttSender iMqttSender;
    private final ExecutorRunner executorRunner;
    private final MqttPahoClientFactory mqttPahoClientFactory;
    private final MessageBusTransport transport;

    private MultiMqttMessageHandler multiMqttMessageHandler;
    private MqttPahoMessageDrivenChannelAdapter channelConsumerDrivenChannelAdapter;
    private MqttPahoMessageDrivenChannelAdapter replyProducerDrivenChannelAdapter;

    public DefaultMqttHandler(IaContext iaContext) {
        super(iaContext.getIaENV());
        this.iaENV = iaContext.getIaENV();
        Properties mqMsgProperties = iaENV.getConf().getMqMsgProperties();
        //初始化mqtt客户端
        try {
            this.mqttPahoClientFactory = mqttClientFactory();
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
        int CORE_POOL_SIZE = (int) mqMsgProperties.getOrDefault("mqtt.executor.corePoolSize", 10);
        int MAX_POOL_SIZE = (int) mqMsgProperties.getOrDefault("mqtt.executor.maxPoolSize", 10000);
        int mqttSenderHandlerCount = (int) mqMsgProperties.getOrDefault("mqtt.sender.count", 10);
        this.ackTopicHandler = new ChannelHandlerAck(replyProducer(builderTaskScheduler(CORE_POOL_SIZE, MAX_POOL_SIZE, "mqtt-reply")));
        this.messageBusOperator = createSender(mqttSenderHandlerCount);
        this.requestTopicHandler = new ChannelHandlerSub(channelConsumer(builderTaskScheduler(CORE_POOL_SIZE, MAX_POOL_SIZE, "mqtt-consumer")));
        this.transport = new MqttMessageBusTransport(iMqttSender, requestTopicHandler, ackTopicHandler);

        receiverInit(iaContext);
        this.executorRunner = new ExecutorRunner(
                this.messageBusOperator
        );
    }

    public ExecutorRunner getExecutorRunner() {
        return executorRunner;
    }

    @Override
    public void addSubTopic(String topic, int qos) {
        transport.subscribe(MessageBusChannel.REQUEST, topic, qos);
    }

    @Override
    public void removeSubTopic(String... topic) {
        transport.unsubscribe(MessageBusChannel.REQUEST, topic);
    }

    @Override
    public void removeAckSubTopic(String... topic) {
        transport.unsubscribe(MessageBusChannel.ACK, topic);
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
        transport.subscribe(MessageBusChannel.ACK, topic, qos);
    }

    void receiverInit(IaContext iaContext) {
        messageBusReceiver = new MqttReceiver(iaContext, iMqttSender);
        DirectChannel inBoundChannel = (DirectChannel) MqttConfigInstance.getInstance().mqttInboundChannel();
        inBoundChannel.subscribe(message -> messageBusReceiver.messageReceiver((Message<String>) message));
        DirectChannel replyChannel = (DirectChannel) MqttConfigInstance.getInstance().mqttReplyChannel();
        replyChannel.subscribe(message -> messageBusOperator.messageReceiver((Message<String>) message));
    }


    private MessageProducer channelConsumer(TaskScheduler taskScheduler) {
        IaConf conf = iaENV.getConf();
        Properties mqMsgProperties = conf.getMqMsgProperties();
        String consumerClientId = String.join("_",
                conf.getGroupCode(),
                conf.getServerName(),
                conf.getServerVersion(), "consumer", UUID.randomUUID().toString());
        String consumerDefaultTopic = mqMsgProperties.getProperty("mqtt.consumer.defaultTopic", "topic");
        Integer completionTimeout = (Integer) mqMsgProperties.getOrDefault("mqtt.completionTimeout", 3000);
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        consumerClientId, mqttPahoClientFactory,
                        StringUtils.split(consumerDefaultTopic, ","));
        adapter.setCompletionTimeout(completionTimeout);
        adapter.setConverter(new DefaultPahoMessageConverter());
//        adapter.setQos(0,1,2);
        // 设置订阅通道
        adapter.setOutputChannel(MqttConfigInstance.getInstance().mqttInboundChannel());
        adapter.setTaskScheduler(taskScheduler);
        adapter.start();
        this.channelConsumerDrivenChannelAdapter = adapter;
        return adapter;
    }

    private TaskScheduler builderTaskScheduler(int coreSize, int maxSize, String prefix) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5); // 线程池大小
        scheduler.setThreadNamePrefix(prefix);
        scheduler.initialize();
        return scheduler;
    }

    public MessageProducer replyProducer(TaskScheduler taskScheduler) {
        IaConf conf = iaENV.getConf();
        String producerClientId = String.join("_",
                conf.getGroupCode(),
                conf.getServerName(),
                conf.getServerVersion(), "producer", UUID.randomUUID().toString());
        // 可以同时消费（订阅）多个Topic
        Properties mqMsgProperties = iaENV.getConf().getMqMsgProperties();
        Integer completionTimeout = (Integer) mqMsgProperties.getOrDefault("mqtt.completionTimeout", 3000);
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        producerClientId + "_reply", mqttPahoClientFactory,
                        "topic_ack");
        adapter.setCompletionTimeout(completionTimeout);
        adapter.setConverter(new DefaultPahoMessageConverter());
        //        adapter.setQos(0,1,2);
        // 设置订阅通道
        adapter.setOutputChannel(MqttConfigInstance.getInstance().mqttReplyChannel());
        adapter.setTaskScheduler(taskScheduler);
        adapter.start();
        boolean running = adapter.isRunning();
        if (!running) {
            throw new IllegalStateException("Adapter is not running");
        }
        this.replyProducerDrivenChannelAdapter = adapter;
        return adapter;
    }

    ChannelSenderImpl createSender(Integer mqttSenderHandlerCount) {
        this.multiMqttMessageHandler = new MultiMqttMessageHandler(this::createMqttOutbound, mqttSenderHandlerCount);
        DirectChannel messageChannel = (DirectChannel) MqttConfigInstance.getInstance().mqttOutboundChannel();
        messageChannel.subscribe(multiMqttMessageHandler);
        //IntegrationFlows.from(messageChannel).handle(messageHandler).get();
        multiMqttMessageHandler.start();
        iMqttSender = new IMqttSender() {

            @Override
            public void sendToMqtt(String data) {
            }

            @Override
            public void sendToMqtt(String topic, String payload) throws Exception {
                Map<String, Object> headers = new HashMap<>();
                headers.put("mqtt_qos", 2);
                messageChannel.send(buildMessage(topic, payload, headers));
                //messageHandler.handleMessageInternal(buildMessage(topic, payload, headers));
            }

            @Override
            public void sendToMqtt(String topic, int qos, String payload) throws Exception {
                Map<String, Object> headers = new HashMap<>();
                headers.put("mqtt_qos", qos);
                messageChannel.send(buildMessage(topic, payload, headers));
                //messageHandler.handleMessageInternal(buildMessage(topic, payload, headers));
            }
        };
        return new ChannelSenderImpl(iaENV, iaENV.getConf(), iMqttSender);
    }

    private Message<String> buildMessage(String topic, String payload, Map<String, Object> headers) throws Exception {
        headers.put("id", UUID.randomUUID());
        headers.put("mqtt_topic", topic);
        headers.put("timestamp", System.currentTimeMillis());
        MessageHeaders headersMsg = new MessageHeaders(headers);
        return new GenericMessage<>(payload, headersMsg);
    }

    public MessageHandler createMqttOutbound() {
        IaConf conf = iaENV.getConf();
        Properties mqMsgProperties = conf.getMqMsgProperties();
        //String producerClientId = mqMsgProperties.getProperty("producerClientId");
        String producerClientId = String.join("_",
                conf.getGroupCode(),
                conf.getServerName(),
                conf.getServerVersion(), "producer", UUID.randomUUID().toString());
        String producerDefaultTopic = mqMsgProperties.getProperty("mqtt.producer.defaultTopic", "topic");
        String s = MqttAsyncClient.generateClientId();
        MyMqttPahoMessageHandler messageHandler = new MyMqttPahoMessageHandler(producerClientId + "_" + s, mqttPahoClientFactory);
        messageHandler.setAsync(true); //异步
        messageHandler.setDefaultTopic(producerDefaultTopic);
        messageHandler.setCompletionTimeout(10000);
        messageHandler.setDefaultQos(1);
        messageHandler.onInit(); //手动初始化
        return messageHandler;
    }


    public MqttPahoClientFactory mqttClientFactory() throws MqttException {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions mqttConnectOptions = getMqttConnectOptions();
        factory.setConnectionOptions(mqttConnectOptions);
        for (String serverUri : mqttConnectOptions.getServerURIs()) {
            IMqttAsyncClient asyncClientInstance = null;
            try {
                //校验一下连接
                asyncClientInstance = factory.getAsyncClientInstance(serverUri, "validate-connect");
                asyncClientInstance.connect(factory.getConnectionOptions()).waitForCompletion(5000);
            } catch (MqttException e) {
                throw new RuntimeException(e);
            } finally {
                if (asyncClientInstance != null) {
                    asyncClientInstance.disconnect();
                }
            }
        }
        return factory;
    }

    public MqttConnectOptions getMqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，
        // 这里设置为true表示每次连接到服务器都以新的身份连接
        options.setCleanSession(true);
        // 设置连接的用户名
        Properties mqMsgProperties = iaENV.getConf().getMqMsgProperties();
        Object username = mqMsgProperties.getProperty("mqtt.username");
        options.setUserName(String.valueOf(username));
        // 设置连接的密码
        //        options.setPassword(password.toCharArray());
        String url = mqMsgProperties.getProperty("mqtt.url");
        options.setServerURIs(StringUtils.split(url, ","));
        // 设置超时时间 单位为秒
        options.setConnectionTimeout(10);
        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送心跳判断客户端是否在线，但这个方法并没有重连的机制
        options.setKeepAliveInterval(20);
        // 设置“遗嘱”消息的话题，若客户端与服务器之间的连接意外中断，服务器将发布客户端的“遗嘱”消息。

        options.setAutomaticReconnect(false);
        options.setWill("willTopic", WILL_DATA, 1, false);
        String maxInflightStr = mqMsgProperties.getProperty("mqtt.maxInflight");
        int maxInflight = StringUtils.isBlank(maxInflightStr) ? 1000 : Integer.parseInt(maxInflightStr);
        options.setMaxInflight(maxInflight);
        return options;
    }


    public void stop() {
        // 记得对message相关实例做stop管理
        multiMqttMessageHandler.stop();
        replyProducerDrivenChannelAdapter.stop();
        channelConsumerDrivenChannelAdapter.stop();
    }

}

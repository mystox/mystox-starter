package tech.mystox.framework.mqtt.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import tech.mystox.framework.core.IaENV;
import tech.mystox.framework.mqtt.config.MyMqttPahoMessageHandler;
import tech.mystox.framework.mqtt.service.IMqttSender;

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

    public DefaultMqttHandler(IaENV iaENV) {
        super(iaENV);
        this.iaENV = iaENV;
        //初始化mqtt客户端
        mqttHandlerAck = new ChannelHandlerAck(replyProducer());
        mqttSenderImpl = createSender();
    }

    ChannelSenderImpl createSender() {
        MyMqttPahoMessageHandler messageHandler = (MyMqttPahoMessageHandler) createMqttOutbound();
        IMqttSender iMqttSender = new IMqttSender() {

            @Override
            public void sendToMqtt(String data) {
            }

            @Override
            public void sendToMqtt(String topic, String payload) throws Exception {
                Map<String, Object> headers = new HashMap<>();
                headers.put("mqtt_qos", 2);
                messageHandler.handleMessageInternal(buildMessage(topic, payload, headers));
            }

            @Override
            public void sendToMqtt(String topic, int qos, String payload) throws Exception {
                Map<String, Object> headers = new HashMap<>();
                headers.put("mqtt_qos", qos);
                messageHandler.handleMessageInternal(buildMessage(topic, payload, headers));
            }
        };
        return new ChannelSenderImpl(iaENV, iaENV.getConf(), iMqttSender, null, null);
    }

    private Message<String> buildMessage(String topic, String payload, Map<String, Object> headers) throws Exception {
        headers.put("id", UUID.randomUUID());
        headers.put("mqtt_topic", topic);
        headers.put("timestamp", System.currentTimeMillis());
        MessageHeaders headersMsg = new MessageHeaders(headers);
        return new GenericMessage<>(payload, headersMsg);
    }

    public MessageHandler createMqttOutbound() {
        Properties mqMsgProperties = iaENV.getConf().getMqMsgProperties();
        String producerClientId = mqMsgProperties.getProperty("producerClientId");
        String producerDefaultTopic = mqMsgProperties.getProperty("producerDefaultTopic");
        String s = MqttAsyncClient.generateClientId();
        MyMqttPahoMessageHandler messageHandler = new MyMqttPahoMessageHandler(producerClientId + "_" + s, mqttClientFactory());
        messageHandler.setAsync(true); //异步
        messageHandler.setDefaultTopic(producerDefaultTopic);
        messageHandler.setDefaultQos(1);
        messageHandler.onInit(); //手动初始化
        return messageHandler;
    }

    public MessageProducer replyProducer() {
        // 可以同时消费（订阅）多个Topic
        Properties mqMsgProperties = iaENV.getConf().getMqMsgProperties();
        Object producerClientId = mqMsgProperties.getProperty("producerClientId");
        Integer completionTimeout = (Integer) mqMsgProperties.get("completionTimeout");
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        producerClientId + "_reply", mqttClientFactory(),
                        StringUtils.split("topic_ack", ","));
        adapter.setCompletionTimeout(completionTimeout);
        adapter.setConverter(new DefaultPahoMessageConverter());
        //        adapter.setQos(0,1,2);
        // 设置订阅通道
        adapter.setOutputChannel(mqttReplyChannel());
        return adapter;
    }

    public MessageChannel mqttReplyChannel() {
        return new DirectChannel();
    }

    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(getMqttConnectOptions());
        return factory;
    }

    public MqttConnectOptions getMqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，
        // 这里设置为true表示每次连接到服务器都以新的身份连接
        options.setCleanSession(true);
        // 设置连接的用户名
        Properties mqMsgProperties = iaENV.getConf().getMqMsgProperties();
        Object username = mqMsgProperties.getProperty("username");
        options.setUserName(String.valueOf(username));
        // 设置连接的密码
        //        options.setPassword(password.toCharArray());
        String url = mqMsgProperties.getProperty("url");
        options.setServerURIs(StringUtils.split(url, ","));
        // 设置超时时间 单位为秒
        options.setConnectionTimeout(10);
        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送心跳判断客户端是否在线，但这个方法并没有重连的机制
        options.setKeepAliveInterval(20);
        // 设置“遗嘱”消息的话题，若客户端与服务器之间的连接意外中断，服务器将发布客户端的“遗嘱”消息。

        options.setWill("willTopic", WILL_DATA, 1, false);
        String maxInflightStr = mqMsgProperties.getProperty("maxInflight");
        int maxInflight = StringUtils.isBlank(maxInflightStr) ? 1000 : Integer.parseInt(maxInflightStr);
        options.setMaxInflight(maxInflight);
        return options;
    }

}

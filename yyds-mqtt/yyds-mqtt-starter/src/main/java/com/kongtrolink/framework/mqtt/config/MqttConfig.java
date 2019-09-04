package com.kongtrolink.framework.mqtt.config;

import com.kongtrolink.framework.mqtt.service.MqttReceiver;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

/**
 * Created by mystoxlol on 2019/8/5, 14:35.
 * company: kongtrolink
 * description:
 * update record:
 */
@Configuration
@IntegrationComponentScan("com.kongtrolink.framework")
@ComponentScan("com.kongtrolink.framework.mqtt")
@PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:mqtt.yml")
public class MqttConfig {
    private static final Logger logger = LoggerFactory.getLogger(MqttConfig.class);
    private static final byte[] WILL_DATA;

    static {
        WILL_DATA = "offline".getBytes();
    }

    @Value("${mqtt.username:root}")
    private String username;

    @Value("${mqtt.password:123456}")
    private String password;

    @Value("${mqtt.url}")
    private String url;

    @Value("${mqtt.producer.clientId}")
    private String producerClientId;

    @Value("${mqtt.producer.defaultTopic}")
    private String producerDefaultTopic;

    @Value("${mqtt.consumer.clientId}")
    private String consumerClientId;

    @Value("${mqtt.consumer.defaultTopic}")
    private String consumerDefaultTopic;

    @Value("${mqtt.completionTimeout}")
    private int completionTimeout;   //连接超时


    /**
     * 订阅的bean名称
     */
    public static final String CHANNEL_NAME_IN = "mqttInboundChannel";
    /**
     * 发布的bean名称
     */
    public static final String CHANNEL_NAME_OUT = "mqttOutboundChannel";
    public static final String CHANNEL_REPLY = "mqttReplyBoundChannel";


    /**
     * MQTT连接器选项
     *
     * @return {@link org.eclipse.paho.client.mqttv3.MqttConnectOptions}
     */
    @Bean
    public MqttConnectOptions getMqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，
        // 这里设置为true表示每次连接到服务器都以新的身份连接
        options.setCleanSession(true);
        // 设置连接的用户名
//        options.setUserName(username);
        // 设置连接的密码
//        options.setPassword(password.toCharArray());
        options.setServerURIs(StringUtils.split(url, ","));
        // 设置超时时间 单位为秒
        options.setConnectionTimeout(10);
        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送心跳判断客户端是否在线，但这个方法并没有重连的机制
        options.setKeepAliveInterval(20);
        // 设置“遗嘱”消息的话题，若客户端与服务器之间的连接意外中断，服务器将发布客户端的“遗嘱”消息。
        options.setWill("willTopic", WILL_DATA, 2, false);
        return options;
    }

    /**
     * MQTT客户端
     *
     * @return {@link org.springframework.integration.mqtt.core.MqttPahoClientFactory}
     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(getMqttConnectOptions());
        return factory;
    }

    /**
     * MQTT信息通道（生产者）
     *
     * @return {@link org.springframework.messaging.MessageChannel}
     */
    @Bean(name = CHANNEL_NAME_OUT)
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }
/*
    @Bean(name = CHANNEL_REPLY)
    public MessageChannel mqttReplyChannel() {
        return new DirectChannel();
    }*/

    /**
     * MQTT消息处理器（生产者）
     *
     * @return {@link org.springframework.messaging.MessageHandler}
     */
    @Bean
    @ServiceActivator(inputChannel = CHANNEL_NAME_OUT)
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(
                producerClientId,
                mqttClientFactory());
        messageHandler.setAsync(true); //异步
        messageHandler.setDefaultTopic(producerDefaultTopic);
        messageHandler.setCompletionTimeout(1000);


        return messageHandler;
    }

    /**
     * MQTT消息订阅绑定（消费者）
     *
     * @return {@link org.springframework.integration.core.MessageProducer}
     */
    @Bean
    public MessageProducer inbound() {
        // 可以同时消费（订阅）多个Topic
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        consumerClientId, mqttClientFactory(),
                        StringUtils.split(consumerDefaultTopic, ","));
        adapter.setCompletionTimeout(completionTimeout);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        // 设置订阅通道
        adapter.setOutputChannel(mqttInboundChannel());
        return adapter;
    }

    /**
     * MQTT信息通道（消费者）
     *
     * @return {@link org.springframework.messaging.MessageChannel}
     */
    @Bean(name = CHANNEL_NAME_IN)
    public MessageChannel mqttInboundChannel() {
        return new DirectChannel();
    }

    @Autowired
    MqttReceiver mqttReceiver;

    /**
     * MQTT消息处理器（消费者）
     *
     * @return {@link org.springframework.messaging.MessageHandler}
     */
    @Bean
    @ServiceActivator(inputChannel = CHANNEL_NAME_IN)
    public MessageHandler handler() {

        MessageHandler messageHandler = new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
//            System.out.println(message);
                //至少送达一次存在重复发送的几率，所以订阅服务需要判断消息订阅的幂等性,幂等性可以通过消息属性判断是否重复发送
                Boolean mqtt_duplicate = (Boolean) message.getHeaders().get("mqtt_duplicate");
                if (mqtt_duplicate) {
                    logger.warn("message receive duplicate [{}]", message);
                }
                String topic = message.getHeaders().get("mqtt_topic").toString();
                String result = mqttReceiver.receive(topic, message.getPayload().toString());
                logger.info("message execute result: [{}]", result);
            }
        };
        System.out.println(messageHandler.toString());
//        messageHandler.handleMessage(MessageBuilder.withPayload("123").setHeader(MqttHeaders.TOPIC,"123").build());
        return messageHandler;
    }


}

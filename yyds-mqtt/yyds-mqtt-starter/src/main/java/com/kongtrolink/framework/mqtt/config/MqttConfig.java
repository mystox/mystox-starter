package com.kongtrolink.framework.mqtt.config;

import com.kongtrolink.framework.config.YamlPropertySourceFactory;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

/**
 * Created by mystoxlol on 2019/8/5, 14:35.
 * company: kongtrolink
 * description:
 * update record:
 */
@Configuration
@IntegrationComponentScan("com.kongtrolink.framework")
@ComponentScan("com.kongtrolink.framework")
@PropertySource(factory = YamlPropertySourceFactory.class, value = {"classpath:mqtt.yml"})
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
    /**
     * 回复通道
     */
    public static final String CHANNEL_REPLY = "mqttReplyBoundChannel";


    /**
     * MQTT连接器选项
     *
     * @return {@link MqttConnectOptions}
     */
    @Bean
    public MqttConnectOptions getMqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，
        // 这里设置为true表示每次连接到服务器都以新的身份连接
        options.setCleanSession(true);
        // 设置连接的用户名
       options.setUserName(username);
        // 设置连接的密码
//        options.setPassword(password.toCharArray());
        options.setServerURIs(StringUtils.split(url, ","));
        // 设置超时时间 单位为秒
        options.setConnectionTimeout(10);
        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送心跳判断客户端是否在线，但这个方法并没有重连的机制
        options.setKeepAliveInterval(20);
        // 设置“遗嘱”消息的话题，若客户端与服务器之间的连接意外中断，服务器将发布客户端的“遗嘱”消息。
        options.setWill("willTopic", WILL_DATA, 1, false);
        return options;
    }

    /**
     * MQTT客户端
     *
     * @return {@link MqttPahoClientFactory}
     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(getMqttConnectOptions());
        return factory;
    }

    /**
     * MQTT信息通道（生产者:订阅人）
     *
     * @return {@link MessageChannel}
     */
    @Bean(name = CHANNEL_NAME_OUT)
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean(name = CHANNEL_REPLY)
    public MessageChannel mqttReplyChannel() {
        return new DirectChannel();
    }
    /**
     * MQTT信息通道（消费者）
     *
     * @return {@link MessageChannel}
     */
    @Bean(name = CHANNEL_NAME_IN)
    public MessageChannel mqttInboundChannel() {
        return new DirectChannel();
    }
    /**
     * MQTT消息处理器（生产者）
     *
     * @return {@link MessageHandler}
     */
    @Bean
    @ServiceActivator(inputChannel = CHANNEL_NAME_OUT)
    public MessageHandler mqttOutbound() {
        return new MultiMqttMessageHandler();
    }
    public MessageHandler createMqttOutbound()
    {
        String s = MqttAsyncClient.generateClientId();
        MyMqttPahoMessageHandler messageHandler = new MyMqttPahoMessageHandler(producerClientId+"_"+s, mqttClientFactory());
        messageHandler.setAsync(true); //异步
//        messageHandler.setAsyncEvents(true);
        messageHandler.setDefaultTopic(producerDefaultTopic);
        messageHandler.setDefaultQos(1);
//        messageHandler.setCompletionTimeout(1000);
        messageHandler.onInit(); //手动初始化
        return messageHandler;
    }
    /**
     * MQTT消息订阅绑定（消费者）
     *
     * @return {@link MessageProducer}
     */
    @Bean("inbound")
    public MessageProducer inbound() {
        // 可以同时消费（订阅）多个Topic
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        consumerClientId, mqttClientFactory(),
                        StringUtils.split(consumerDefaultTopic, ","));
        adapter.setCompletionTimeout(completionTimeout);
        adapter.setConverter(new DefaultPahoMessageConverter());
//        adapter.setQos(0,1,2);
        // 设置订阅通道
        adapter.setOutputChannel(mqttInboundChannel());
        return adapter;
    }

    /**
     * MQTT消息订阅回复消息
     *
     * @return {@link MessageProducer}
     */
    @Bean("replyProducer")
    public MessageProducer replyProducer() {
        // 可以同时消费（订阅）多个Topic
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




}

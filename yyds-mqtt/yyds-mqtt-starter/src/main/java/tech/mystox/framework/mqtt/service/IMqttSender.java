package tech.mystox.framework.mqtt.service;

/**
 * Created by mystoxlol on 2019/8/6, 14:15.
 * company: mystox
 * description:
 * update record:
 */

import tech.mystox.framework.mqtt.config.MqttConfig;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * MQTT生产者消息发送接口
 * <p>MessagingGateway要指定生产者的通道名称</p>
 * @author BBF
 */
@Component
@MessagingGateway(defaultRequestChannel = MqttConfig.CHANNEL_NAME_OUT,
        defaultRequestTimeout = "10000"/*,
        defaultReplyChannel = MqttConfig.CHANNEL_REPLY,
        defaultReplyTimeout = "10000"*/)
public interface IMqttSender {

    /**
     * 发送信息到MQTT服务器
     *
     * @param data 发送的文本
     */
    void sendToMqtt(String data);

    /**
     * 发送信息到MQTT服务器

     *
     * @param topic 主题
     * @param payload 消息主体
     */
    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic,
                    String payload) throws Exception;

    /**
     * 发送信息到MQTT服务器
     *
     * @param topic 主题
     * @param qos 对消息处理的几种机制。<br> 0 表示的是订阅者没收到消息不会再次发送，消息会丢失。<br>
     * 1 表示的是会尝试重试，一直到接收到消息，但这种情况可能导致订阅者收到多次重复消息。<br>
     * 2 多了一次去重的动作，确保订阅者收到的消息有一次。
     * @param payload 消息主体
     */
    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic,
                    @Header(MqttHeaders.QOS) int qos,
                    String payload) throws Exception;


//    @Gateway(requestChannel = MqttConfig.CHANNEL_NAME_OUT/*,replyChannel = MqttConfig.CHANNEL_REPLY,replyTimeout = 6000*/)
//    Future<?> sendToMqttSync(@Header(MqttHeaders.TOPIC) String topic,
//                                 @Header(MqttHeaders.QOS) int qos,
//                                 String payload);
}
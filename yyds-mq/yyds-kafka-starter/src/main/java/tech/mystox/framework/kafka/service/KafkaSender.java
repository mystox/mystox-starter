package tech.mystox.framework.kafka.service;

/**
 * Created by mystoxlol on 2019/8/6, 14:15.
 * company: mystox
 * description:
 * update record:
 */

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import tech.mystox.framework.kafka.config.KafkaConfig;

/**
 * MQTT生产者消息发送接口
 * <p>MessagingGateway要指定生产者的通道名称</p>
 * @author BBF
 */
@Component
@MessagingGateway(defaultRequestChannel = KafkaConfig.CHANNEL_NAME_OUT,
        defaultRequestTimeout = "10000"/*,
        defaultReplyChannel = MqttConfig.CHANNEL_REPLY,
        defaultReplyTimeout = "10000"*/)
public interface KafkaSender {

    /**
     * 发送信息到MQTT服务器
     *
     * @param data 发送的文本
     */
    void sendToKafka(String data);

    /**
     * 发送信息到MQTT服务器

     *
     * @param topic 主题
     * @param payload 消息主体
     */
    void sendToKafka(@Header(KafkaHeaders.TOPIC) String topic,
                    String payload);

}
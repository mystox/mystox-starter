package com.kongtrolink.framework.gateway.mqtt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;

import static com.kongtrolink.framework.gateway.mqtt.GatewayMqttConfig.CHANNEL_NAME_IN;

/**
 * Created by mystoxlol on 2019/8/13, 11:05.
 * company: kongtrolink
 * description:
 * update record:
 */
@MessageEndpoint
public class GatewayMqttReceiverNative  {
    private static final Logger logger = LoggerFactory.getLogger(GatewayMqttReceiverNative.class);

    @ServiceActivator(inputChannel = CHANNEL_NAME_IN/*, outputChannel = CHANNEL_NAME_OUT*/)
    public void messageReceiver(Message<String> message) {
        //至少送达一次存在重复发送的几率，所以订阅服务需要判断消息订阅的幂等性,幂等性可以通过消息属性判断是否重复发送
        Boolean mqtt_duplicate = (Boolean) message.getHeaders().get("mqtt_duplicate");
        if (mqtt_duplicate) {
            logger.warn("message receive  duplicate [{}]", message);
        }
        String topic = message.getHeaders().get("mqtt_topic").toString();
        String payload = message.getPayload();

        logger.info("[MQTT] Message arrived. topic: {} message: {}", topic, payload);

    }


}

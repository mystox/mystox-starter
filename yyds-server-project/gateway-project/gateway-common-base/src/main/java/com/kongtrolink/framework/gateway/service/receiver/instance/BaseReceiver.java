package com.kongtrolink.framework.gateway.service.receiver.instance;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.gateway.entity.ParseProtocol;
import com.kongtrolink.framework.gateway.mqtt.GatewayMqttSenderNative;
import com.kongtrolink.framework.gateway.service.parse.ParseService;
import com.kongtrolink.framework.gateway.service.receiver.ReceiveHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import static com.kongtrolink.framework.gateway.mqtt.GatewayMqttConfig.CHANNEL_NAME_IN;

/**
 * Created by mystoxlol on 2019/10/16, 9:40
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class BaseReceiver extends ReceiveHandler {

    private static final Logger logger = LoggerFactory.getLogger(BaseReceiver.class);

    @Autowired
    GatewayMqttSenderNative gatewayMqttSenderNative;
    @Autowired
    ParseService parseService;

    @Autowired
    ThreadPoolTaskExecutor mqttExecutor;

    @ServiceActivator(inputChannel = CHANNEL_NAME_IN/*, outputChannel = CHANNEL_NAME_OUT*/)
    public void messageReceiver(Message<String> message) {
        mqttExecutor.execute(() -> {
            try {
                //至少送达一次存在重复发送的几率，所以订阅服务需要判断消息订阅的幂等性,幂等性可以通过消息属性判断是否重复发送
                Boolean mqtt_duplicate = (Boolean) message.getHeaders().get("mqtt_duplicate");
                if (mqtt_duplicate) {
                    logger.warn("message receive  duplicate [{}]", message);
                }
                String topics = message.getHeaders().get("mqtt_topic").toString();
                String payload = message.getPayload();
                logger.info("[MQTT] Message arrived. topic: {} message: {}", topics, payload);
                //iaiot/edge/yy/+/packetName/version
                String sn = topics.split("/")[3];
                String packetName = topics.split("/")[4];
                String uuid = topics;
                ParseProtocol parseProtocol = new ParseProtocol(packetName, sn, uuid, payload);
                parseService.execute(JSONObject.toJSONString(parseProtocol));
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }



    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void restart() {

    }


}

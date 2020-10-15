package tech.mystox.framework.mqtt.config;

import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.Message;

/**
 * \* @Author: mystox
 * \* Date: 2019/11/22 17:19
 * \* Description:
 * \
 */
public class MyMqttPahoMessageHandler extends MqttPahoMessageHandler {
    public MyMqttPahoMessageHandler(String url, String clientId, MqttPahoClientFactory clientFactory) {
        super(url, clientId, clientFactory);
    }

    public MyMqttPahoMessageHandler(String clientId, MqttPahoClientFactory clientFactory) {
        super(clientId, clientFactory);
    }

    public MyMqttPahoMessageHandler(String url, String clientId) {
        super(url, clientId);
    }


    @Override
    public void doStop() {
        super.doStop();
    }

    @Override
    public void handleMessageInternal(Message<?> message) throws Exception {
        super.handleMessageInternal(message);
    }

    @Override
    public void onInit() {
        try {
            super.onInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
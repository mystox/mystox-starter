package tech.mystox.framework.mqtt.config;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mystoxlol on 2019/8/20, 13:51.
 * company: mystox
 * description:
 * update record:
 */
public class PushCallback implements MqttCallback {
    Logger logger = LoggerFactory.getLogger(PushCallback.class);
    @Override
    public void connectionLost(Throwable throwable) {
        logger.warn("mqtt connection lost...");

    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        logger.info("messageArrived");
        logger.info("topic: {} message: {}",topic,mqttMessage);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        try {
            logger.info(iMqttDeliveryToken.getMessage().toString());
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
}

package com.kongtrolink.framework.mqtt.service.impl;

import com.kongtrolink.framework.mqtt.service.IMqttSender;
import com.kongtrolink.framework.mqtt.service.MqttSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/8/19, 8:28.
 * company: kongtrolink
 * description: 封装的mqtt生产者
 * update record:
 */
@Service
public class MqttSenderImpl implements MqttSender {

    @Value("${mqtt.producer.defaultTopic}")
    private String producerDefaultTopic;

    @Autowired
    private IMqttSender mqttSender;




    @Override
    public void sendToMqtt(String serverCode, String operaCode,                           String payload) {
//        preconditionSend(topic);
//        isExistsByPubList(topic);
//        mqttSender.sendToMqtt(topic, payload);
    }

    @Override
    public void sendToMqtt(String serverCode, String operaCode,
                           int qos,String payload) {

//        mqttSender.sendToMqtt(topic, qos, payload);
    }
    private boolean isExistsByPubList(String topic) {
        //todo
        //是否已经发布


        return true;

    }

}

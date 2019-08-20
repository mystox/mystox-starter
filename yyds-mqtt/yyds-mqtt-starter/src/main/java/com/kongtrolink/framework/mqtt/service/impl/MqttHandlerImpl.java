package com.kongtrolink.framework.mqtt.service.impl;

import com.kongtrolink.framework.mqtt.service.MqttHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/8/20, 10:09.
 * company: kongtrolink
 * description: 处理一些mqtt的接口操作
 * update record:
 */
@Service
public class MqttHandlerImpl implements MqttHandler {



    @Autowired
    private MessageProducer messageProducer;

    @Override
    public void addSubTopic(String topic, int qos) {
        MqttPahoMessageDrivenChannelAdapter messageProducer = (MqttPahoMessageDrivenChannelAdapter) this.messageProducer;
        messageProducer.addTopic(topic, 2);

    }

    @Override
    public void addSubTopic(String... topic) {
        MqttPahoMessageDrivenChannelAdapter messageProducer = (MqttPahoMessageDrivenChannelAdapter) this.messageProducer;
        messageProducer.addTopic(topic);
    }

    @Override
    public void removeSubTopic(String... topic) {
        MqttPahoMessageDrivenChannelAdapter messageProducer = (MqttPahoMessageDrivenChannelAdapter) this.messageProducer;
        messageProducer.removeTopic(topic);
    }
}

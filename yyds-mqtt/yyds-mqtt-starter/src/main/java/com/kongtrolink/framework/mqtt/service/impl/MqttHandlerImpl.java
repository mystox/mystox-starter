package com.kongtrolink.framework.mqtt.service.impl;

import com.kongtrolink.framework.service.MqttHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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

    Logger logger = LoggerFactory.getLogger(MqttHandlerImpl.class);


    @Value("${server.name}")
    private String serverName;

    @Value("${server.version}")
    private String serverVersion;

    @Autowired
    @Qualifier("inbound")
    private MessageProducer messageProducer;




    @Override
    public String assembleSubTopic(String operaCode) {
        return serverName +"_"+ serverVersion + "/" + operaCode;
    }

    @Override
    public void addSubTopic(String topic, int qos) {
        MqttPahoMessageDrivenChannelAdapter messageProducer = (MqttPahoMessageDrivenChannelAdapter) this.messageProducer;
        messageProducer.addTopic(topic, 2);
    }

    @Override
    public void addSubTopic(String... topics) {
        MqttPahoMessageDrivenChannelAdapter messageProducer = (MqttPahoMessageDrivenChannelAdapter) this.messageProducer;
        messageProducer.addTopic(topics);
    }

    @Override
    public void removeSubTopic(String... topic) {
        MqttPahoMessageDrivenChannelAdapter messageProducer = (MqttPahoMessageDrivenChannelAdapter) this.messageProducer;
        messageProducer.removeTopic(topic);
    }
}

package com.kongtrolink.framework.mqtt.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mystoxlol on 2019/8/20, 10:09.
 * company: kongtrolink
 * description: 处理一些mqtt的接口操作
 * update record:
 */
@Service(value = "mqttHandlerImpl")
public class ChannelHandlerSub {

    Logger logger = LoggerFactory.getLogger(ChannelHandlerSub.class);

    @Value("${server.name}_${server.version}")
    private String serverCode;

    @Autowired
    @Qualifier("inbound")
    private MessageProducer messageProducer;



    public void addSubTopic(String topic, int qos) {
        logger.debug("add addSubTopic topic: {}", topic);
        MqttPahoMessageDrivenChannelAdapter messageProducer = (MqttPahoMessageDrivenChannelAdapter) this.messageProducer;
        messageProducer.addTopic(topic, 2);
    }


    public synchronized void addSubTopic(String... topics) {
        logger.debug("add addSubTopic topics: {}", topics);
        MqttPahoMessageDrivenChannelAdapter messageProducer = (MqttPahoMessageDrivenChannelAdapter) this.messageProducer;
        List<String> topicAdd = new ArrayList<>();
        for (String topic: topics)
        {
            if (!isExists(topic)){
                topicAdd.add(topic);
            }
        }
        messageProducer.addTopic(topicAdd.toArray(new String[topicAdd.size()]));
    }


    public void removeSubTopic(String... topic) {
        logger.debug("delete All SUB topics {}",topic);
        MqttPahoMessageDrivenChannelAdapter messageProducer = (MqttPahoMessageDrivenChannelAdapter) this.messageProducer;
        messageProducer.removeTopic(topic);
    }


    public boolean isExists(String topic) {
        MqttPahoMessageDrivenChannelAdapter messageProducer = (MqttPahoMessageDrivenChannelAdapter) this.messageProducer;
        String[] topics = messageProducer.getTopic();
        List<String> topicList = Arrays.asList(topics);
        return topicList.contains(topic);
    }




}

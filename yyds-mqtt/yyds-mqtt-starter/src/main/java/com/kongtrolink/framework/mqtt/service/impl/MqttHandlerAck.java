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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mystoxlol on 2019/8/20, 10:09.
 * company: kongtrolink
 * description: 处理一些mqtt的接口操作
 * update record:
 */
@Service("mqttHandlerAck")
public class MqttHandlerAck implements MqttHandler {

    Logger logger = LoggerFactory.getLogger(MqttHandlerAck.class);


    @Value("${server.name}")
    private String serverName;

    @Value("${server.version}")
    private String serverVersion;

    @Autowired
    @Qualifier(value = "replyProducer")
    private MessageProducer messageProducer;

//    @Override
//    public String assembleSubTopic(String operaCode) {
//        return MqttUtils.preconditionSubTopicId(
//                MqttUtils.preconditionServerCode(serverName, serverVersion), operaCode) + "/ack";
//    }

    @Override
    public void addSubTopic(String topic, int qos) {
        logger.info("add topic: [{}] qos: [{}]", topic,qos);
        MqttPahoMessageDrivenChannelAdapter messageProducer = (MqttPahoMessageDrivenChannelAdapter) this.messageProducer;
        messageProducer.addTopic(topic, 2);
    }

    @Override
    public synchronized void addSubTopic(String... topics) {
        logger.info("add ack topics: {}", topics);
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

    @Override
    public void removeSubTopic(String... topics) {
        logger.info("delete topics: {}", topics);
        MqttPahoMessageDrivenChannelAdapter messageProducer = (MqttPahoMessageDrivenChannelAdapter) this.messageProducer;
        messageProducer.removeTopic(topics);
    }

    @Override
    public boolean isExists(String topic) {
        MqttPahoMessageDrivenChannelAdapter messageProducer = (MqttPahoMessageDrivenChannelAdapter) this.messageProducer;
        String[] topics = messageProducer.getTopic();
        List<String> topicList = Arrays.asList(topics);
        return topicList.contains(topic);
    }
}

package tech.mystox.framework.kafka.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Service;
import tech.mystox.framework.common.util.KafkaUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static tech.mystox.framework.kafka.config.KafkaConfig.CHANNEL_NAME_IN;
import static tech.mystox.framework.kafka.config.KafkaConfig.CHANNEL_REPLY;

/**
 * Created by mystoxlol on 2019/8/20, 10:09.
 * company: mystox
 * description: 处理一些mqtt的接口操作
 * update record:
 */
@Service("kafkaHandlerAck")
public class ChannelHandlerAck {

    Logger logger = LoggerFactory.getLogger(ChannelHandlerAck.class);

    @Autowired(required = false)
    private IntegrationFlowContext flowContext;
    @Autowired
    @Qualifier("myKafkaConsumerFactory")
    ConsumerFactory kafkaConsumerFactory;

    public void addSubTopic(String topic, int qos) {
        logger.debug("add addSubTopic topic: {}", topic);
        StandardIntegrationFlow standardIntegrationFlow;
        try {
            if (topic.contains("*"))
                standardIntegrationFlow = IntegrationFlows.from(Kafka.messageDrivenChannelAdapter(
                        this.kafkaConsumerFactory, Pattern.compile(topic))).channel(CHANNEL_REPLY).get();
            else
                standardIntegrationFlow = IntegrationFlows.from(Kafka.messageDrivenChannelAdapter(
                        this.kafkaConsumerFactory, topic)).channel(CHANNEL_REPLY).get();
            flowContext.registration(standardIntegrationFlow).id(topic).register();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public synchronized void addSubTopic(String... topics) {
        logger.debug("add SubTopic topics: {}", topics);
        for (String topic : topics) {
            topic  = KafkaUtils.toKafkaTopic(topic);
            if (!isExists(topic)) {
                addSubTopic(topic, 0);
            }
        }
    }


    public void removeSubTopic(String... topics) {
        logger.debug("remove sub topics {}", topics);
        for (String topic : topics) {
            topic  = KafkaUtils.toKafkaTopic(topic);
            if (isExists(topic)) {
                flowContext.remove(topic);
            }
        }
    }


    public boolean isExists(String topic) {
        IntegrationFlowContext.IntegrationFlowRegistration registrationById = flowContext.getRegistrationById(topic);
        return registrationById != null;
    }
}

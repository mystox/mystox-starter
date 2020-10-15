package tech.mystox.framework.kafka.service.impl;

import org.apache.kafka.clients.consumer.ConsumerConfig;
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
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;
import tech.mystox.framework.common.util.KafkaUtils;
import tech.mystox.framework.kafka.config.KafkaProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static tech.mystox.framework.kafka.config.KafkaConfig.CHANNEL_NAME_IN;

/**
 * Created by mystoxlol on 2019/8/20, 10:09.
 * company: mystox
 * description: 处理一些mqtt的接口操作
 * update record:
 */
@Service(value = "kafkaHandlerImpl")
public class ChannelHandlerSub {

    Logger logger = LoggerFactory.getLogger(ChannelHandlerSub.class);

    @Value("${server.name}_${server.version}")
    private String serverCode;


    @Autowired(required = false)
    private IntegrationFlowContext flowContext;

    @Autowired
    KafkaProperties kafkaProperties;
    @Autowired
    @Qualifier("myKafkaConsumerFactory")
    ConsumerFactory kafkaConsumerFactory;

    public void addSubTopic(String topic, int partitionId) {
        logger.debug("add addSubTopic topic: {}", topic);
        StandardIntegrationFlow standardIntegrationFlow;
        if (topic.contains("*"))
            standardIntegrationFlow = IntegrationFlows.from(Kafka.messageDrivenChannelAdapter(
                    this.kafkaConsumerFactory, Pattern.compile(topic))).channel(CHANNEL_NAME_IN).get();
        else
            standardIntegrationFlow = IntegrationFlows.from(Kafka.messageDrivenChannelAdapter(
                    this.kafkaConsumerFactory, topic)).channel(CHANNEL_NAME_IN).get();
        flowContext.registration(standardIntegrationFlow).id(topic).register();
    }


    public synchronized void addSubTopic(String... topics) {
        logger.debug("add addSubTopic topics: {}", topics);
        for (String topic : topics) {
            topic = KafkaUtils.toKafkaTopic(topic);
            if (!isExists(topic)) {
                addSubTopic(topic, 0);
            }
        }
    }


    public void removeSubTopic(String... topics) {
        logger.debug("remove sub topics {}", topics);
        for (String topic : topics) {
            topic = KafkaUtils.toKafkaTopic(topic);
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

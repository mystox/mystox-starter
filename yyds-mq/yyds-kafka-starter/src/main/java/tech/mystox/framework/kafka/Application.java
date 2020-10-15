package tech.mystox.framework.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.messaging.MessageChannel;
import tech.mystox.framework.kafka.config.KafkaProperties;
import tech.mystox.framework.kafka.service.KafkaSender;

import java.util.Map;
import java.util.regex.Pattern;

import static tech.mystox.framework.kafka.config.KafkaConfig.CHANNEL_NAME_IN;

//@SpringBootApplication
public class Application {

//    @Autowired
    KafkaProperties kafkaProperties;

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context
                = new SpringApplicationBuilder(Application.class)
                .web(WebApplicationType.NONE)
                .run(args);
        KafkaSender kafkaSender = context.getBean("kafkaSender", KafkaSender.class);
//        context.getBean(Application.class).runDemo(context);
//        context.close();
//        logger.debug("add addSubTopic topic: {}", topic);
        System.out.println("add topic");
        KafkaProperties kafkaProperties = context.getBean(KafkaProperties.class);
        IntegrationFlowContext flowContext = context.getBean(IntegrationFlowContext.class);
        MessageProducer inbound = context.getBean("inbound", MessageProducer.class);
        ConsumerFactory kafkaConsumerFactory = context.getBean("kafkaConsumerFactory", ConsumerFactory.class);
        Map configurationProperties = kafkaConsumerFactory.getConfigurationProperties();

        Map<String, Object> consumerProperties = kafkaProperties.buildConsumerProperties();
//        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG,
//                consumerProperties.get(ConsumerConfig.GROUP_ID_CONFIG) + "x");
//        KafkaMessageDrivenChannelAdapter messageProducer = (KafkaMessageDrivenChannelAdapter) inbound;
//        StandardIntegrationFlow standardIntegrationFlow = IntegrationFlows.from(Kafka.messageDrivenChannelAdapter(
//                new DefaultKafkaConsumerFactory<String, String>(consumerProperties), "ddd")).channel(CHANNEL_NAME_IN).get();
        StandardIntegrationFlow standardIntegrationFlow2 = IntegrationFlows.from(Kafka.messageDrivenChannelAdapter(
                kafkaConsumerFactory, "eeee","ddd")).channel(CHANNEL_NAME_IN).get();
//        flowContext.registration(standardIntegrationFlow).register();
//        flowContext.registration(standardIntegrationFlow2).id("eeee").register();
////        flowContext.remove();
////        MessageChannel outputChannel = messageProducer.getOutputChannel();
//        kafkaSender.sendToMqtt("ddd","dddddddddddddddddddd");
//        kafkaSender.sendToMqtt("eeee","eeeeeeeeeee");
//
//
//        Thread.sleep(5000);
//
//        standardIntegrationFlow2 = IntegrationFlows.from(Kafka.messageDrivenChannelAdapter(
//                kafkaConsumerFactory, "ddd")).channel(CHANNEL_NAME_IN).get();
//        flowContext.registration(standardIntegrationFlow2).id("ddd").register();
//        flowContext.remove("eeee");
//        Thread.sleep(5000);
//        kafkaSender.sendToMqtt("ddd","dddddddddddddddddddd");
//        kafkaSender.sendToMqtt("eeee","eeeeeeeeeee");
//
//        Thread.sleep(5000);

        standardIntegrationFlow2 = IntegrationFlows.from(Kafka.messageDrivenChannelAdapter(
                kafkaConsumerFactory, Pattern.compile("ddd.+.ack"))).channel(CHANNEL_NAME_IN).get();
        flowContext.registration(standardIntegrationFlow2).id("ack").register();
//        Thread.sleep(10000);
//        kafkaSender.sendToMqtt("ddd.sssssssssssss.ack","ddd.sssssssssssss.ackackaaaaaaaaaaaaaaaaadddddddddddddddddddd");
//        kafkaSender.sendToMqtt("ddd.ddd.ack","ddd.ddd.ackackaaaaaaaaaaaaaaaaadddddddddddddddddddd");
//        kafkaSender.sendToMqtt("ddd.bbb.ack","ddd.bbb.ackackaaaaaaaaaaaaaaaaadddddddddddddddddddd");
//        kafkaSender.sendToMqtt("ddd.ccc444444444444444444.ack","ddd.ccc.dd.ackackaaaaaaaaaaaaaaaaadddddddddddddddddddd");
//        for (int i = 10; i< 20; i++)
//        kafkaSender.sendToMqtt("ddd.55555555555_5"+6666+"5.ack","ddd.555555555555555555555.dd.ackackaaaaaaaaaaaaaaaaadddddddddddddddddddd");
//        kafkaSender.sendToMqtt("eeee","eeeeeeeeeee");

        Thread.sleep(5000);
        System.out.println("send end............");


        Thread.sleep(10000000000L);



//        MessageChannel toKafka = context.getBean("toKafka", MessageChannel.class);
    }
}

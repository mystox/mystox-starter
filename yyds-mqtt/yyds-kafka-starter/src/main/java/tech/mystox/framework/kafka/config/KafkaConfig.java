package tech.mystox.framework.kafka.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.integration.kafka.outbound.KafkaProducerMessageHandler;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.support.TopicPartitionInitialOffset;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.PollableChannel;
//import tech.mystox.framework.config.YamlPropertySourceFactory;

import java.util.Map;

/**
 * Created by mystoxlol on 2019/8/5, 14:35.
 * company: mystox
 * description:
 * update record:
 */
@Configuration
@IntegrationComponentScan("tech.mystox.framework")
@ComponentScan("tech.mystox.framework")
//@PropertySource(factory = YamlPropertySourceFactory.class, value = {"classpath:kafka.yml"})
public class KafkaConfig {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConfig.class);
    private static final byte[] WILL_DATA;

    static {
        WILL_DATA = "offline".getBytes();
    }

//    @Value("${kafka.username:root}")
//    private String username;
//
//    @Value("${kafka.password:123456}")
//    private String password;
//
//    @Value("${kafka.url}")
//    private String url;
//
//    @Value("${kafka.producer.clientId}")
//    private String producerClientId;
//
//    @Value("${kafka.producer.defaultTopic}")
//    private String producerDefaultTopic;
//
//    @Value("${kafka.consumer.clientId}")
//    private String consumerClientId;
//
//    @Value("${kafka.consumer.defaultTopic}")
//    private String consumerDefaultTopic;
//
//    @Value("${kafka.completionTimeout}")
//    private int completionTimeout;   //连接超时
//
//    @Value("${kafka.maxInflight:1000}")
//    private int maxInflight;


    /**
     * 订阅的bean名称
     */
    public static final String CHANNEL_NAME_IN = "mqttInboundChannel";
    /**
     * 发布的bean名称
     */
    public static final String CHANNEL_NAME_OUT = "toKafka";
    /**
     * 回复通道
     */
    public static final String CHANNEL_REPLY = "mqttReplyBoundChannel";


    /**
     * MQTT信息通道（生产者:订阅人）
     *
     * @return {@link MessageChannel}
     */
    @Bean(name = CHANNEL_NAME_OUT)
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean(name = CHANNEL_REPLY)
    public MessageChannel mqttReplyChannel() {
        return new DirectChannel();
    }

    /**
     * MQTT信息通道（消费者）
     *
     * @return {@link MessageChannel}
     */
    @Bean(name = CHANNEL_NAME_IN)
    public MessageChannel mqttInboundChannel() {
        DirectChannel directChannel = new DirectChannel();
        return directChannel;
    }

    /**
     * MQTT消息处理器（生产者）
     *
     * @return {@link MessageHandler}
     */
    @Bean
    @ServiceActivator(inputChannel = CHANNEL_NAME_OUT)
    public MessageHandler mqttOutbound(KafkaTemplate kafkaTemplate) {
        KafkaProducerMessageHandler<String, String> handler =
                new KafkaProducerMessageHandler<>(kafkaTemplate);
        handler.setMessageKeyExpression(new LiteralExpression("mystox.kafka.key"));
        return handler;
    }

    @Bean
    public ConsumerFactory kafkaConsumerFactory(KafkaProperties properties) {
        Map<String, Object> consumerProperties = properties
                .buildConsumerProperties();
        consumerProperties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000);
        return new DefaultKafkaConsumerFactory<>(consumerProperties);
    }

    @Bean
    public KafkaMessageListenerContainer container(ConsumerFactory kafkaConsumerFactory) {
        KafkaMessageListenerContainer topic = new KafkaMessageListenerContainer<>(kafkaConsumerFactory,
                new ContainerProperties(new TopicPartitionInitialOffset("topic", 0)));
        return topic;
    }

    @Bean("inbound")
    public MessageProducer
    adapter(KafkaMessageListenerContainer<String, String> container) {
        KafkaMessageDrivenChannelAdapter<String, String> kafkaMessageDrivenChannelAdapter =
                new KafkaMessageDrivenChannelAdapter<>(container);
        kafkaMessageDrivenChannelAdapter.setOutputChannel(mqttInboundChannel());
        return kafkaMessageDrivenChannelAdapter;
    }

//    @Bean
//    public PollableChannel fromKafka() {
//        return new QueueChannel();
//    }

}

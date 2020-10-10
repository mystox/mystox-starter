package tech.mystox.framework.kafka.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import tech.mystox.framework.kafka.config.KafkaConfig;



/**
 * Created by mystoxlol on 2019/8/13, 11:05.
 * company: mystox
 * description:
 * update record:
 */
@MessageEndpoint
public class KafkaReceiver {
    private static final Logger logger = LoggerFactory.getLogger(KafkaReceiver.class);




    @ServiceActivator(inputChannel = KafkaConfig.CHANNEL_NAME_IN/*, outputChannel = CHANNEL_NAME_OUT*/)
    public void messageReceiver(Message<String> message) {


        System.out.println(message);

    }



}

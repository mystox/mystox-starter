package com.kongtrolink.framework.mqtt.service.impl;

import com.kongtrolink.framework.entity.MqttMsg;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;

import java.util.concurrent.Callable;

/**
 * Created by mystoxlol on 2019/9/5, 8:49.
 * company: kongtrolink
 * description:
 * update record:
 */
public class CallBackTopic implements Callable<MqttMsg> {

    private String replyTopic;

    MessageProducer reply;

    public CallBackTopic(String replyTopic, MessageProducer reply) {
        this.replyTopic = replyTopic;
        this.reply = reply;
    }

    @Override
    public MqttMsg call() throws Exception {
        System.out.println("等待回复。。。");
        MqttPahoMessageDrivenChannelAdapter mqttRely = (MqttPahoMessageDrivenChannelAdapter) reply;
        String ackTopic = "123";
        MqttMessage mqttMessage = new MqttMessage();
        Thread.sleep(6000L);
        mqttRely.messageArrived(ackTopic,mqttMessage);
        System.out.println("收到消息"+mqttMessage.toString());
        MqttMsg mqttMsg = new MqttMsg();
        mqttMsg.setPayload(mqttMessage.toString());
        return mqttMsg;
    }
}

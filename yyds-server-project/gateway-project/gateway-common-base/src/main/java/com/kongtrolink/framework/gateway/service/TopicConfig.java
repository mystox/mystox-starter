package com.kongtrolink.framework.gateway.service;

import com.kongtrolink.framework.gateway.mqtt.base.MqttPubTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 取得下发topic
 * Created by Mag on 2019/10/17.
 */
@Service
public class TopicConfig {

    @Value("${gatewayMqtt.version}")
    private String version;

    /**
     * iaiot/cloud/yy/sn/packetName/version
     */
    public String getFsuTopic(String sn, MqttPubTopic mqttPubTopic){
        return "iaiot/cloud/yy/"+sn+"/"+mqttPubTopic.getTopicName()+"/"+version;
    }
}

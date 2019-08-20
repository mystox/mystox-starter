package com.kongtrolink.framework.mqtt.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.mqtt.entity.MqttMsg;
import com.kongtrolink.framework.mqtt.entity.PayloadType;
import com.kongtrolink.framework.mqtt.service.IMqttSender;
import com.kongtrolink.framework.mqtt.service.MqttSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/8/19, 8:28.
 * company: kongtrolink
 * description: 封装的mqtt生产者
 * update record:
 */
@Service
public class MqttSenderImpl implements MqttSender {

    @Value("${mqtt.producer.defaultTopic}")
    private String producerDefaultTopic;

    @Value("${server.name}")
    private String serverName;

    @Value("${server.version}")
    private String serverVersion;

    @Autowired
    private IMqttSender mqttSender;


    @Override
    public void sendToMqtt(String serverCode, String operaCode, String payload) {
        String localServerCode = this.serverName + "_" + this.serverVersion;
        boolean existsByPubList = isExistsByPubList(localServerCode, operaCode);
        if (existsByPubList) {
            //获取目标topic列表，判断sub_list是否有人订阅处理
            if (isExistsBySubList(serverCode, operaCode)) {
                //组建topicid
                String topic = preconditionSend(serverCode, operaCode);
                //组建消息体
                MqttMsg mqttMsg = buildMqttMsg(topic, localServerCode,payload);
                mqttSender.sendToMqtt(topic, JSONObject.toJSONString(mqttMsg));
            }
        }
    }


    @Override
    public void sendToMqtt(String serverCode, String operaCode,
                           int qos, String payload) {
        String localServerCode = this.serverName + "_" + this.serverVersion;
        boolean existsByPubList = isExistsByPubList(localServerCode, operaCode);
        if (existsByPubList) {
            //获取目标topic列表，判断sub_list是否有人订阅处理
            if (isExistsBySubList(serverCode, operaCode)) {
                //组建topicid
                String topic = preconditionSend(serverCode, operaCode);
                mqttSender.sendToMqtt(topic, qos, payload);
            }
        }
    }

    private boolean isExistsByPubList(String serverCode, String operaCode) {
        //todo
        //是否已经发布
        return true;

    }

    private boolean isExistsBySubList(String serverCode, String operaCode) {
        //todo
        return true;
    }

    private String preconditionSend(String serverCode, String operaCode) {
        return serverCode + "/" + operaCode;
    }
    private MqttMsg buildMqttMsg(String topicId,String localServerCode, String payload) {
        MqttMsg mqttMsg = new MqttMsg();
        mqttMsg.setTopic(topicId);
        mqttMsg.setPayloadType(PayloadType.STRING);
        mqttMsg.setPayload(payload);
        mqttMsg.setSourceAddress(localServerCode);
        return mqttMsg;
    }

}

package com.kongtrolink.framework.demo.api;

import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.service.MqttSender;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by mystoxlol on 2019/9/9, 13:15.
 * company: kongtrolink
 * description: 发送消息的demo
 * update record:
 */
public class FooPublishImpl implements FooPublish {

    @Autowired
    MqttSender mqttSender;

    /**
     * 异步的接口实现案例
     * @param serverCode
     * @param operaCode
     * @param payload
     */
    @Override
    public void sendMsg(String serverCode, String operaCode, String payload) {
        mqttSender.sendToMqtt(serverCode, operaCode, payload);
    }

    /**
     * 同步阻塞的实现案例
     * @param serverCode
     * @param operaCode
     * @param payload
     * @return
     */
    @Override
    public MsgResult sendMesgSyn(String serverCode, String operaCode, String payload) {
        MsgResult s = mqttSender.sendToMqttSyn(serverCode, operaCode, payload);
        return s;
    }
}

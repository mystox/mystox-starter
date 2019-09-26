package com.kongtrolink.framework.api.impl;

import com.kongtrolink.framework.api.Publish;
import com.kongtrolink.framework.common.util.MqttUtils;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.entity.ServerName;
import com.kongtrolink.framework.service.MqttSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("MqttPublish")
public class MqttPublish implements Publish {

    @Autowired
    MqttSender mqttSender;

    @Override
    public MsgResult getRegionCode(String payload) {

        String serverCode = MqttUtils.preconditionServerCode(ServerName.AUTH_PLATFORM, "1.0.0");
        String operaCode = "getRegionCode";

        return mqttSender.sendToMqttSyn(serverCode, operaCode, payload);
    }

    @Override
    public void publishCIProps(String payload) {

        String serverCode = MqttUtils.preconditionSubTopicId(ServerName.ALARM_SERVER, "1.0.0");
        String opearCode = "getCIProps";

        mqttSender.sendToMqtt(serverCode, opearCode, payload);
    }
}

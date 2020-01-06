package com.kongtrolink.framework.api.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.api.Publish;
import com.kongtrolink.framework.common.util.MqttUtils;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.entity.ServerName;
import com.kongtrolink.framework.mqtt.service.MqttSender;
import com.kongtrolink.framework.utils.DeviceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service("MqttPublish")
public class MqttPublish implements Publish {

    @Autowired
    @Lazy
    MqttSender mqttSender;

    @Autowired
    DeviceUtils deviceUtils;

    @Override
    public MsgResult getRegionCode(String payload) {

        String serverCode = MqttUtils.preconditionServerCode(ServerName.AUTH_PLATFORM, "1.0.0");
        String operaCode = "getRegionCode";

        return mqttSender.sendToMqttSyn(serverCode, operaCode, payload);
    }

    @Override
    public void publishCIProps(String payload) {

        String serverCode = MqttUtils.preconditionServerCode(ServerName.ALARM_SERVER, "1.0.0");
        String operaCode = "getCIProps";

        mqttSender.sendToMqtt(serverCode, operaCode, payload);
    }

    @Override
    public void deviceGet(String sn, String gatewayServerCode) {

        String operaCode = "deviceGet";

        JSONObject payload = new JSONObject();
        payload.put("sn", sn);

        MsgResult msgResult = mqttSender.sendToMqttSyn(gatewayServerCode, operaCode, JSONObject.toJSONString(payload));
        if (msgResult.getStateCode() == 1) {
            deviceUtils.deviceGetAck(msgResult.getMsg());
        }
    }
}

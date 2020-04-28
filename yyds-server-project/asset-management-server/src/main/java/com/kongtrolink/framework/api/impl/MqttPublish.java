package com.kongtrolink.framework.api.impl;

import com.alibaba.fastjson.JSONObject;

import com.kongtrolink.framework.api.Publish;
import com.kongtrolink.framework.core.IaContext;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.service.MsgHandler;
import com.kongtrolink.framework.utils.DeviceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("MqttPublish")
public class MqttPublish implements Publish {

//    @Autowired
//    MqttOpera mqttOpera;

    @Autowired
    IaContext iaContext;
    @Autowired
    DeviceUtils deviceUtils;

    @Override
    public MsgResult getRegionCode(String payload) {
       MsgHandler msgHandler= iaContext.getIaENV().getMsgScheduler().getIahander();

        String operaCode = "getRegionCode";

        return msgHandler.opera(operaCode, payload);
    }

    @Override
    public void publishCIProps(String payload) {
        MsgHandler msgHandler= iaContext.getIaENV().getMsgScheduler().getIahander();
        String operaCode = "getCIProps";

        msgHandler.operaAsync(operaCode, payload);
    }

    @Override
    public void deviceGet(String sn, String gatewayServerCode) {
        MsgHandler msgHandler= iaContext.getIaENV().getMsgScheduler().getIahander();

        String operaCode = "deviceGet";

        JSONObject payload = new JSONObject();
        payload.put("sn", sn);

        MsgResult msgResult = msgHandler.opera(operaCode, JSONObject.toJSONString(payload));
        if (msgResult.getStateCode() == 1) {
            deviceUtils.deviceGetAck(msgResult.getMsg());
        }
    }
}

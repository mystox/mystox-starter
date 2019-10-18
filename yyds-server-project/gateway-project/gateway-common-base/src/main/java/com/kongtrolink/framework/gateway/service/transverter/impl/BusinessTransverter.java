package com.kongtrolink.framework.gateway.service.transverter.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.common.util.MqttUtils;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.entity.OperaCode;
import com.kongtrolink.framework.entity.ServerName;
import com.kongtrolink.framework.gateway.entity.ParseProtocol;
import com.kongtrolink.framework.gateway.mqtt.GatewayMqttSenderNative;
import com.kongtrolink.framework.gateway.mqtt.base.MqttPubTopic;
import com.kongtrolink.framework.gateway.service.TopicConfig;
import com.kongtrolink.framework.gateway.service.transverter.TransverterHandler;
import com.kongtrolink.framework.gateway.tower.entity.rec.Register;
import com.kongtrolink.framework.gateway.tower.entity.send.RegisterAck;
import com.kongtrolink.framework.gateway.tower.entity.send.base.SendBase;
import com.kongtrolink.framework.stereotype.Transverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by mystoxlol on 2019/10/16, 15:29.
 * company: kongtrolink
 * description: 其他业务转换器
 * update record:
 */
@Service
@Transverter(name = "businessCode")
public class BusinessTransverter extends TransverterHandler {

    private static final Logger logger = LoggerFactory.getLogger(BusinessTransverter.class);

    @Autowired
    GatewayMqttSenderNative gatewayMqttSenderNative;

    @Value("gateway.scloud.version:1.0.0")
    private String scloudServerVersion;
    @Autowired
    private TopicConfig topicConfig;

    @Override
    protected void transferExecute(ParseProtocol parseProtocol) {
        String packetName = parseProtocol.getMsgType();
        switch (packetName){
            case "Register":
                logger.info("SN:{} 注册");
                registerAck(parseProtocol.getSn(),parseProtocol.getPayload());
                break;
            case "Heartbeat":break;
            case "PushDeviceAsset":break;
            case "GetDeviceDataModelAck":break;
            case "PushRealtimeData":break;
            case "PushAlarm":break;
            case "SetDataAck":break;
            case "GetAlarmParamAck":break;
            case "SetAlarmParamAck":break;
            case "GetDeviceAlarmModelAck":break;
        }
    }


    private void registerAck(String sn,String json){
        MsgResult result = reportMsgSyn(MqttUtils.preconditionServerCode(ServerName.SCLOUD_SERVER,scloudServerVersion),
                OperaCode.Register,json);
        Register register = JSONObject.parseObject(json,Register.class);
        RegisterAck registerAck =  new RegisterAck();
        registerAck.setResult(1);
        registerAck.setTime(new Date().getTime());
        registerAck.setHeartbeatInterval(120);
        SendBase sendBase = new SendBase(register.getMsgId(),registerAck);
        String messageAck = JSONObject.toJSONString(sendBase);
        gatewayMqttSenderNative.sendToMqtt(messageAck,topicConfig.getFsuTopic(sn, MqttPubTopic.RegisterAck));

    }


}

package com.kongtrolink.framework.gateway.iaiot.server.service.transverter.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.gateway.iaiot.server.mqtt.base.MqttPubTopic;
import com.kongtrolink.framework.gateway.iaiot.server.service.transverter.TransverterHandler;
import com.kongtrolink.framework.gateway.iaiot.server.entity.ParseProtocol;
import com.kongtrolink.framework.gateway.iaiot.server.entity.Transverter;
import com.kongtrolink.framework.gateway.iaiot.server.mqtt.GatewayMqttSenderNative;
import com.kongtrolink.framework.gateway.iaiot.server.service.DeviceTypeConfig;
import com.kongtrolink.framework.gateway.iaiot.server.service.TopicConfig;
import com.kongtrolink.framework.gateway.iaiot.core.rec.Heartbeat;
import com.kongtrolink.framework.gateway.iaiot.core.rec.Register;
import com.kongtrolink.framework.gateway.iaiot.core.send.RegisterAck;
import com.kongtrolink.framework.gateway.iaiot.core.send.AckBase;
import com.kongtrolink.framework.gateway.iaiot.core.send.SendBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

/**
 * Created by mystoxlol on 2019/10/16, 15:29.
 * company: kongtrolink
 * description: 其他业务转换器
 * update record:
 */
@Transverter("businessTransverter")
public class BusinessTransverter extends TransverterHandler {

    private static final Logger logger = LoggerFactory.getLogger(BusinessTransverter.class);

    @Autowired
    GatewayMqttSenderNative gatewayMqttSenderNative;

    @Value("${gateway.scloud.version:1.0.0}")
    private String scloudServerVersion;
    @Autowired
    private TopicConfig topicConfig;
    @Autowired
    private DeviceTypeConfig deviceTypeConfig;

    @Override
    protected void transferExecute(ParseProtocol parseProtocol) {
        String packetName = parseProtocol.getMsgType();
        switch (packetName){
            case "Register":
                logger.info("SN:{} 注册",parseProtocol.getSn());
                registerAck(parseProtocol.getSn(),parseProtocol.getPayload());
                break;
            case "Heartbeat":
                logger.info("SN:{} 心跳",parseProtocol.getSn());
                heartAck(parseProtocol.getSn(),parseProtocol.getPayload());
                break;
            case "GetDeviceDataModelAck":break;
            case "PushRealtimeData":break;
            case "SetDataAck":break;
            case "GetAlarmParamAck":break;
            case "SetAlarmParamAck":break;
            case "GetDeviceAlarmModelAck":break;
            case "GatewaySystemSetDevType": //刷新redis中 资管设备类型映射表
                deviceTypeConfig.reFlashRedisDeviceType();
                break;

        }
    }


    private void registerAck(String sn,String json){
//        MsgResult result = reportMsgSyn(MqttUtils.preconditionServerCode(ServerName.SCLOUD_SERVER,scloudServerVersion),
//                OperaCode.Register,json);
        Register register = JSONObject.parseObject(json,Register.class);
        RegisterAck registerAck =  new RegisterAck();
        registerAck.setResult(1);
        registerAck.setTime(new Date().getTime());
        registerAck.setHeartbeatInterval(120);
        SendBase sendBase = new SendBase(register.getMsgId(),registerAck);
        String messageAck = JSONObject.toJSONString(sendBase);
        gatewayMqttSenderNative.sendToMqtt(messageAck,topicConfig.getFsuTopic(sn, MqttPubTopic.RegisterAck));

    }

    private void heartAck(String sn,String json){
        Heartbeat heartbeat = JSONObject.parseObject(json,Heartbeat.class);
        AckBase ackBase = new AckBase(heartbeat.getMsgId());
        String messageAck = JSONObject.toJSONString(ackBase);
        gatewayMqttSenderNative.sendToMqtt(messageAck,topicConfig.getFsuTopic(sn, MqttPubTopic.HeartbeatAck));
    }
}

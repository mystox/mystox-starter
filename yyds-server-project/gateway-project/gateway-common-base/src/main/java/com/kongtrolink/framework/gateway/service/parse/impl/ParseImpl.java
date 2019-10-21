package com.kongtrolink.framework.gateway.service.parse.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.gateway.entity.ParseProtocol;
import com.kongtrolink.framework.gateway.mqtt.GatewayMqttSenderNative;
import com.kongtrolink.framework.gateway.service.parse.ParseHandler;
import com.kongtrolink.framework.gateway.service.transverter.impl.AlarmTransverter;
import com.kongtrolink.framework.gateway.service.transverter.impl.AssetTransverter;
import com.kongtrolink.framework.gateway.service.transverter.impl.BusinessTransverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/10/16, 15:04.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class ParseImpl extends ParseHandler {

    private Logger logger = LoggerFactory.getLogger(ParseImpl.class);

    @Autowired
    RedisUtils redisUtils;
    @Autowired
    AlarmTransverter alarmTransverter;
    @Autowired
    AssetTransverter assetTransverter;
    @Autowired
    BusinessTransverter businessTransverter;
    @Autowired
    GatewayMqttSenderNative gatewayMqttSenderNative;

    @Override
    public void execute(String payload) {
        ParseProtocol parseProtocol  = JSONObject.parseObject(payload,ParseProtocol.class);
        String packetName = parseProtocol.getMsgType();
        switch (packetName){
            case "Register":
                logger.info("SN:{} 注册",parseProtocol.getSn());
                businessTransverter.transfer(parseProtocol);
                break;
            case "Heartbeat":break;
            case "PushDeviceAsset":
                assetTransverter.transfer(parseProtocol);
                break;
            case "GetDeviceDataModelAck":break;
            case "PushRealtimeData":break;
            case "PushAlarm":
                alarmTransverter.transfer(parseProtocol);
                break;
            case "SetDataAck":break;
            case "GetAlarmParamAck":break;
            case "SetAlarmParamAck":break;
            case "GetDeviceAlarmModelAck":break;
            case "GatewaySystemSetDevType": //刷新redis中 资管设备类型映射表
                logger.info("刷新redis中 资管设备类型映射表");
                businessTransverter.transfer(parseProtocol);
                break;
        }
        //根据消息类型获取协议转换器执行
        redisUtils.get("");
    }



}

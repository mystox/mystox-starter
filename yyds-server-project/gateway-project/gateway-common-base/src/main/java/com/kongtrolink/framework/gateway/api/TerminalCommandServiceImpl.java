package com.kongtrolink.framework.gateway.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.entity.StateCode;
import com.kongtrolink.framework.gateway.mqtt.GatewayMqttSenderNative;
import com.kongtrolink.framework.gateway.mqtt.base.MqttPubTopic;
import com.kongtrolink.framework.gateway.service.TopicConfig;
import com.kongtrolink.framework.gateway.service.transverter.impl.AssetTransverter;
import com.kongtrolink.framework.gateway.tower.entity.rec.base.RecServerBase;
import com.kongtrolink.framework.gateway.tower.entity.rec.info.PushDeviceAssetDeviceList;
import com.kongtrolink.framework.gateway.tower.entity.send.base.AckBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by mystoxlol on 2019/9/9, 13:03.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class TerminalCommandServiceImpl implements TerminalCommandService {

    private static final Logger logger = LoggerFactory.getLogger(TerminalCommandServiceImpl.class);

    @Autowired
    GatewayMqttSenderNative gatewayMqttSenderNative;
    @Autowired
    private TopicConfig topicConfig;
    @Autowired
    private AssetTransverter assetTransverter;
    /**
     * 资管主动下发 下发设备获取设备信息
     * @param message 消息体
     * @return 结果
     */
    @Override
    public String deviceGet(String message) {
        RecServerBase recServerBase = JSONObject.parseObject(message,RecServerBase.class);
        String sn = recServerBase.getSn();
        AckBase ackBase = new AckBase();
        String msgId = 1+""+new Date().getTime();
        ackBase.setMsgId(msgId);
        String s = JSONObject.toJSONString(ackBase);
        MsgResult result = gatewayMqttSenderNative.sendToMqttSyn(msgId,s,topicConfig.getFsuTopic(sn, MqttPubTopic.GetDeviceAsset));
        logger.info("MsgResult :{} " ,result.toString());
        if(StateCode.SUCCESS != result.getStateCode()){
            return JSONObject.toJSONString(result);
        }
        PushDeviceAssetDeviceList deviceList = JSONObject.parseObject(result.getMsg(),PushDeviceAssetDeviceList.class);
        return assetTransverter.getAssetServerResult(deviceList,sn);
    }

    /**
     * 业务平台 设置数值
     * @param message 消息体
     * @return  结果
     */
    @Override
    public String setData(String message) {
        return getMsgResult(message,MqttPubTopic.SetData);
    }

    @Override
    public String getDeviceDataModel(String message) {
        return getMsgResult(message,MqttPubTopic.GetDeviceDataModel);
    }

    @Override
    public String getAlarmParam(String message) {
        return getMsgResult(message,MqttPubTopic.GetAlarmParam);
    }

    @Override
    public String setAlarmParam(String message) {
        return getMsgResult(message,MqttPubTopic.SetAlarmParam);
    }

    @Override
    public String getDeviceAlarmModel(String message) {
        return getMsgResult(message,MqttPubTopic.GetDeviceAlarmModel);
    }



    private String getMsgResult(String message, MqttPubTopic topic){
        try{
            RecServerBase recServerBase = JSONObject.parseObject(message,RecServerBase.class);
            String sn = recServerBase.getSn();
            String payload = recServerBase.getPayload();
            JSONObject json = (JSONObject) JSON.toJSON(payload);
            String msgId = json.getString("msgId");
            MsgResult result = gatewayMqttSenderNative.sendToMqttSyn(msgId,payload,topicConfig.getFsuTopic(sn, topic));
            if(result==null){
                return null;
            }
            return result.getMsg();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}

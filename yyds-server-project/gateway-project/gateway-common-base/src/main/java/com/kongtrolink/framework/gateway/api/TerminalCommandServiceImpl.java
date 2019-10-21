package com.kongtrolink.framework.gateway.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.entity.StateCode;
import com.kongtrolink.framework.gateway.mqtt.GatewayMqttSenderNative;
import com.kongtrolink.framework.gateway.mqtt.base.MqttPubTopic;
import com.kongtrolink.framework.gateway.service.TopicConfig;
import com.kongtrolink.framework.gateway.service.transverter.impl.AssetTransverter;
import com.kongtrolink.framework.gateway.tower.entity.rec.PushDeviceAsset;
import com.kongtrolink.framework.gateway.tower.entity.rec.base.RecServerBase;
import com.kongtrolink.framework.gateway.tower.entity.send.GetDeviceDataModel;
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
        GetDeviceDataModel getDeviceDataModel = new GetDeviceDataModel();
        String s = JSONObject.toJSONString(getDeviceDataModel);
        String msgId = 1+""+new Date().getTime();
        MsgResult result = gatewayMqttSenderNative.sendToMqttSyn(msgId,s,topicConfig.getFsuTopic(sn, MqttPubTopic.GetDeviceAsset));
        if(StateCode.FAILED == result.getStateCode()){
            return JSONObject.toJSONString(result);
        }
        PushDeviceAsset alarmReport = JSONObject.parseObject(result.getMsg(),PushDeviceAsset.class);
        return assetTransverter.getAssetServerResult(alarmReport,sn);
    }

    /**
     * 业务平台 设置数值
     * @param message 消息体
     * @return  结果
     */
    @Override
    public String setData(String message) {
        try {
            RecServerBase recServerBase = JSONObject.parseObject(message,RecServerBase.class);
            String sn = recServerBase.getSn();
            String payload = recServerBase.getPayload();
            JSONObject json = (JSONObject) JSON.toJSON(payload);
            String msgId = json.getString("msgId");
            MsgResult result = gatewayMqttSenderNative.sendToMqttSyn(msgId,payload,topicConfig.getFsuTopic(sn, MqttPubTopic.SetData));
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

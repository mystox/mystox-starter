package com.kongtrolink.framework.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.YwclMessage;
import com.kongtrolink.framework.execute.module.RpcModule;
import com.kongtrolink.framework.model.PktType;
import com.kongtrolink.framework.service.DataMntService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * \* @Author: mystox
 * \* Date: 2018/12/1 10:47
 * \* Description:
 * \
 */
@Service
public class DataMntServiceImpl implements DataMntService {

    @Autowired
    RpcModule rpcModule;

    @Override
    public JSONObject setFsu(Map fsuMap) {

        return null;
    }

    @Override
    public JSONObject getSignalList(Map<String, Object> requestBody, String fsuId) {
        if (requestBody == null) return null;
        YwclMessage ywclMessage = new YwclMessage(PktType.GET_DATA,System.currentTimeMillis(),fsuId);
        ywclMessage.setData(JSON.toJSONString(requestBody));
        JSONObject result = rpcModule.syncRequestData(ywclMessage,JSONObject.class);
        return result;
    }
    @Override
    public JSONObject getSignalListHistory(Map<String, Object> requestBody, String fsuId)
    {
        if (requestBody == null) return null;
        YwclMessage ywclMessage = new YwclMessage(PktType.GET_HISTORY_DATA,System.currentTimeMillis(),fsuId);
        ywclMessage.setData(JSON.toJSONString(requestBody));
        JSONObject result = rpcModule.syncRequestData(ywclMessage,JSONObject.class);
        return result;
    }

    @Override
    public JSONObject setSignal(Map<String, Object> requestBody, String fsuId)
    {
        if (requestBody==null)
        {
            return null;
        }
        YwclMessage ywclMessage= new YwclMessage(PktType.SET_DATA,System.currentTimeMillis(),fsuId);
        ywclMessage.setData(JSON.toJSONString(requestBody));
        JSONObject result = rpcModule.syncRequestData(ywclMessage,JSONObject.class);
        return result;
    }

    @Override
    public JSONObject setThreshold(Map<String, Object> requestBody, String fsuId) {
        if (requestBody==null)
        {
           return null;
        }
        YwclMessage ywclMessage= new YwclMessage(PktType.SET_ALARM_PARAM,System.currentTimeMillis(),fsuId);
        ywclMessage.setData(JSON.toJSONString(requestBody));
        JSONObject result = rpcModule.syncRequestData(ywclMessage,JSONObject.class,60000L);
        return result;
    }

    @Override
    public JSONObject getAlarmList(Map<String, Object> requestBody, String fsuId) {
        if (requestBody == null) return null;
        YwclMessage ywclMessage = new YwclMessage(PktType.GET_ALARMS,System.currentTimeMillis(),fsuId);
        ywclMessage.setData(JSON.toJSONString(requestBody));
//        JSONObject result = rpcModule.syncRequestData(ywclMessage,omcTopic,JSONObject.class,mqttTimeout);
        JSONObject result = rpcModule.syncRequestData(ywclMessage,  JSONObject.class);
        return result;

    }

    @Override
    public JSONObject getDeviceList(Map<String, Object> requestBody) {

        return null;
    }

    @Override
    public JSONObject getThreshold(Map<String, Object> requestBody, String fsuId) {
        if (requestBody == null) return null;
        YwclMessage ywclMessage = new YwclMessage(PktType.GET_ALARM_PARAM,System.currentTimeMillis(),fsuId);
        ywclMessage.setData(JSON.toJSONString(requestBody));
        JSONObject result = rpcModule.syncRequestData(ywclMessage,JSONObject.class);
        return result;
    }


}
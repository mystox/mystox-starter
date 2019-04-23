package com.kongtrolink.framework.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.core.entity.PktType;
import com.kongtrolink.framework.execute.module.RpcModule;
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
    public JSONObject getSignalList(Map<String, Object> requestBody, String sn) {


        if (requestBody == null) return null;
        ModuleMsg moduleMsg = new ModuleMsg(PktType.GET_DATA, sn);
        moduleMsg.setPayload((JSONObject) JSON.toJSON(requestBody));
        JSONObject result = rpcModule.syncRequestData(moduleMsg, JSONObject.class);
        return result;
    }

    @Override
    public JSONObject getSignalListHistory(Map<String, Object> requestBody, String sn) {
        if (requestBody == null) return null;
        ModuleMsg moduleMsg = new ModuleMsg(PktType.GET_HISTORY_DATA, sn);
        moduleMsg.setPayload((JSONObject) JSON.toJSON(requestBody));
        JSONObject result = rpcModule.syncRequestData(moduleMsg, JSONObject.class);
        return result;
    }

    @Override
    public JSONObject setSignal(Map<String, Object> requestBody, String sn) {
        if (requestBody == null) return null;
        requestBody.put("SN", sn);
        ModuleMsg moduleMsg = new ModuleMsg(PktType.SET_DATA, sn);
        moduleMsg.setPayload((JSONObject) JSON.toJSON(requestBody));
        JSONObject result = rpcModule.syncRequestData(moduleMsg, JSONObject.class);
        return result;
    }


    @Override
    public JSONObject setThreshold(Map<String, Object> requestBody, String sn) {
        if (requestBody == null) {
            return null;
        }
        ModuleMsg moduleMsg = new ModuleMsg(PktType.SET_ALARM_PARAM, sn);
        moduleMsg.setPayload((JSONObject) JSON.toJSON(requestBody));
        JSONObject result = rpcModule.syncRequestData(moduleMsg, JSONObject.class);
        return result;
    }

    @Override
    public JSONArray getAlarmList(Map<String, Object> requestBody, String sn) {
        ModuleMsg moduleMsg = new ModuleMsg(PktType.GET_ALARMS, sn);
        moduleMsg.setPayload((JSONObject) JSON.toJSON(requestBody));
        JSONArray result = rpcModule.syncRequestData(moduleMsg, JSONArray.class);
        return result;

    }

    @Override
    public JSONObject getDeviceList(Map<String, Object> requestBody) {

        return null;
    }

    @Override
    public JSONArray getThreshold(Map<String, Object> requestBody, String sn) {
        if (requestBody == null) return null;
        ModuleMsg moduleMsg = new ModuleMsg(PktType.GET_ALARM_PARAM, sn);
        moduleMsg.setPayload((JSONObject) JSON.toJSON(requestBody));
        JSONArray result = rpcModule.syncRequestData(moduleMsg, JSONArray.class);
        return result;
    }


}
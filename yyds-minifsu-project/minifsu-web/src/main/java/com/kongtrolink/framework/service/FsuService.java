package com.kongtrolink.framework.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.Fsu;
import com.kongtrolink.framework.model.OperatHistory;

import java.util.List;
import java.util.Map;

/**
 * \* @Author: mystox
 * \* Date: 2018/12/1 10:46
 * \* Description:
 * \
 */
public interface FsuService {
    JSONObject setFsu(Map<String, Object> requestBody);

    Fsu getFsu(Map<String, Object> requestBody);


    JSONObject upgrade(Map<String, Object> requestBody, String fsuId);

    JSONObject compiler(JSONObject requestBody, String sn);

    JSONArray getDeviceList(Map<String, Object> requestBody, String fsuId);

    List<Fsu>  getFsuListByCoordinate(Map fsuMap);

    JSONObject listFsu(Map<String, Object> requestBody);

    List<Fsu> searchFsu(Map<String, Object> requestBody);

    JSONObject getFsuStatus(Map<String, Object> requestBody,String fsuId);

    List<OperatHistory> getOperationHistory(Map<String, Object> requestBody, String fsuId);

    Map<String,Integer> getFsuDeviceCountMap(List<String> fsuIds);

    JSONObject getOperationHistoryByMqtt(Map<String, Object> requestBody, String fsuId);

    JSONObject logoutFsu(Map<String, Object> requestBody, String fsuId);

    JSONObject saveTerminal(JSONArray snList);

    JSONObject terminalReboot(Map<String, Object> requestBody, String sn);

    JSONObject setGprs(Map<String, Object> requestBody, String sn);

    JSONObject saveSignalModelList(JSONArray signalModelList);

    JSONObject saveAlarmModelList(JSONArray alarmSignalList);

    JSONObject unbind(Map<String, Object> requestBody,String sn);

    JSONObject getRunState(Map<String, Object> requestBody, String sn);

    JSONObject getTerminalPayload(Map<String, Object> requestBody, String sn);

    JSONObject getCompilerDeviceInfo(JSONObject compilerBody, String sn);
    JSONObject compilerFile(JSONObject compilerBody);

    JSONObject getCompilerConfig(Map<String, Object> requestBody, String sn);
    JSONObject getEngineInfo(JSONObject compilerBody, String sn);

}
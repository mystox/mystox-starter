package com.kongtrolink.framework.execute.module.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.Fsu;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.core.entity.OperatHistory;

import java.util.List;
import java.util.Map;

/**
 * Created by mystoxlol on 2019/3/25, 10:34.
 * company: kongtrolink
 * description:
 * update record:
 */
public interface TerminalService
{
    JSONObject setFsu(Map<String, Object> requestBody);

    Fsu getFsu(ModuleMsg requestBody);


    JSONObject upgrade(Map<String, Object> requestBody, String fsuId);

    JSONObject compiler(Map<String, Object> requestBody);

    JSONArray getDeviceList(ModuleMsg moduleMsg);

    JSONObject deleteDevice(Map<String, Object> requestBody);

    JSONObject addDevice(Map<String, Object> requestBody, String fsuId);

    List<Fsu> getFsuListByCoordinate(Map fsuMap);

    JSONObject listFsu(ModuleMsg requestBody);

    List<Fsu> searchFsu(Map<String, Object> requestBody);

    JSONObject getFsuStatus(Map<String, Object> requestBody,String fsuId);

    List<OperatHistory> getOperationHistory(Map<String, Object> requestBody, String fsuId);

    Map<String,Integer> getFsuDeviceCountMap(List<String> fsuIds);

    JSONObject getOperationHistoryByMqtt(Map<String, Object> requestBody, String fsuId);

    JSONObject logoutFsu(Map<String, Object> requestBody, String fsuId);

    JSONObject saveTerminal(ModuleMsg moduleMsg);

    JSONObject setTerminal(ModuleMsg moduleMsg);

    JSONObject terminalLogSave(ModuleMsg moduleMsg);

    JSONObject TerminalStatus(ModuleMsg moduleMsg);

    JSONObject getRunStates(ModuleMsg moduleMsg);

    JSONObject getTerminalPayloadLog(ModuleMsg moduleMsg);

    JSONObject unBind(ModuleMsg moduleMsg);
}

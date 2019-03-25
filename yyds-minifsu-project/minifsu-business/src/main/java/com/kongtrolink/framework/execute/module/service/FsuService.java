package com.kongtrolink.framework.execute.module.service;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.Fsu;
import com.kongtrolink.framework.core.entity.OperatHistory;

import java.util.List;
import java.util.Map;

/**
 * Created by mystoxlol on 2019/3/25, 10:34.
 * company: kongtrolink
 * description:
 * update record:
 */
public interface FsuService
{
    JSONObject setFsu(Map<String, Object> requestBody);

    Fsu getFsu(Map<String, Object> requestBody);


    JSONObject upgrade(Map<String, Object> requestBody, String fsuId);

    JSONObject compiler(Map<String, Object> requestBody);

    JSONObject getDeviceList(Map<String, Object> requestBody, String fsuId);

    JSONObject deleteDevice(Map<String, Object> requestBody);

    JSONObject addDevice(Map<String, Object> requestBody, String fsuId);

    List<Fsu> getFsuListByCoordinate(Map fsuMap);

    List<Fsu> listFsu(Map<String, Object> requestBody);

    List<Fsu> searchFsu(Map<String, Object> requestBody);

    JSONObject getFsuStatus(Map<String, Object> requestBody,String fsuId);

    List<OperatHistory> getOperationHistory(Map<String, Object> requestBody, String fsuId);

    Map<String,Integer> getFsuDeviceCountMap(List<String> fsuIds);

    JSONObject getOperationHistoryByMqtt(Map<String, Object> requestBody, String fsuId);

    JSONObject logoutFsu(Map<String, Object> requestBody, String fsuId);
}

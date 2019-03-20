package com.kongtrolink.yyjw.service;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.yyjw.model.Fsu;
import com.kongtrolink.yyjw.model.OperatHistory;

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

    JSONObject compiler(Map<String, Object> requestBody);

    JSONObject getDeviceList(Map<String, Object> requestBody, String fsuId);

    JSONObject deleteDevice(Map<String, Object> requestBody);

    JSONObject addDevice(Map<String, Object> requestBody, String fsuId);

    List<Fsu>  getFsuListByCoordinate(Map fsuMap);

    List<Fsu> listFsu(Map<String, Object> requestBody);

    List<Fsu> searchFsu(Map<String, Object> requestBody);

    JSONObject getFsuStatus(Map<String, Object> requestBody,String fsuId);

    List<OperatHistory> getOperationHistory(Map<String, Object> requestBody, String fsuId);

    Map<String,Integer> getFsuDeviceCountMap(List<String> fsuIds);

    JSONObject getOperationHistoryByMqtt(Map<String, Object> requestBody, String fsuId);

    JSONObject logoutFsu(Map<String, Object> requestBody, String fsuId);
}
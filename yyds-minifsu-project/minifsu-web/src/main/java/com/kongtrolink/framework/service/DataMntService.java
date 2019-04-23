package com.kongtrolink.framework.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * \* @Author: mystox
 * \* Date: 2018/12/1 10:47
 * \* Description:
 * \
 */
public interface DataMntService {
    JSONObject setFsu(Map fsuMap);

    JSONObject getSignalList(Map<String, Object> requestBody, String fsuId);

    JSONObject setThreshold(Map<String, Object> requestBody, String fsuId);

    JSONArray getAlarmList(Map<String, Object> requestBody, String fsuId);

    JSONObject getDeviceList(Map<String, Object> requestBody);

    JSONArray getThreshold(Map<String, Object> requestBody, String fsuId);

    JSONObject getSignalListHistory(Map<String, Object> requestBody, String fsuId);

    JSONObject setSignal(Map<String, Object> requestBody, String fsuId);

}
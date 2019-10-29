package com.kongtrolink.framework.api.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.api.Service;
import com.kongtrolink.framework.dao.DBService;
import com.kongtrolink.framework.utils.DeviceUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

@org.springframework.stereotype.Service("MqttService")
public class MqttService implements Service {

    @Resource(name = "Neo4jDBService")
    private DBService dbService;

    @Autowired
    DeviceUtils deviceUtils;

    public String getCIModel(String payload) {

        JSONObject jsonObject = JSONObject.parseObject(payload);
        JSONArray jsonArray = dbService.searchCIModel(jsonObject);

        return JSONObject.toJSONString(jsonArray);
    }

    public String getCIIds(String payload) {

        JSONObject jsonObject = JSONObject.parseObject(payload);
        JSONArray jsonArray = dbService.searchCIIds(jsonObject);

        return JSONObject.toJSONString(jsonArray);
    }

    public String getCIProp(String payload) {

        JSONObject jsonObject = JSONObject.parseObject(payload);
        JSONObject result = dbService.searchCIProp(jsonObject);

        return JSONObject.toJSONString(result);
    }

    public String getCI(String payload) {

        JSONObject jsonObject = JSONObject.parseObject(payload);
        JSONObject result = dbService.searchCI(jsonObject);

        return JSONObject.toJSONString(result);
    }

    public void deviceReport(String payload) {

        deviceUtils.deviceReport(payload);
    }
}

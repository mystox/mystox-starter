package com.kongtrolink.framework.api.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.api.Service;
import com.kongtrolink.framework.dao.DBService;

import javax.annotation.Resource;

@org.springframework.stereotype.Service("MqttService")
public class MqttService implements Service {

    @Resource(name = "Neo4jDBService")
    private DBService dbService;

    public void reportCI(String payload) {

    }

    public String getCIModel(String payload) {

        JSONObject jsonObject = JSONObject.parseObject(payload);
        JSONArray jsonArray = dbService.searchCIModel(jsonObject);

        return JSONObject.toJSONString(jsonArray);
    }

    public String getCI(String payload) {

        return "";
    }
}

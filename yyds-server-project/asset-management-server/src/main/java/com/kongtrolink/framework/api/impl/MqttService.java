package com.kongtrolink.framework.api.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.api.Service;
import com.kongtrolink.framework.dao.DBService;
import com.kongtrolink.framework.dao.impl.Neo4jDBService;
import com.kongtrolink.framework.entity.DBResult;
import com.kongtrolink.framework.utils.DeviceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;

@org.springframework.stereotype.Service("MqttService")
public class MqttService implements Service {

    private Logger logger = LoggerFactory.getLogger(MqttService.class);

    @Resource(name = "Neo4jDBService")
    private DBService dbService;

    @Autowired
    DeviceUtils deviceUtils;

    @Value("${server.name}_${server.version}")
    private String serverCode;

    public String getCIModel(String payload) {

        JSONObject jsonObject = JSONObject.parseObject(payload);

        JSONArray result = new JSONArray();

        try {
            DBResult dbResult = dbService.searchCIModel(jsonObject);
            if (dbResult.getResult()) {
                result = dbResult.getJsonArray();
            }
        } catch (Exception e) {
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return JSONObject.toJSONString(result);
    }

    public String getCIIds(String payload) {

        JSONObject jsonObject = JSONObject.parseObject(payload);

        JSONArray result = new JSONArray();

        try {
            DBResult dbResult = dbService.searchCIIds(jsonObject);
            if (dbResult.getResult()) {
                result = dbResult.getJsonArray();
            }
        } catch (Exception e) {
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return JSONObject.toJSONString(result);
    }

    public String getCIProp(String payload) {

        JSONObject jsonObject = JSONObject.parseObject(payload);

        JSONObject result = new JSONObject();

        try {
            DBResult dbResult = dbService.searchCIProp(jsonObject);
            if (dbResult.getResult()) {
                result = dbResult.getJsonObject();
            }
        } catch (Exception e) {
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return JSONObject.toJSONString(result);
    }

    public String getCI(String payload) {

        JSONObject jsonObject = JSONObject.parseObject(payload);

        JSONObject result = new JSONObject();

        try {
            DBResult dbResult = dbService.searchCI(jsonObject);
            if (dbResult.getResult()) {
                result.put("count", dbResult.getCount());
                result.put("infos", dbResult.getJsonArray());
            }
        } catch (Exception e) {
            logger.error(JSONObject.toJSONString(e), serverCode);
        }


        return JSONObject.toJSONString(result);
    }

    public void deviceReport(String payload) {

        deviceUtils.deviceReport(payload);
    }
}

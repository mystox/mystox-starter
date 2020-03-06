package com.kongtrolink.framework.api.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.api.Service;
import com.kongtrolink.framework.dao.DBService;
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
            if (dbResult.getResult() != 0) {
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
            if (dbResult.getResult() != 0) {
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
            if (dbResult.getResult() != 0) {
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
            DBResult dbResult = dbService.searchCI_V2(jsonObject);
            result.put("result", dbResult.getResult());
            result.put("info", dbResult.getInfo());
            if (dbResult.getResult() != 0) {
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

    public String addCI(String payload) {

        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("count", 0);
        result.put("infos", new JSONArray());

        JSONArray array = JSONArray.parseArray(payload);

        int count = 0;
        for (int i = 0; i < array.size(); ++i) {
            JSONObject jsonObject = array.getJSONObject(i);

            try {
                DBResult dbResult = dbService.addCI(jsonObject);
                if (dbResult.getResult() != 0) {
                    count++;
                    result.getJSONArray("infos").add(jsonObject.getString("sn"));
                }

                if (jsonObject.containsKey("_parent")) {
                    String id = dbResult.getJsonObject().getString("id");

                    JSONObject request = new JSONObject();
                    request.put("enterpriseCode", jsonObject.getString("enterpriseCode"));
                    request.put("serverCode", jsonObject.getString("serverCode"));
                    request.put("type", jsonObject.getJSONObject("_parent").getString("type"));
                    request.put("sn", jsonObject.getJSONObject("_parent").getString("sn"));

                    dbResult = dbService.searchCI(request);
                    if (dbResult.getResult() != 0 && dbResult.getCount() == 1) {
                        String parentId = dbResult.getJsonArray().getJSONObject(0).getString("id");

                        request = new JSONObject();
                        request.put("id1", parentId);
                        request.put("id2", id);
                        request.put("type", "Logical");

                        dbService.addCIRelationship(request);
                    }
                }

            } catch (Exception e) {
                logger.error(JSONObject.toJSONString(e), serverCode);
            }
        }

        result.put("result", 1);
        result.put("count", count);

        return JSONObject.toJSONString(result);
    }

    public String deleteCI(String payload) {
        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("count", 0);
        result.put("infos", new JSONArray());

        JSONArray array = JSONArray.parseArray(payload);

        int count = 0;
        for (int i = 0; i < array.size(); ++i) {
            JSONObject jsonObject = array.getJSONObject(i);

            try {
                JSONObject request = new JSONObject();
                request.put("enterpriseCode", jsonObject.getString("enterpriseCode"));
                request.put("serverCode", jsonObject.getString("serverCode"));
                request.put("sn", jsonObject.getString("sn"));

                DBResult dbResult = dbService.searchCI(request);
                if (dbResult.getResult() != 0 && dbResult.getCount() == 1) {
                    String id = dbResult.getJsonArray().getJSONObject(0).getString("id");

                    request = new JSONObject();
                    request.put("id", id);

                    dbResult = dbService.deleteCI(request);
                    if (dbResult.getResult() != 0) {
                        count++;
                        result.getJSONArray("infos").add(id);
                    }
                }
            } catch (Exception e) {
                logger.error(JSONObject.toJSONString(e), serverCode);
            }
        }

        result.put("result", 1);
        result.put("count", count);

        return JSONObject.toJSONString(result);
    }

    public String modifyCI(String payload) {
        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("count", 0);
        result.put("infos", new JSONArray());

        try {
            JSONObject jsonObject = JSONObject.parseObject(payload);

            JSONObject request = new JSONObject();
            request.put("enterpriseCode", jsonObject.getString("enterpriseCode"));
            request.put("serverCode", jsonObject.getString("serverCode"));
            request.put("sn", jsonObject.getString("sn"));

            DBResult dbResult = dbService.searchCI(request);
            if (dbResult.getResult() != 0 && dbResult.getCount() == 1) {

                jsonObject.put("id", dbResult.getJsonArray().getJSONObject(0).getString("id"));
                dbResult = dbService.modifyCI(jsonObject);
                result.put("result", dbResult.getResult());
                result.put("info", dbResult.getInfo());
            }
        } catch (Exception e) {
            result.put("info", e.getMessage());
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return JSONObject.toJSONString(result);
    }
}

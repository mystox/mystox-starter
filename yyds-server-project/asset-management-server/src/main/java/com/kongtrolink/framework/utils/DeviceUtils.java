package com.kongtrolink.framework.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.dao.DBService;
import com.kongtrolink.framework.entity.DBResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class DeviceUtils {

    private Logger logger = LoggerFactory.getLogger(DeviceUtils.class);

    @Value("${server.name}_${server.version}")
    private String selfServerCode;

    @Resource(name = "Neo4jDBService")
    private DBService dbService;

    public void deviceReport(String payload) {

        JSONObject jsonObject = JSONObject.parseObject(payload);

        JSONArray deviceList = jsonObject.getJSONArray("childDevices");
        jsonObject.remove("childDevices");

        String id = mergeDevice(jsonObject);
        if (id.equals("")) {
            return;
        }

        for (int i = 0; i < deviceList.size(); ++i) {
            JSONObject device = deviceList.getJSONObject(i);
            String childId = mergeDevice(device);

            if (childId.equals("")) {
                continue;
            }

            JSONObject relationship = new JSONObject();
            relationship.put("id1", id);
            relationship.put("id2", childId);
            relationship.put("type", "Logical");

            try {
                DBResult dbResult = dbService.addCIRelationship(relationship);
                if (!dbResult.getResult()) {
                    logger.error(JSONObject.toJSONString(relationship) + " addCIRelationship failed:" + dbResult.getInfo(), selfServerCode);
                }
            } catch (Exception e) {
                logger.error(JSONObject.toJSONString(e), selfServerCode);
            }
        }

    }

    public void deviceGetAck(String payload) {

        JSONObject jsonObject = JSONObject.parseObject(payload);

        if (jsonObject.keySet().size() == 0) {
            //该回复报文中无设备信息
            return;
        }

        JSONArray deviceList = jsonObject.getJSONArray("childDevices");
        jsonObject.remove("childDevices");

        String id = mergeDevice(jsonObject);
        if (id.equals("")) {
            return;
        }

        String enterpriseCode = jsonObject.getString("enterpriseCode");
        String serverCode = jsonObject.getString("serverCode");

        initRelationship(id, enterpriseCode, serverCode);

        for (int i = 0; i < deviceList.size(); ++i) {
            JSONObject device = deviceList.getJSONObject(i);
            String childId = mergeDevice(device);

            if (childId.equals("")) {
                continue;
            }

            String type1 = jsonObject.getString("type");
            String type2 = device.getString("type");

            JSONObject request = new JSONObject();
            request.put("parent", type1);
            request.put("child", type2);

            JSONArray array;
            try {
                DBResult dbResult = dbService.searchCITypeConnectionRelationship(request);
                if (!dbResult.getResult()) {
                    continue;
                }

                array = dbResult.getJsonArray();
                if (array.size() == 0) {
                    request.put("parent", type2);
                    request.put("child", type1);

                    dbResult = dbService.searchCITypeConnectionRelationship(request);
                    if (!dbResult.getResult()) {
                        continue;
                    }
                    array = dbResult.getJsonArray();
                }

                if (array.size() == 0) {
                    continue;
                }
            } catch (Exception e) {
                logger.error(JSONObject.toJSONString(e), selfServerCode);
                continue;
            }

            String type = "";
            for (int j = 0; j < array.size(); ++j) {
                JSONObject connectionType = array.getJSONObject(j);
                String tmp = connectionType.getString("type");
                if (!tmp.equals("Logical")) {
                    type = tmp;
                    break;
                }
            }

            if (type.equals("")) {
                continue;
            }

            JSONObject relationship = new JSONObject();
            relationship.put("id1", id);
            relationship.put("id2", childId);
            relationship.put("type", type);

            try {
                DBResult dbResult = dbService.addCIRelationship(relationship);
                if (!dbResult.getResult()) {
                    logger.error(JSONObject.toJSONString(relationship) + " addCIRelationship failed:" + dbResult.getInfo(), selfServerCode);
                }
            } catch (Exception e) {
                logger.error(JSONObject.toJSONString(e), selfServerCode);
            }
        }
    }

    private void initRelationship(String id, String enterpriseCode, String serverCode) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("enterpriseCode", enterpriseCode);
        jsonObject.put("serverCode", serverCode);

        try {
            DBResult dbResult = dbService.searchCIRelationship(jsonObject);
            if (dbResult.getResult()) {
                JSONObject response = dbResult.getJsonObject();
                JSONArray parent = response.getJSONArray("parent");
                for (int i = 0; i < parent.size(); ++i) {
                    JSONObject relationship = parent.getJSONObject(i);
                    String type = relationship.getString("relationshipType");
                    if (!type.equals("Logical")) {
                        JSONObject request = new JSONObject();
                        request.put("id1", id);
                        request.put("id2", response.getString("id"));
                        request.put("type", type);

                        dbResult = dbService.deleteCIRelationship(request);
                        if (!dbResult.getResult()) {
                            logger.error(JSONObject.toJSONString(request) + " deleteCIRelationship failed", selfServerCode);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(JSONObject.toJSONString(e), selfServerCode);
        }
    }

    private String mergeDevice(JSONObject jsonObject) {

        JSONObject request = new JSONObject();
        JSONArray array = new JSONArray();

        array.add(jsonObject.getString("sn"));
        request.put("sns", array);
        request.put("enterpriseCode", jsonObject.getString("enterpriseCode"));
        request.put("serverCode", jsonObject.getString("serverCode"));

        String id = "";
        try {
            DBResult dbResult = dbService.searchCI(request);
            if (dbResult.getResult()) {

                JSONObject extend = jsonObject.getJSONObject("extend");
                jsonObject.remove("extend");
                jsonObject.putAll(extend);

                jsonObject.put("address", jsonObject.getString("regionCode"));
                jsonObject.remove("regionCode");

                if (dbResult.getCount() == 1) {
                    id = dbResult.getJsonArray().getJSONObject(0).getString("id");
                    jsonObject.put("id", id);
                    if (!dbService.modifyCI(jsonObject).getResult()) {
                        id = "";
                    }
                } else {
                    dbResult = dbService.addCI(request);
                    if (dbResult.getResult()) {
                        id = dbResult.getJsonObject().getString("id");
                    }
                }
            }
        } catch (Exception e) {
            logger.error(JSONObject.toJSONString(e), selfServerCode);
        }

        return id;
    }

}

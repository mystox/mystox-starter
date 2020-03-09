package com.kongtrolink.framework.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.api.impl.MqttService;
import com.kongtrolink.framework.dao.impl.Neo4jDBService;
import com.kongtrolink.framework.entity.DBResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/Test")
public class TestController {

    @Autowired
    Neo4jDBService neo4jDBService;

    @Autowired
    MqttService mqttService;

    @Resource(name = "assetManagementExecutor")
    ThreadPoolTaskExecutor taskExecutor;

    @RequestMapping("/create")
    public String testCreateCI(@RequestBody JSONObject requestBody) {

        final String enterpriseCode = requestBody.getString("enterpriseCode");
        final String serverCode = requestBody.getString("serverCode");
        final int start = requestBody.getInteger("start");
        final int end = requestBody.getInteger("end");

        JSONObject jsonObjectFsu = new JSONObject();
        jsonObjectFsu.put("enterpriseCode", enterpriseCode);
        jsonObjectFsu.put("serverCode", serverCode);
        jsonObjectFsu.put("address", "000000");
        jsonObjectFsu.put("type", "yy38");
        jsonObjectFsu.put("sn", "000000");
        jsonObjectFsu.put("model", "fsu");
        jsonObjectFsu.put("status", true);
        jsonObjectFsu.put("user", "test");
        jsonObjectFsu.put("remarks", "测试");
        jsonObjectFsu.put("resourceNo", 1);
        jsonObjectFsu.put("versionMajor", 1);
        jsonObjectFsu.put("versionMinor", 1);
        jsonObjectFsu.put("versionRevision", 2);

        JSONObject jsonObjectPW = new JSONObject();
        jsonObjectPW.put("enterpriseCode", enterpriseCode);
        jsonObjectPW.put("serverCode", serverCode);
        jsonObjectPW.put("address", "000000");
        jsonObjectPW.put("type", "yy6");
        jsonObjectPW.put("sn", "000000");
        jsonObjectPW.put("model", "power");
        jsonObjectPW.put("status", true);
        jsonObjectPW.put("user", "test");
        jsonObjectPW.put("remarks", "测试");
        jsonObjectPW.put("resourceNo", 1);
        jsonObjectPW.put("versionMajor", 1);
        jsonObjectPW.put("versionMinor", 0);
        jsonObjectPW.put("versionRevision", 5);

        JSONObject jsonObjectENV = new JSONObject();
        jsonObjectENV.put("enterpriseCode", enterpriseCode);
        jsonObjectENV.put("serverCode", serverCode);
        jsonObjectENV.put("address", "000000");
        jsonObjectENV.put("type", "yy18");
        jsonObjectENV.put("sn", "000000");
        jsonObjectENV.put("model", "environment");
        jsonObjectENV.put("status", true);
        jsonObjectENV.put("user", "test");
        jsonObjectENV.put("remarks", "测试");
        jsonObjectENV.put("resourceNo", 1);
        jsonObjectENV.put("versionMajor", 1);
        jsonObjectENV.put("versionMinor", 2);
        jsonObjectENV.put("versionRevision", 9);

        neo4jDBService.searchCIConnectionType();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        System.out.println(simpleDateFormat.format(now) + ":Start");

        for (int i = start; i <= end; ++i) {
            String sn = "00000" + i;
            sn = sn.substring(sn.length() - 5);
            jsonObjectFsu.put("sn", "438" + sn);
            jsonObjectPW.put("sn", "406" + sn);
            jsonObjectENV.put("sn", "418" + sn);

            DBResult dbResult;

            dbResult = neo4jDBService.addCI(jsonObjectFsu);
            String fsuId = dbResult.getJsonObject().getString("id");

            dbResult = neo4jDBService.addCI(jsonObjectPW);
            String powerId = dbResult.getJsonObject().getString("id");


            dbResult = neo4jDBService.addCI(jsonObjectENV);
            String envId = dbResult.getJsonObject().getString("id");

            JSONObject relationship = new JSONObject();

            relationship.put("id1", fsuId);
            relationship.put("id2", powerId);
            relationship.put("type", "Physical");
            neo4jDBService.addCIRelationship(relationship);

            relationship.put("id2", envId);
            neo4jDBService.addCIRelationship(relationship);

            if (i % 1000 == 0) {
                now = new Date();
                System.out.println(simpleDateFormat.format(now) + ":" + i);
            }
        }

        now = new Date();
        System.out.println(simpleDateFormat.format(now) + ":" + end);
        return "1";
    }

    @RequestMapping("/search")
    public String testSearchCI(@RequestBody JSONObject requestBody) {

        int threadCount = requestBody.getInteger("threadCount");
        final String enterpriseCode = requestBody.getString("enterpriseCode");
        final String serverCode = requestBody.getString("serverCode");
        final int times = requestBody.getInteger("times");
        final int pageNum = requestBody.getInteger("pageNum");
        final int total = requestBody.getInteger("total");

        Boolean[] threadBoolean = new Boolean[threadCount];

        neo4jDBService.searchCIConnectionType();

        for (int i = 0; i < threadCount; ++i) {
            threadBoolean[i] = false;
            final int index = i;
            taskExecutor.execute(()->searchCI(index, enterpriseCode, serverCode, times, total, pageNum));
        }

        return "1";
    }

    private void searchCI(int index, String enterpriseCode, String serverCode, int times, int total, int pageNum) {

        int pageTotal = total / pageNum;
        if ((total % pageNum) > 0) {
            pageTotal++;
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enterpriseCode", enterpriseCode);
        jsonObject.put("serverCode", serverCode);
        jsonObject.put("pageNum", pageNum);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        System.out.println(simpleDateFormat.format(now) + ":Thread-" + index + " Start");

        long sum = 0;
        for (int count = 0; count < times; ++count) {
            for (int i = 1; i <= pageTotal; ++i) {
                jsonObject.put("curPage", i);

                Date startDate = new Date();
                neo4jDBService.searchCI(jsonObject);
                Date endDate = new Date();

                long diff = endDate.getTime() - startDate.getTime();

                System.out.println(simpleDateFormat.format(startDate) + ":Thread-" + index + ",count-" + count + ",i-" + i + ",Begin");
                System.out.println(simpleDateFormat.format(endDate) + ":Thread-" + index + ",count-" + count + ",i-" + i + ",End,interval-" + diff);

                sum += diff;
            }
        }

        double average = sum * 1.0 / times / pageTotal;
        System.out.println("Thread-" + index + ",average-" + average);
    }

    @RequestMapping("/search_V2")
    public String testSearchCI_V2(@RequestBody JSONObject requestBody) {

        DBResult dbResult = neo4jDBService.searchCI_V2(requestBody);

        return JSONObject.toJSONString(dbResult);
    }

    @RequestMapping("/addCI")
    public String addCI(@RequestBody JSONArray requestBody) {
        return mqttService.addCISCloud(JSONObject.toJSONString(requestBody));
    }

    @RequestMapping("/deleteCI")
    public String deleteCI(@RequestBody JSONArray requestBody) {
        return mqttService.deleteCISCloud(JSONObject.toJSONString(requestBody));
    }

    @RequestMapping("/modifyCI")
    public String modifyCI(@RequestBody JSONObject requestBody) {
        return mqttService.modifyCISCloud(JSONObject.toJSONString(requestBody));
    }
}

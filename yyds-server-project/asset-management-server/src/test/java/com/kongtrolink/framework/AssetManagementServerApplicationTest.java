package com.kongtrolink.framework;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.api.impl.MqttPublish;
import com.kongtrolink.framework.common.util.MqttUtils;
import com.kongtrolink.framework.dao.impl.Neo4jDBService;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.entity.ServerName;
import com.kongtrolink.framework.service.MqttSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AssetManagementServerApplicationTest {

    @Autowired
    Neo4jDBService neo4jDBService;

    @Autowired
    MqttPublish mqttPublish;

    @Autowired
    MqttSender mqttSender;

    @Test
    public void testCIType() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", "测试1111");
        jsonObject.put("name", "test1111");
        jsonObject.put("code", "test1");
        jsonObject.put("level", 2);

        System.out.println("deleteCIType:" + neo4jDBService.deleteCIType("test1111"));
        jsonObject.put("relationship", "Application");

        System.out.println("addCIType:" + neo4jDBService.addCIType(jsonObject));

        jsonObject.put("title", "测试2222");
        System.out.println("modifyCIType:" + neo4jDBService.modifyCIType(jsonObject));

        jsonObject.put("title", "");
        jsonObject.put("name", "BusinessDevice");
        jsonObject.put("code", "");
        JSONArray array = neo4jDBService.searchCIType(jsonObject);
        System.out.println(array);
    }

    @Test
    public void testGetRegionCode() {

        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("userId", "392e4847-abf5-48a7-b6a4-f2bdd41bf1c2");
//        jsonObject.put("enterpriseCode", "Skongtrolink");
//        jsonObject.put("serverCode", "");
        jsonObject.put("structure", 0);

        MsgResult msgResult = mqttPublish.getRegionCode(JSONObject.toJSONString(jsonObject));

        System.out.println(JSONObject.toJSONString(msgResult));
    }

    @Test
    public void testGetCIModel() {

        String serverCode = MqttUtils.preconditionServerCode(ServerName.ASSET_MANAGEMENT_SERVER, "1.0.0");
        String operaCode = "getCIModel";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enterpriseCode", "Skongtrolink");
        jsonObject.put("serverCode", "AUTH_PLATFORM");

        MsgResult result = mqttSender.sendToMqttSyn(serverCode, operaCode, JSONObject.toJSONString(jsonObject));

        System.out.println(JSONObject.toJSONString(result));
    }

    private String enterpriseCode = "1";
    private String serverCode = "1";

    @Test
    public void testCreateCI() {
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

        for (int i = 2; i <= 20000; ++i) {
            String sn = "00000" + i;
            sn = sn.substring(sn.length() - 5);
            jsonObjectFsu.put("sn", "438" + sn);
            jsonObjectPW.put("sn", "406" + sn);
            jsonObjectENV.put("sn", "418" + sn);

            String fsuId = neo4jDBService.addCI(jsonObjectFsu);
            String powerId = neo4jDBService.addCI(jsonObjectPW);
            String envId = neo4jDBService.addCI(jsonObjectENV);

            JSONObject relationship = new JSONObject();

            relationship.put("id1", fsuId);
            relationship.put("id2", powerId);
            relationship.put("type", "Physical");
            neo4jDBService.addCIRelationship(relationship);

            relationship.put("id2", envId);
            neo4jDBService.addCIRelationship(relationship);

        }
    }

    @Test
    public void testMsg() {

        JSONObject request = new JSONObject();
        request.put("sn", "38");
        request.put("enterpriseCode", "1");
        request.put("serverCode", "1");
        request.put("curPage", 1);
        request.put("pageNum", 20);

        String serverCode = MqttUtils.preconditionServerCode("ASSET_MANAGEMENT_SERVER", "1.0.0");
        String operaCode = "getCI";


        request(0, request, serverCode, operaCode);
    }

    private void request(int threadIndex, JSONObject request, String serverCode, String operaCode) {

        for (int i = 0; i < 100; ++i) {
            MsgResult msgResult = mqttSender.sendToMqttSyn(serverCode, operaCode, JSONObject.toJSONString(request));
            System.out.println((new Date()).toString() + ":threadIndex:" + threadIndex + ",count:" + i + " " + msgResult.getStateCode());
        }
    }
}

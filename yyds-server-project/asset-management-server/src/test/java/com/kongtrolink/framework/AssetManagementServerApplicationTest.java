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
}

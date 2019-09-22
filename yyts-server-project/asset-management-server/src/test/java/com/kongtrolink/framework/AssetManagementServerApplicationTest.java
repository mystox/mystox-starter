package com.kongtrolink.framework;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.dao.impl.Neo4jDBService;
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
}

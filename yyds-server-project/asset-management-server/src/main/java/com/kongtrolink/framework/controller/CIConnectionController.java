package com.kongtrolink.framework.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.dao.DBService;
import com.kongtrolink.framework.entity.DBResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/CIConnectionType")
public class CIConnectionController {

    private Logger logger = LoggerFactory.getLogger(CIConnectionController.class);

    @Resource(name = "Neo4jDBService")
    private DBService dbService;

    @Value("${server.name}_${server.version}")
    private String serverCode;

    @RequestMapping("/add")
    public String add(@RequestBody JSONObject requestBody) {

        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("info", "添加失败");

        try {
            DBResult dbResult = dbService.addCIConnectionType(requestBody);
            result.put("result", dbResult.getResult() ? 1 : 0);
            result.put("info", dbResult.getInfo());
        } catch (Exception e) {
            result.put("info", e.getMessage());
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/search")
    public String search() {

        JSONArray result = new JSONArray();

        try {
            DBResult dbResult = dbService.searchCIConnectionType();
            if (dbResult.getResult()) {
                result = dbResult.getJsonArray();
            }
        } catch (Exception e) {
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return JSONObject.toJSONString(result);
    }
}

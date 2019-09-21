package com.kongtrolink.framework.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.dao.DBService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/CIConnectionType")
public class CIConnectionController {

    @Resource(name = "Neo4jDBService")
    private DBService dbService;

    @RequestMapping("/addType")
    public String addType(@RequestBody JSONObject requestBody) {

        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("info", "添加失败");

        if (dbService.addCIConnectionType(requestBody)) {
            result.put("result", 1);
            result.put("info", "添加成功");
        }

        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/searchType")
    public String searchType() {

        JSONArray result = dbService.searchCIConnectionType();

        return JSONObject.toJSONString(result);
    }
}

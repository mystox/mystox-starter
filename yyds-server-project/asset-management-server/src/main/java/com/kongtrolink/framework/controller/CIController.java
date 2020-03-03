package com.kongtrolink.framework.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.dao.DBService;
import com.kongtrolink.framework.dao.impl.Neo4jDBService;
import com.kongtrolink.framework.entity.DBResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/CI")
public class CIController {

    private Logger logger = LoggerFactory.getLogger(CIController.class);

    @Resource(name = "Neo4jDBService")
    private DBService dbService;

    @Value("${server.name}_${server.version}")
    private String serverCode;

    @RequestMapping("/add")
    public String add(@RequestBody JSONObject requestBody) {

        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("id", "");
        result.put("info", "添加失败");

        try {
            DBResult dbResult = dbService.addCI(requestBody);
            result.put("result", dbResult.getResult() ? 1 : 0);
            result.put("id", dbResult.getJsonObject().getString("id"));
            result.put("info", dbResult.getInfo());
        } catch (Exception e) {
            result.put("info", e.getMessage());
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/delete")
    public String delete(@RequestBody JSONObject requestBody) {

        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("info", "删除失败");

        try {
            DBResult dbResult = dbService.deleteCI(requestBody);
            result.put("result", dbResult.getResult() ? 1 : 0);
            result.put("info", dbResult.getInfo());
        } catch (Exception e) {
            result.put("info", e.getMessage());
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/modify")
    public String modify(@RequestBody JSONObject requestBody) {

        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("info", "修改失败");

        try {
            DBResult dbResult = dbService.modifyCI(requestBody);
            result.put("result", dbResult.getResult() ? 1 : 0);
            result.put("info", dbResult.getInfo());
        } catch (Exception e) {
            result.put("info", e.getMessage());
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/search")
    public String search(@RequestBody JSONObject requestBody) {

        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("info", "查询失败");
        result.put("infos", new JSONArray());

        try {
            DBResult dbResult = dbService.searchCI(requestBody);
            result.put("result", dbResult.getResult() ? 1 : 0);
            result.put("count", dbResult.getCount());
            result.put("infos", dbResult.getJsonArray());
        } catch (Exception e) {
            result.put("info", e.getMessage());
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/addRelationship")
    public String addRelationship(@RequestBody JSONObject requestBody) {

        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("info", "添加失败");

        try {
            DBResult dbResult = dbService.addCIRelationship(requestBody);
            result.put("result", dbResult.getResult() ? 1 : 0);
            result.put("info", dbResult.getInfo());
        } catch (Exception e) {
            result.put("info", e.getMessage());
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/deleteRelationship")
    public String deleteRelationship(@RequestBody JSONObject requestBody) {

        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("info", "删除失败");

        try {
            DBResult dbResult = dbService.deleteCIRelationship(requestBody);
            result.put("result", dbResult.getResult() ? 1 : 0);
            result.put("info", dbResult.getInfo());
        } catch (Exception e) {
            result.put("info", e.getMessage());
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/searchRelationship")
    public String searchRelationship(@RequestBody JSONObject requestBody) {

        JSONObject result = new JSONObject();
        result.put("id", "");
        result.put("parent", new JSONArray());
        result.put("children", new JSONArray());

        try {
            DBResult dbResult = dbService.searchCIRelationship(requestBody);
            if (dbResult.getResult()) {
                result.put("id", dbResult.getJsonObject().getString("id"));
                result.put("parent", dbResult.getJsonObject().getJSONArray("parent"));
                result.put("children", dbResult.getJsonObject().getJSONArray("children"));
            }
            result.put("result", dbResult.getResult() ? 1 : 0);
            result.put("info", dbResult.getInfo());
        } catch (Exception e) {
            result.put("info", e.getMessage());
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return JSONObject.toJSONString(result);
    }
}

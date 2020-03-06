package com.kongtrolink.framework.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.api.Publish;
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
@RequestMapping("/CIProp")
public class CIPropController {

    private Logger logger = LoggerFactory.getLogger(CIPropController.class);

    @Resource(name = "Neo4jDBService")
    private DBService dbService;

    @Resource(name = "MqttPublish")
    private Publish publish;

    @Value("${server.name}_${server.version}")
    private String serverCode;

    @RequestMapping("/search")
    public String search(@RequestBody JSONObject requestBody) {

        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("info", "查询失败");

        try {
            DBResult dbResult = dbService.searchCIProp(requestBody);
            if (dbResult.getResult() != 0) {
                result = dbResult.getJsonObject();
            }
            result.put("result", dbResult.getResult());
            result.put("info", dbResult.getInfo());
        } catch (Exception e) {
            result.put("info", e.getMessage());
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/add")
    public String add(@RequestBody JSONObject requestBody) {

        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("info", "添加失败");

        try {
            DBResult dbResult = dbService.addCIProp(requestBody);
            result.put("result", dbResult.getResult());
            result.put("info", dbResult.getInfo());
            if (dbResult.getResult() != 0) {
                publishCIProps(requestBody.getString("name"), requestBody.getJSONObject("prop"));
            }
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
            DBResult dbResult = dbService.deleteCIProp(requestBody);
            result.put("result", dbResult.getResult());
            result.put("info", dbResult.getInfo());
            if (dbResult.getResult() != 0) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", requestBody.getString("name"));

                    JSONObject prop = new JSONObject();
                    prop.put("title", new JSONArray());
                    prop.put("name", new JSONArray());
                    prop.put("type", new JSONArray());
                    prop.put("enterpriseCode", requestBody.getString("enterpriseCode"));
                    prop.put("serverCode", requestBody.getString("serverCode"));

                    JSONArray jsonArray = new JSONArray();
                    jsonArray.add(prop);
                    jsonObject.put("props", jsonArray);

                    publish.publishCIProps(JSONObject.toJSONString(jsonObject));
                } catch (Exception e) {
                    logger.error(JSONObject.toJSONString(e), serverCode);
                }
            }
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
            DBResult dbResult = dbService.modifyCIProp(requestBody);
            result.put("result", dbResult.getResult());
            result.put("info", dbResult.getInfo());
            if (dbResult.getResult() != 0) {
                publishCIProps(requestBody.getString("name"), requestBody.getJSONObject("prop"));
            }
        } catch (Exception e) {
            result.put("info", e.getMessage());
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return JSONObject.toJSONString(result);
    }

    private void publishCIProps(String name, JSONObject prop) {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", name);

            JSONArray jsonArray = new JSONArray();
            jsonArray.add(prop);
            jsonObject.put("props", jsonArray);

            publish.publishCIProps(JSONObject.toJSONString(jsonObject));
        } catch (Exception ignored) {

        }
    }
}

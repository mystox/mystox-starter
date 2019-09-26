package com.kongtrolink.framework.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.api.Publish;
import com.kongtrolink.framework.dao.DBService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/CIProp")
public class CIPropController {

    @Resource(name = "Neo4jDBService")
    private DBService dbService;

    @Resource(name = "MqttPublish")
    private Publish publish;

    @RequestMapping("/search")
    public String search(@RequestBody JSONObject requestBody) {

        JSONObject result = dbService.searchCIProp(requestBody);

        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/add")
    public String add(@RequestBody JSONObject requestBody) {

        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("info", "添加失败");

        if (dbService.addCIProp(requestBody)) {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", requestBody.getString("name"));

            JSONArray jsonArray = new JSONArray();
            jsonArray.add(requestBody.getJSONObject("prop"));
            jsonObject.put("props", jsonArray);

            publish.publishCIProps(JSONObject.toJSONString(jsonObject));

            result.put("result", 1);
            result.put("info", "添加成功");
        }

        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/delete")
    public String delete(@RequestBody JSONObject requestBody) {

        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("info", "删除失败");

        if (dbService.deleteCIProp(requestBody)) {

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

            result.put("result", 1);
            result.put("info", "删除成功");
        }

        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/modify")
    public String modify(@RequestBody JSONObject requestBody) {

        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("info", "修改失败");

        if (dbService.modifyCIProp(requestBody)) {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", requestBody.getString("name"));

            JSONArray jsonArray = new JSONArray();
            jsonArray.add(requestBody.getJSONObject("prop"));
            jsonObject.put("props", jsonArray);

            publish.publishCIProps(JSONObject.toJSONString(jsonObject));

            result.put("result", 1);
            result.put("info", "修改成功");
        }

        return JSONObject.toJSONString(result);
    }
}

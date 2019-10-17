package com.kongtrolink.framework.controller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.dao.DBService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/CI")
public class CIController {

    @Resource(name = "Neo4jDBService")
    private DBService dbService;

    @RequestMapping("/add")
    public String add(@RequestBody JSONObject requestBody) {

        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("info", "添加失败");

        String id = dbService.addCI(requestBody);
        if (!id.equals("")) {
            result.put("result", 1);
            result.put("id", id);
            result.put("info", "添加成功");
        }

        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/delete")
    public String delete(@RequestBody JSONObject requestBody) {

        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("info", "删除失败");

        if (dbService.deleteCI(requestBody)) {
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

        if (dbService.modifyCI(requestBody)) {
            result.put("result", 1);
            result.put("info", "修改成功");
        }

        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/search")
    public String search(@RequestBody JSONObject requestBody) {

        JSONObject result = dbService.searchCI(requestBody);

        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/addRelationship")
    public String addRelationship(@RequestBody JSONObject requestBody) {

        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("info", "添加失败");

        if (dbService.addCIRelationship(requestBody)) {
            result.put("result", 1);
            result.put("info", "添加成功");
        }

        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/deleteRelationship")
    public String deleteRelationship(@RequestBody JSONObject requestBody) {

        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("info", "删除失败");

        if (dbService.deleteCIRelationship(requestBody)) {
            result.put("result", 1);
            result.put("info", "删除成功");
        }

        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/searchRelationship")
    public String searchRelationship(@RequestBody JSONObject requestBody) {

        JSONObject result = dbService.searchCIRelationship(requestBody);

        return JSONObject.toJSONString(result);
    }
}

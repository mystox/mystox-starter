package com.kongtrolink.framework.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.dao.DBService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/CIType")
public class CITypeController {

    @Resource(name = "Neo4jDBService")
    private DBService dbService;

    @RequestMapping("/search")
    public String search(@RequestBody JSONObject requestBody) {

        JSONArray result = dbService.searchCIType(requestBody);

        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/add")
    public String add(@RequestBody JSONObject requestBody) {

        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("info", "添加失败");

        if (dbService.addCIType(requestBody)) {
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

        String name = requestBody.getString("name");

        JSONObject request = new JSONObject();
        request.put("title", "");
        request.put("name", name);
        request.put("code", "");
        JSONArray ciTypeList = dbService.searchCIType(request);
        if (ciTypeList.size() != 1) {
            result.put("info", "删除失败，类型有误");
            return JSONObject.toJSONString(result);
        }

        request = new JSONObject();
        JSONObject ciType = ciTypeList.getJSONObject(0);
        int level = ciType.getInteger("level");
        String code = ciType.getString("code");
        if (level == 1) {
            request.put("id", code + "-.*-.*-.*-.*-.*");
        } else if (level == 2) {
            request.put("id", ".*-" + code + "-.*-.*-.*-.*");
        } else if (level == 3) {
            request.put("id", ".*-.*-" + code + "-.*-.*-.*");
        }
        JSONObject ciList = dbService.searchCI(request);
        if (ciList.getInteger("count") > 0) {
            result.put("info", "删除失败，该类型下存在设备信息，无法删除");
            return JSONObject.toJSONString(result);
        }

        if (dbService.deleteCIType(name)) {
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

        if (dbService.modifyCIType(requestBody)) {
            result.put("result", 1);
            result.put("info", "修改成功");
        }

        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/addConnection")
    public String addConnection(@RequestBody JSONObject requestBody) {

        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("info", "新增连接关系失败");

        if (dbService.addCITypeConnectionRelationship(requestBody)) {
            result.put("result", 1);
            result.put("info", "新增连接关系成功");
        }

        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/deleteConnection")
    public String deleteConnection(@RequestBody JSONObject requestBody) {

        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("info", "删除连接关系失败");

        if (dbService.deleteCITypeConnectionRelationship(requestBody)) {
            result.put("result", 1);
            result.put("info", "删除连接关系成功");
        }

        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/searchConnection")
    public String searchConnection(@RequestBody JSONObject requestBody) {

        JSONArray result = dbService.searchCITypeConnectionRelationship(requestBody);

        return JSONObject.toJSONString(result);
    }
}

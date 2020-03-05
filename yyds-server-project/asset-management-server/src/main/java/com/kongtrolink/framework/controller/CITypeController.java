package com.kongtrolink.framework.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.dao.DBService;
import com.kongtrolink.framework.entity.DBResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;

@RestController
@RequestMapping("/CIType")
public class CITypeController {

    private Logger logger = LoggerFactory.getLogger(CITypeController.class);

    @Resource(name = "Neo4jDBService")
    private DBService dbService;

    @Value("${appResources.ciTypeIcon:AppResources/ciTypeIcon}")
    private String ciTypeIconFolder;

    @Value("${server.name}_${server.version}")
    private String serverCode;

    @RequestMapping("/search")
    public String search(@RequestBody JSONObject requestBody) {

        JSONArray result = new JSONArray();

        try {
            DBResult dbResult = dbService.searchCIType(requestBody);
            if (dbResult.getResult()) {
                for (int i = 0; i < dbResult.getJsonArray().size(); ++i) {
                    JSONObject jsonObject = dbResult.getJsonArray().getJSONObject(i);
                    String icon = jsonObject.getString("icon");
                    jsonObject.put("icon", ciTypeIconFolder + "/" + icon);
                    result.add(jsonObject);
                }
            }
        } catch (Exception e) {
            result = new JSONArray();
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return JSONObject.toJSONString(result);
    }

    @RequestMapping(value = "/add")
    public String add(@RequestBody JSONObject requestBody) {

        JSONObject result = new JSONObject();

        try {
            DBResult dbResult = dbService.addCIType(requestBody);
            result.put("result", dbResult.getResult() ? 1 : 0);
            result.put("info", dbResult.getInfo());
        } catch (Exception e) {
            result.put("result", 0);
            result.put("info", e.getMessage());
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return JSONObject.toJSONString(result);
    }

    @RequestMapping(value = "/updateIcon")
    public String updateIcon(@RequestParam("file") MultipartFile multipartFile, @RequestParam String name) {

        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("info", "图标上传失败");

        if (!multipartFile.isEmpty()) {
            String filePath = "./" + ciTypeIconFolder + "/" + multipartFile.getOriginalFilename();
            File file = new File(filePath);
            try {
                multipartFile.transferTo(file);
                DBResult dbResult = dbService.modifyCITypeIcon(name, multipartFile.getOriginalFilename());
                result.put("result", dbResult.getResult() ? 1 : 0);
                result.put("info", dbResult.getInfo());
            } catch (Exception e) {
                result.put("info", e.getMessage());
                logger.error(JSONObject.toJSONString(e), serverCode);
            }
        }

        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/delete")
    public String delete(@RequestBody JSONObject requestBody) {

        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("info", "CI类型删除失败");

        try {
            String name = requestBody.getString("name");

            DBResult dbResult = dbService.searchCITypeByName(name);
            JSONArray ciTypeList = dbResult.getJsonArray();
            if (!dbResult.getResult() || ciTypeList.size() != 1) {
                result.put("info", "CI类型删除失败，无法查询到指定类型信息");
                return JSONObject.toJSONString(result);
            }

            JSONObject ciType = ciTypeList.getJSONObject(0);
            JSONArray children = ciType.getJSONArray("children");
            if (children.size() > 0) {
                result.put("info", "CI类型删除失败，该类型下存在子类型");
                return JSONObject.toJSONString(result);
            }

            JSONObject request = new JSONObject();
            request.put("type", name);
            dbResult = dbService.searchCI(request);
            if (!dbResult.getResult()) {
                result.put("info", "CI类型删除失败，无法确认该类型下是否存在设备信息，无法删除");
                return JSONObject.toJSONString(result);
            }else if (dbResult.getCount() > 0) {
                result.put("info", "CI类型删除失败，该类型下存在设备信息，无法删除");
                return JSONObject.toJSONString(result);
            }

            dbResult = dbService.deleteCIType(name);
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
            DBResult dbResult = dbService.modifyCIType(requestBody);
            result.put("result", dbResult.getResult() ? 1 : 0);
            result.put("info", dbResult.getInfo());
        } catch (Exception e) {
            result.put("info", e.getMessage());
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/bindBusiness")
    public String bindBusiness(@RequestBody JSONObject requestBody) {

        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("info", "绑定失败");

        try {
            DBResult dbResult = dbService.bindCITypeBusinessCode(requestBody);
            result.put("result", dbResult.getResult() ? 1 : 0);
            result.put("info", dbResult.getInfo());
        } catch (Exception e) {
            result.put("info", e.getMessage());
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/unbindBusiness")
    public String unbindBusiness(@RequestBody JSONObject requestBody) {

        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("info", "解绑失败");

        try {
            DBResult dbResult = dbService.unbindCITypeBusinessCode(requestBody);
            result.put("result", dbResult.getResult() ? 1 : 0);
            result.put("info", dbResult.getInfo());
        } catch (Exception e) {
            result.put("info", e.getMessage());
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/addConnection")
    public String addConnection(@RequestBody JSONObject requestBody) {

        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("info", "新增连接关系失败");

        try {
            DBResult dbResult = dbService.addCITypeConnectionRelationship(requestBody);
            result.put("result", dbResult.getResult() ? 1 : 0);
            result.put("info", dbResult.getInfo());
        } catch (Exception e) {
            result.put("info", e.getMessage());
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/deleteConnection")
    public String deleteConnection(@RequestBody JSONObject requestBody) {

        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("info", "删除连接关系失败");

        try {
            DBResult dbResult = dbService.deleteCITypeConnectionRelationship(requestBody);
            result.put("result", dbResult.getResult() ? 1 : 0);
            result.put("info", dbResult.getInfo());
        } catch (Exception e) {
            result.put("info", e.getMessage());
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/searchConnection")
    public String searchConnection(@RequestBody JSONObject requestBody) {

        JSONArray result = new JSONArray();

        try {
            DBResult dbResult = dbService.searchCITypeConnectionRelationship(requestBody);
            if (dbResult.getResult()) {
                result = dbResult.getJsonArray();
            }
        } catch (Exception e) {
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return JSONObject.toJSONString(result);
    }
}

package com.kongtrolink.framework.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.dao.DBService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/CIType")
public class CITypeController {

    @Resource(name = "Neo4jDBService")
    private DBService dbService;

    @Value("${appResources.ciTypeIcon:AppResources/ciTypeIcon}")
    private String ciTypeIconFolder;

    @RequestMapping("/search")
    public String search(@RequestBody JSONObject requestBody) {

        JSONArray result = dbService.searchCIType(requestBody);
        for (int i = 0; i < result.size(); ++i) {
            JSONObject jsonObject = result.getJSONObject(i);
            String name = jsonObject.getString("name");
            String icon = jsonObject.getString("icon");
            jsonObject.put("icon", ciTypeIconFolder + "/" + icon);
        }

        return JSONObject.toJSONString(result);
    }

    @RequestMapping(value = "/add")
    public String add(@RequestBody JSONObject requestBody) {

        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("info", "添加失败");

        if (dbService.addCIType(requestBody)) {

            result.put("result", 1);
            result.put("info", "添加成功");

//            String defaultPath = "./" + ciTypeIconFolder + "/default.ico";
//            String iconPath = "./AppResources/" + ciTypeIcon + "/" + requestBody.getString("name") + ".ico";
//            File defaultFile = new File(defaultPath);
//            File iconFile = new File(iconPath);
//            try {
//                Files.copy(defaultFile.toPath(), iconFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//            } catch (Exception e) {
//                result.put("info", "添加成功，图标加载失败");
//            }
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
                if (dbService.modifyCITypeIcon(name, multipartFile.getOriginalFilename())) {
                    result.put("result", 1);
                    result.put("info", "图标上传成功");
                } else {
                    result.put("info", "图标信息保存失败");
                }
            } catch (Exception e) {
                result.put("info", "图标上传失败");
            }
        }

        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/delete")
    public String delete(@RequestBody JSONObject requestBody) {

        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("info", "删除失败");

        String name = requestBody.getString("name");

        JSONArray ciTypeList = dbService.searchCITypeByName(name);
        if (ciTypeList.size() != 1) {
            result.put("info", "删除失败，无法查询到指定类型信息");
            return JSONObject.toJSONString(result);
        }

        JSONObject ciType = ciTypeList.getJSONObject(0);
        JSONArray children = ciType.getJSONArray("children");
        if (children.size() > 0) {
            result.put("info", "删除失败，该类型下存在子类型");
            return JSONObject.toJSONString(result);
        }

        JSONObject request = new JSONObject();
        request.put("type", name);
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

    @RequestMapping("/bindBusiness")
    public String bindBusiness(@RequestBody JSONObject requestBody) {

        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("info", "绑定失败");

        if (dbService.bindCITypeBusinessCode(requestBody)) {
            result.put("result", 1);
            result.put("info", "绑定成功");
        }

        return JSONObject.toJSONString(result);
    }

    @RequestMapping("/unbindBusiness")
    public String unbindBusiness(@RequestBody JSONObject requestBody) {

        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("info", "解绑失败");

        if (dbService.unbindCITypeBusinessCode(requestBody)) {
            result.put("result", 1);
            result.put("info", "解绑成功");
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

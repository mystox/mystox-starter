package com.kongtrolink.framework.reports.controller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.reports.service.MqttCommonInterface;
import com.kongtrolink.framework.service.MqttOpera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by mystoxlol on 2019/10/31, 9:34.
 * company: kongtrolink
 * description:
 * update record:
 */
@RestController
@RequestMapping("/reportsOpera")
public class ReportsOperaController extends BaseController {

    @Autowired
    MqttOpera mqttOpera;

    @Autowired
    MqttCommonInterface mqttCommonInterface;

    @RequestMapping("/getAlarmLevel")
    public JsonResult getAlarmLevel() {
        String uniqueCode = getUniqueCode();
        String currentServerCode = getCurrentService().getCode();
        JSONObject query = new JSONObject();
        query.put("enterpriseCode",uniqueCode);
        query.put("serverCode", currentServerCode);
        List<String> result = mqttCommonInterface.getAlarmLevel(query);
        if (result != null) {
            result.add(0, "全部");
            return new JsonResult(result);
        }
        return new JsonResult(new String[]{"全部"});
//        return new JsonResult(new String[]{"紧急告警","一级告警","二级告警","三级告警","四级告警"});
    }


    @RequestMapping("/getStationList")
    public JsonResult getStationListTree() {
        String uniqueCode = getUniqueCode();
        String currentServerCode = getCurrentService().getCode();
        JSONObject query = new JSONObject();
        String userId = getUserId();
        query.put("uniqueCode",uniqueCode);
        query.put("serverCode", currentServerCode);
        query.put("userId", userId);
        List<JSONObject> result = mqttCommonInterface.getCurrentStationList(query);
        if (result != null) {
            return new JsonResult(result);
        }
        return new JsonResult(new String[]{});
    }




}


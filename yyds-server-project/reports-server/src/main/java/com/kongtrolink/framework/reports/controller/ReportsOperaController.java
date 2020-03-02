package com.kongtrolink.framework.reports.controller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.service.MqttOpera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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



    @RequestMapping("/getAlarmLevel")
    public JsonResult getAlarmLevel() {
        MsgResult result = mqttOpera.opera("getAlarmLevel","");
        String alarmLevels = result.getMsg();
        return new JsonResult(JSONObject.parseArray(alarmLevels));
    }


}


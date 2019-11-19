package com.kongtrolink.framework.log.controller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.log.service.LogControllerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by mystoxlol on 2019/11/8, 14:42.
 * company: kongtrolink
 * description:
 * update record:
 */
@RestController
@RequestMapping("/log")
public class LogController {

    @Autowired
    LogControllerService logControllerService;

    @RequestMapping("/getMqttLog")
    public JsonResult getMqttLog(@RequestBody(required = false) JSONObject query) {
        JSONObject mqttLogList = logControllerService.getMqttLogList(query);
        return new JsonResult(mqttLogList);
    }

}

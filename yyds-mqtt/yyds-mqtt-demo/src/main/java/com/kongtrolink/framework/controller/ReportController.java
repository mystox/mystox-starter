package com.kongtrolink.framework.controller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.common.util.MqttUtils;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.entity.ServerName;
import com.kongtrolink.framework.mqtt.service.MqttSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by mystoxlol on 2019/10/30, 13:28.
 * company: kongtrolink
 * description:
 * update record:
 */
@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    MqttSender mqttSender;


    @Value("${server.name}")
    private String serverName;

    @Value("${server.version}")
    private String serverVersion;

    @RequestMapping("/testSyn")
    JsonResult reportsTest(@RequestBody(required = false) JSONObject body) {

        String operaCode = body.getString("operaCode");
        body.put("serverCode", MqttUtils.preconditionServerCode(serverName, serverVersion));
//        body.put("asyn", true);//是否异步
        Boolean asyn = body.getBoolean("asyn");
//        if (asyn)
//            mqttSender.sendToMqtt(MqttUtils.preconditionServerCode(ServerName.REPORTS_SERVER, "1.0.0"), operaCode, body.toJSONString());
//        else {
            MsgResult msgResult = mqttSender.sendToMqttSync(MqttUtils.preconditionServerCode(ServerName.REPORTS_SERVER, "1.0.0"), operaCode, body.toJSONString());
            return new JsonResult(msgResult);
//        }
//        return new JsonResult();

    }

}

package com.kongtrolink.framework.controller;

import com.kongtrolink.framework.core.IaContext;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.service.MsgHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * \* @Author: mystox
 * \* Date: 2020/1/4 16:15
 * \* Description:
 * \
 */
@RestController
@RequestMapping("/operaRest")
public class MqttOperaRestController {

//    @Autowired
//    MqttOpera mqttOpera;


    @Autowired
    IaContext iaContext;
    @RequestMapping("/operaSync")
    public JsonResult operaSyn(@RequestParam String operaCode,@RequestBody String message) {
        MsgHandler msgHandler =iaContext.getIaENV().getMsgScheudler().getIahander();
        MsgResult opera = msgHandler.opera("getDevice",message,2,120, TimeUnit.SECONDS);
        return new JsonResult(opera);
    }

    @RequestMapping("/operaAsync")
    public JsonResult operaAsync(@RequestParam String operaCode,@RequestBody String message) {
        MsgHandler msgHandler =iaContext.getIaENV().getMsgScheudler().getIahander();
        msgHandler.operaAsync(operaCode,message);
        return new JsonResult();
    }
    @RequestMapping("/broadcast")
    public JsonResult broadcast(@RequestParam String operaCode,@RequestBody String message) {
        MsgHandler msgHandler =iaContext.getIaENV().getMsgScheudler().getIahander();
        msgHandler.broadcast(operaCode,message);
        return new JsonResult("ok");
    }

}
package com.kongtrolink.framework.controller;

import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.service.MqttOpera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * \* @Author: mystox
 * \* Date: 2020/1/4 16:15
 * \* Description:
 * \
 */
@RestController
@RequestMapping("/operaRest")
public class MqttOperaRestController {


    @Autowired
    MqttOpera mqttOpera;


    @RequestMapping("/operaSyn")
    public JsonResult operaSyn(@RequestParam String operaCode,@RequestBody String message) {
        MsgResult opera = mqttOpera.opera(operaCode,message);
        return new JsonResult(opera);
    }

}
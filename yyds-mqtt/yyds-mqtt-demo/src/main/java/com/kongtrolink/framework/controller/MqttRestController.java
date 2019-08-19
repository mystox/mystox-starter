package com.kongtrolink.framework.controller;

import com.kongtrolink.framework.mqtt.service.IMqttSender;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by mystoxlol on 2019/8/15, 15:17.
 * company: kongtrolink
 * description:
 * update record:
 */
@RestController
@RequestMapping("/rest")
public class MqttRestController {

    /**
     * 注入发送MQTT的Bean
     */
    @Resource
    private IMqttSender iMqttSender;

    @RequestMapping("/sendMqtt")
    public String sendMqtt(@RequestParam String topic, @RequestBody String message) {
        iMqttSender.sendToMqtt(topic, message);
        return "ok";
    }
}

package com.kongtrolink.framework.controller;

import com.kongtrolink.framework.mqtt.service.IMqttSender;
import com.kongtrolink.framework.mqtt.service.MqttHandler;
import com.kongtrolink.framework.mqtt.service.MqttSender;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    MqttHandler mqttHandler;
    @Autowired
    MqttSender mqttSender;

    @RequestMapping("/sendMqtt")
    public String sendMqtt(@RequestParam String topic, @RequestBody String message) {
        iMqttSender.sendToMqtt(topic, message);
        return "ok";
    }

    @RequestMapping("/sendMsg")
    public String sendMqtt(@RequestParam String serviceCode, @RequestParam String operaCode,
                           @RequestBody String message) {
        mqttSender.sendToMqtt(serviceCode, operaCode, message);
        return "ok";
    }

    @RequestMapping("/addTopic")
    public String addTopic(@RequestParam String topic) {
        mqttHandler.addSubTopic(topic, 2);
        return "ok";
    }

    @RequestMapping("/addPub")
    public String addTopic(@RequestParam String serviceCode, @RequestParam String operaCode) {
        mqttHandler.addSubTopic(serviceCode + "/" + operaCode, 2);
        return "ok";
    }

}

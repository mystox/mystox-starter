package com.kongtrolink.framework.controller;

import com.kongtrolink.framework.mqtt.service.IMqttSender;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Created by mystoxlol on 2019/8/6, 14:14.
 * company: kongtrolink
 * description:
 * update record:
 */
@Controller
@RequestMapping(value = "/")
public class MqttController {
    /**
     * 注入发送MQTT的Bean
     */
    @Resource
    private IMqttSender iMqttSender;

    /**
     * 发送MQTT消息
     * @param message 消息内容
     * @return 返回
     */
    @ResponseBody
    @GetMapping(value = "/mqtt", produces ="text/html")
    public ResponseEntity<String> sendMqtt(@RequestParam String topic, @RequestBody String message) {
        iMqttSender.sendToMqtt(topic, message);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}

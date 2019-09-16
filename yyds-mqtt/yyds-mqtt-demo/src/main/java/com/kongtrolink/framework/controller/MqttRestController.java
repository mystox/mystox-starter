package com.kongtrolink.framework.controller;

import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.mqtt.service.IMqttSender;
import com.kongtrolink.framework.service.MqttSender;
import com.kongtrolink.framework.service.MqttHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Qualifier("mqttHandlerImpl")
    MqttHandler mqttHandler;

    @Autowired
    MqttSender mqttSender;

    @RequestMapping("/sendMqtt")
    public String sendMqtt(@RequestParam String topic, @RequestBody String message) {
        iMqttSender.sendToMqtt(topic, message);
        return "ok";
    }

    /**
     * 异步的mqtt消息发送者
     * @param serverCode 目标的服务编码：serverName_serverVersion
     * @param operaCode 操作码
     * @param message 消息实体
     * @return
     */
    @RequestMapping("/sendMsg")
    public String sendMqtt(@RequestParam String serverCode, @RequestParam String operaCode,
                           @RequestBody String message) {
        mqttSender.sendToMqtt(serverCode, operaCode, message);
        return "ok";
    }

    /**
     * 同步接口发送mqtt消息处理返回值
     * @param serverCode 目标的服务编码：serverName_serverVersion
     * @param operaCode 操作码
     * @param message 消息实体
     * @return
     */
    @RequestMapping("/sendMsgSyn")
    public MsgResult sendMqttSyn(@RequestParam String serverCode, @RequestParam String operaCode,
                           @RequestBody String message) {
        MsgResult s = mqttSender.sendToMqttSyn(serverCode, operaCode, message);
        return s;
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

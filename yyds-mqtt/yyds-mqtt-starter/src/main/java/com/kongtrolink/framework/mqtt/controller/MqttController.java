package com.kongtrolink.framework.mqtt.controller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.AckEnum;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.RegisterSub;
import com.kongtrolink.framework.mqtt.util.SpringContextUtil;
import com.kongtrolink.framework.register.runner.RegisterRunner;
import com.kongtrolink.framework.service.MqttHandler;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by mystoxlol on 2019/8/20, 15:18.
 * company: kongtrolink
 * description:
 * update record:
 */
@RestController
@RequestMapping("/mqtt")
public class MqttController {

    private static final Logger logger = LoggerFactory.getLogger(MqttController.class);


    @Autowired
    MqttHandler mqttHandlerImpl;

    @Autowired
    RegisterRunner registerRunner;

    /**
     * 注册订阅表
     *
     * @return
     */
    @RequestMapping("/registerSub")
    public JsonResult registerSub(@RequestBody String body) {
        logger.info("register sub msg" + body);
        JSONObject subJson = JSONObject.parseObject(body);
//        String serverCode = subJson.getString("serverCode");
        String operaCode = subJson.getString("operaCode");
        String executeUnit = subJson.getString("executeUnit");
        String ack = subJson.getString("ack");
        logger.info("往注册中心注册订阅实体,跟随注册模块的实现。。。");
        //todo
        RegisterSub sub = new RegisterSub();
        sub.setExecuteUnit(executeUnit);
        sub.setOperaCode(operaCode);
        sub.setAck(AckEnum.ACK.toString().equals(ack) ? AckEnum.ACK : AckEnum.NA);
        try {
            registerRunner.setDataToRegistry(sub);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //暂时性的内部map
        String topic = mqttHandlerImpl.assembleSubTopic(operaCode);
        SpringContextUtil.getServiceMap().put(topic, executeUnit);
        logger.info("add sub topic[{}] to mqtt broker...", topic);
        mqttHandlerImpl.addSubTopic(topic);
        return new JsonResult();
    }

    /**
     * 注册请求表
     *
     * @return
     */
    @RequestMapping("/registerPub")
    public JsonResult registerPub(@RequestBody String body) {
        logger.info("register pub msg" + body);
        JSONObject subJson = JSONObject.parseObject(body);
        String serverCode = subJson.getString("serverCode");
        logger.info("往注册中心注册请求实体,跟随注册模块的实现。。。");
        //todo
        return new JsonResult();
    }

    /**
     * 更新订阅表
     *
     * @return
     */
    @RequestMapping("/updateSub")
    public JsonResult updateSub() {

        //todo
        return new JsonResult();
    }

    /**
     * 更新请求表
     *
     * @return
     */
    @RequestMapping("/updatePub")
    public JsonResult updatePub() {
        //todo
        return new JsonResult();
    }

    /**
     * 删除订阅表
     *
     * @return
     */
    @RequestMapping("/deleteSub")
    public JsonResult deleteSub(@RequestParam String topic) {
        logger.info("delete topic[{}] ...", topic);
        //todo
        logger.info("从注册中心订阅表移除topic...注册模块实现...");
        mqttHandlerImpl.removeSubTopic(topic);
        return new JsonResult();
    }

    /**
     * 删除请求表
     *
     * @return
     */
    @RequestMapping("/deletePub")
    public JsonResult deletePub(@RequestParam String topic) {
        //todo
        logger.info("delete topic[{}] ...", topic);
        //todo
        logger.info("从注册中心订阅表移除topic...注册模块实现...");
        return new JsonResult();
    }
}

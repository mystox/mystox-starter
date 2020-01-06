package com.kongtrolink.framework.mqtt.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.MqttResp;
import com.kongtrolink.framework.mqtt.service.MqttRestService;
import com.kongtrolink.framework.mqtt.service.MqttSender;
import com.kongtrolink.framework.mqtt.service.impl.CallBackTopic;
import com.kongtrolink.framework.mqtt.service.impl.MqttSenderImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
    MqttSender mqttSender;

    @Autowired
    MqttRestService mqttRestService;



    /**
     * 注册订阅表
     *
     * @return
     */
    @RequestMapping("/registerSub")
    public JsonResult registerSub(@RequestBody JSONObject subJson) {
        logger.info("register sub msg" + subJson);
        return mqttRestService.registerSub(subJson);
    }

    /**
     * 注册请求表 无效
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
    public JsonResult updateSub(@RequestBody JSONObject body) {
        return registerSub(body);
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
    public JsonResult deleteSub(@RequestBody JSONObject body) {
        logger.info("delete body[{}] ...", body);
        return mqttRestService.deleteSub(body);
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

    @RequestMapping("/getCallBack")
    public JsonResult getCallBack(@RequestBody JSONObject condition)
    {
        String msgId = condition.getString("msgId");
        MqttSenderImpl mqttSender = (MqttSenderImpl) this.mqttSender;
        Map<String, CallBackTopic> callbacks = mqttSender.getCALLBACKS();
        CallBackTopic callBackTopic = callbacks.get(msgId);
        MqttResp call = null;
        try {
            if (callBackTopic != null) {
                System.out.println(callBackTopic.toString());
                call = callBackTopic.call();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isBlank(msgId))
            return new JsonResult(callbacks.size());
        return new JsonResult(call);
    }


    @RequestMapping("/updateOperaRoute")
    public JsonResult updateOperaRoute(@RequestBody JSONObject body) {
        String operaCode = body.getString("operaCode");
        JSONArray subGroupServerArr = body.getJSONArray("subGroupServerArr");
        List<String> subGroupServerList = subGroupServerArr.toJavaList(String.class);
//        Map<String, List<String>> operaRoute = operaRouteConfig.getOperaRoute();
        try {
            mqttRestService.updateOperaRoute(operaCode,subGroupServerList);
        } catch (KeeperException | InterruptedException |IOException e) {
            logger.error("update opera route error: [{}]",e.toString());
            if (logger.isDebugEnabled())
                e.printStackTrace();
            return new JsonResult("update opera route error: [{}]"+e.toString(), false);
        }
        return new JsonResult();

    }

}

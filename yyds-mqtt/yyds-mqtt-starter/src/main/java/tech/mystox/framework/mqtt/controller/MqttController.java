package tech.mystox.framework.mqtt.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.web.bind.annotation.*;
import tech.mystox.framework.common.util.SpringContextUtil;
import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.entity.JsonResult;
import tech.mystox.framework.entity.MqttMsg;
import tech.mystox.framework.entity.MsgRsp;
import tech.mystox.framework.entity.OperaType;
import tech.mystox.framework.mqtt.service.impl.CallSubpackageMsg;
import tech.mystox.framework.mqtt.service.impl.ChannelSenderImpl;
import tech.mystox.framework.mqtt.service.impl.MqttReceiver;
import tech.mystox.framework.mqtt.service.impl.MqttRestService;
import tech.mystox.framework.service.common.OperaRouteService;
import tech.mystox.framework.stereotype.Opera;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by mystoxlol on 2019/8/20, 15:18.
 * company: mystox
 * description:
 * update record:
 */
@RestController
@ConditionalOnExpression("${mqtt.controller.enable:false}")
@RequestMapping("/register")
public class MqttController {

    private static final Logger logger = LoggerFactory.getLogger(MqttController.class);
    final IaContext iaContext;
    final MqttRestService mqttRestService;

    @Opera(operaType = OperaType.Broadcast)
    OperaRouteService operaRouteService;

    public MqttController(IaContext iaContext, MqttRestService mqttRestService) {
        this.iaContext = iaContext;
        this.mqttRestService = mqttRestService;
    }


    /**
     * 注册订阅表
     */
    @PostMapping ("/registerSub")
    public JsonResult<String> registerSub(@RequestBody JSONObject subJson) {
        logger.info("Register sub msg" + subJson);
        return mqttRestService.registerSub(subJson);
    }

    /**
     * 注册请求表 无效
     *
     */
    @PostMapping("/registerPub")
    public JsonResult<?> registerPub(@RequestBody String body) {
        logger.info("Register pub msg" + body);
        JSONObject subJson = JSONObject.parseObject(body);
//        String serverCode = subJson.getString("serverCode");
//        logger.info("往注册中心注册请求实体,跟随注册模块的实现。。。");
        //todo
        return new JsonResult<>();
    }

    /**
     * 更新订阅表
     *
     */
    @PostMapping("/updateSub")
    public JsonResult<?> updateSub(@RequestBody JSONObject body) {
        return registerSub(body);
    }

    /**
     * 更新请求表
     *
     */
    @PostMapping("/updatePub")
    public JsonResult<?> updatePub() {
        //todo
        return new JsonResult<>();
    }

    /**
     * 删除订阅表
     *
     */
    @PostMapping("/deleteSub")
    public JsonResult<String> deleteSub(@RequestBody JSONObject body) {
        logger.info("Delete body[{}] ...", body);
        return mqttRestService.deleteSub(body);
    }

    /**
     * 删除请求表
     *
     */
    @PostMapping("/deletePub")
    public JsonResult<?> deletePub(@RequestParam String topic) {
        //todo
        logger.info("Delete topic[{}] ...", topic);
        //todo
        logger.info("从注册中心订阅表移除topic...注册模块实现...");
        return new JsonResult<>();
    }

    @PostMapping("/getReceiverPackageCallBack")
    public JsonResult<?> getReceiverPackageCallBack(@RequestParam(required = false) String msgId) {
        MqttReceiver bean = SpringContextUtil.getApplicationContext().getBean(MqttReceiver.class);
        Map<String, CallSubpackageMsg<MqttMsg>> callbacks = bean.getCALLBACKS();
        if (StringUtils.isNotBlank(msgId)) {
            CallSubpackageMsg<MqttMsg> callBackTopic = callbacks.get(msgId);
            MqttMsg call = null;
            try {
                if (callBackTopic != null) {
                    call = callBackTopic.call();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new JsonResult<>(call);
        }
        return new JsonResult<>(callbacks);
    }

    @PostMapping("/getSenderSyncCallBack")
    public JsonResult<?> getSenderSyncCallBack(@RequestParam(required = false) String msgId) {
        ChannelSenderImpl mqttSender = (ChannelSenderImpl) this.iaContext.getIaENV().getMsgScheduler();
        Map<String, CallSubpackageMsg<MsgRsp>> callbacks = mqttSender.getCALLBACKS();
        if (StringUtils.isNotBlank(msgId)) {
            CallSubpackageMsg<MsgRsp> callBackTopic = callbacks.get(msgId);
            MsgRsp call = null;
            try {
                if (callBackTopic != null) {
                    call = callBackTopic.call();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new JsonResult<>(call);
        }
        return new JsonResult<>(callbacks.size());
    }

    @PostMapping("/updateOperaRoute")
    public JsonResult<?> updateOperaRoute(@RequestBody JSONObject body) {
        String operaCode = body.getString("operaCode");
        JSONArray subGroupServerArr = body.getJSONArray("subGroupServerArr");
        List<String> subGroupServerList = subGroupServerArr.toJavaList(String.class);
        //        Map<String, List<String>> operaRoute = operaRouteConfig.getOperaRoute();
        try {
            mqttRestService.updateOperaRoute(operaCode, subGroupServerList);
            //修改完成需要广播
            operaRouteService.broadcastOperaRoute(operaCode, subGroupServerList);
        } catch (InterruptedException | IOException e) {
            logger.error("update opera route error:", e);
            if (logger.isDebugEnabled())
                e.printStackTrace();
            return new JsonResult<>("update opera route error: " + e, false);
        }
        return new JsonResult<>();

    }

}

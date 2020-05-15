package com.kongtrolink.framework.mqtt.service;

import com.alibaba.fastjson.JSONArray;
import com.kongtrolink.framework.config.IaConf;
import com.kongtrolink.framework.core.IaContext;
import com.kongtrolink.framework.core.IaENV;
import com.kongtrolink.framework.entity.*;
import com.kongtrolink.framework.mqtt.service.impl.ChannelHandlerAck;
import com.kongtrolink.framework.mqtt.service.impl.ChannelHandlerSub;
import com.kongtrolink.framework.mqtt.service.impl.ChannelSenderImpl;
import com.kongtrolink.framework.scheudler.MsgScheduler;
import com.kongtrolink.framework.scheudler.RegScheduler;
import com.kongtrolink.framework.service.MsgHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.kongtrolink.framework.common.util.MqttUtils.*;

@Component("MqttHandler")
public class MqttHandler implements MsgHandler {
    @Autowired
    IaContext iaContext;
    Logger logger = LoggerFactory.getLogger(ChannelHandlerSub.class);
    @Autowired
    ChannelHandlerAck mqttHandlerAck;
    @Autowired
    ChannelHandlerSub mqttHandlermpl;
    @Autowired
    ChannelSenderImpl mqttSender;

    public ChannelHandlerAck getMqttHandlerAck() {
        return mqttHandlerAck;
    }
    public ChannelHandlerSub getMqttHandlermpl() {
        return mqttHandlermpl;
    }
    @Override
    public void addSubTopic(String topic, int qos) {
        mqttHandlermpl.addSubTopic(topic, qos);
    }

    @Override
    public void removeSubTopic(String... topic) {
            mqttHandlermpl.removeSubTopic(topic);
    }

    @Override
    public void removeAckSubTopic(String... topic) {
        mqttHandlerAck.removeSubTopic(topic);
    }

    @Override
    public boolean isAckExists(String topic) {
        return mqttHandlerAck.isExists(topic);
    }

    @Override
    public boolean isExists(String topic) {
        return mqttHandlermpl.isExists(topic);

    }

    @Override
    public void addAckTopic(String topic, int qos) {
        mqttHandlerAck.addSubTopic(topic,qos);
    }

    /////////////IaOperate/////////////
    @Override
    public MsgResult opera(String operaCode, String msg) {
        return opera(operaCode,msg);
    }


    @Override
    public RegisterMsg whereIsCentre() {
        IaENV iaENV= iaContext.getIaENV();
        IaConf iaconf= iaENV.getConf();
        String serverName=iaconf.getServerName();
        String groupCode=iaconf.getGroupCode();

        String registerServerName=iaconf.getRegisterServerName();
        String registerServerVersion=iaconf.getRegisterServerVersion();
        String registerUrl=iaconf.getRegisterUrl();
        RegisterMsg registerMsg = new RegisterMsg();
        //TODO 第三方机构实现注册中心客户端负载均衡
//        if (!serverName.equals(registerServerName)) {  //非认证服务执行操作
//            ServerMsg serverMsg = new ServerMsg(iaconf.getHost(), iaconf.getPort(), iaconf.getServerName(), iaconf.getServerVersion(),
//                    iaconf.getRouteMark(),iaconf.getPageRoute() ,iaconf.getServerUri(),iaconf.getTitle(), groupCode,iaconf.getMyid());
//
//            String sLoginPayload = JSONObject.toJSONString(serverMsg);
//            MsgResult slogin=null;
//            do{
//                    DateUtil.Wait(1000);
//                    slogin=slogin(preconditionGroupServerCode(GroupCode.ROOT, preconditionServerCode(registerServerName,
//                        registerServerVersion)), sLoginPayload);
//                    logger.error("slogin failed state[{}], msg: [{}]", slogin.getStateCode(), slogin.getMsg());
//                    if ("dev".equals(iaconf.getDevFlag())) {
//                        logger.warn("environment ${spring.profiles.active} is dev, set registerUrl is [{}]", registerUrl);
//                    }
//            }
//            while(slogin.getStateCode()!=StateCode.SUCCESS);
//
//            String msg = slogin.getMsg();
//            Object parse = JSON.parse(msg);
//            if (parse instanceof JSONObject) {
//                registerUrl=((JSONObject) parse).getString("registerUrl");
//                if (StringUtils.isBlank(registerUrl)) {
//                    String errorMsg = ((JSONObject) parse).getString("errorMsg");
//                    logger.error("slogin failed state[{}], msg: [{}]", slogin.getStateCode(), errorMsg);
//                    return null;
//                }
//                logger.info("get slogin result(registerUrl) is [{}]", registerUrl);
//                }
//            }
        logger.info("{} registerUrl is: [{}]", serverName, registerUrl);
        String[] split = registerUrl.split("://");
        String registerUrlHeader = split[0];
        String registerHosts = split[1];
        registerMsg.setRegisterUrl(registerHosts);
        registerMsg.setRegisterUrlHeader(registerUrlHeader);
        if (StringUtils.equals(RegisterType.ZOOKEEPER.toString(), registerUrlHeader.toUpperCase()))
            registerMsg.setRegisterType(RegisterType.ZOOKEEPER);
        if (StringUtils.equals(RegisterType.REDIS.toString(), registerUrlHeader.toUpperCase()))
            registerMsg.setRegisterType(RegisterType.REDIS);
        return registerMsg;
    }

    @Override
    public void sendToMqtt(String serverCode, String operaCode, String payload) {

    }

    @Override
    public void sendToMqtt(String serverCode, String operaCode, int qos, String payload) {

    }

    @Override
    public MsgResult sendToMqttSync(String serverCode, String operaCode, String payload) {
        return null;
    }

    @Override
    public MsgResult sendToMqttSync(String serverCode, String operaCode, int qos, String payload, long timeout, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public boolean sendToMqttBoolean(String serverCode, String operaCode, int qos, String payload) {
        return false;
    }


    /**
     * @return com.kongtrolink.framework.entity.MsgResult
     * @Date 16:12 2020/1/4
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description
     **/
    private MsgResult opera(String operaCode, String msg, int qos, long timeout, TimeUnit timeUnit, boolean setFlag, boolean async) {
        MsgResult result;

        IaENV iaENV= iaContext.getIaENV();
        RegScheduler regScheduler=iaENV.getRegScheduler();
        IaConf iaconf= iaENV.getConf();
        String serverName=iaconf.getServerName();
        String groupCode=iaconf.getGroupCode();
        String serverVersion=iaconf.getServerVersion();
        //检查注册中心状态
        // iaENV.getRegScheduler()
        try {
            //优先配置中获取
            // 获取operaCode 路由表 /mqtt/operaRoute/groupCode/serverCode/operaCode
            String groupCodeServerCode = preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion));
            String routePath = preconditionRoutePath(groupCodeServerCode, operaCode);
//            if (CollectionUtils.isEmpty(topicArr)) {
            if (!regScheduler.exists(routePath))
                regScheduler.create(routePath, null, IaConf.EPHEMERAL);
            String data = regScheduler.getData(routePath);
            List<String> topicArr = JSONArray.parseArray(data, String.class);
            if (CollectionUtils.isEmpty(topicArr)) {
                //根据订阅表获取整合的订阅信息 <operaCode,[subTopic1,subTopic2]>
                List<String> subTopicArr = buildOperaMap(operaCode);
                regScheduler.setData(routePath, JSONArray.toJSONBytes(subTopicArr));
                topicArr = subTopicArr;
            }
            //如果路由配置只有一个元素，则默认直接选择单一元素进行发送
            if (CollectionUtils.isEmpty(topicArr)) {
                logger.error("[{}] route topic list size is null error...", operaCode);
         //       mqttLogUtil.OPERA_ERROR(StateCode.OPERA_ROUTE_EXCEPTION, operaCode);
                return new MsgResult(StateCode.OPERA_ROUTE_EXCEPTION, "[" + operaCode + "] route topic list size is null error...");
            }
            int size = topicArr.size();
            String groupServerCode = "";
            if (size == 1) {
                groupServerCode = topicArr.get(0);
                result = operaTarget(operaCode, msg, qos, timeout, timeUnit, setFlag, async, groupServerCode);
                return result;
            }
//            if (size > 1) { //默认数组多于1的情况下，识别为负载均衡
//                Random r = new Random();
//                int i = r.nextInt(size);
//                groupServerCode = topicArr.get(i);
//                result = operaTarget(operaCode, msg, qos, timeout, timeUnit, setFlag, async, groupServerCode);
//                if (result.getStateCode() != StateCode.SUCCESS) {
//                    //移除路由
//                    topicArr.remove(i);
//                    logger.warn("[{}] mqtt sender state code is failed, retry another server opera...topicArr: {}", operaCode, JSONArray.toJSONString(topicArr));
////                    serviceRegistry.setData(routePath, JSONArray.toJSONBytes(topicArr));
//                    //负载请求
            return operaBalance(operaCode, msg, qos, timeout, timeUnit, setFlag, async, topicArr, routePath); //
//                }
//                return result;
//            }
        } catch (Exception e) {
            if (logger.isDebugEnabled())
                e.printStackTrace();
         //   mqttLogUtil.OPERA_ERROR(StateCode.OPERA_ROUTE_EXCEPTION, operaCode);
            logger.error("[{}] operaCode executor error [{}]", operaCode, e.toString());
        }
        result = new MsgResult(StateCode.OPERA_ROUTE_EXCEPTION, "route request failed");
        return result;
    }

    private MsgResult operaBalance(String operaCode, String msg, int qos, long timeout, TimeUnit timeUnit, boolean setFlag, boolean async, List<String> topicArr, String routePath)   {
        IaENV iaENV= iaContext.getIaENV();
        RegScheduler regScheduler=iaENV.getRegScheduler();
        //如果路由配置只有一个元素，则默认直接选择单一元素进行发送
        int size = topicArr.size();
        String groupServerCode = "";
        if (size > 1) { //默认数组多于1的情况下，识别为负载均衡
            Random r = new Random();
            int i = r.nextInt(size);
            groupServerCode = topicArr.get(i);
            MsgResult result = operaTarget(operaCode, msg, qos, timeout, timeUnit, setFlag, async, groupServerCode);
            if (result.getStateCode() != StateCode.SUCCESS) {
                //移除路由
                topicArr.remove(i);
                logger.warn("[{}] mqtt sender state code is failed, retry another server opera...topicArr: {}", operaCode, JSONArray.toJSONString(topicArr));
                regScheduler.setData(routePath, JSONArray.toJSONBytes(topicArr));
                //重新请求
                return operaBalance(operaCode, msg, qos, timeout, timeUnit, setFlag, async, topicArr, routePath); //
            }
            return result;
        } else {
            groupServerCode = topicArr.get(0);
            MsgResult result = operaTarget(operaCode, msg, qos, timeout, timeUnit, setFlag, async, groupServerCode);
            if (result.getStateCode() != StateCode.SUCCESS) {
                topicArr.remove(0);
                logger.error("[{}] mqtt sender topicArr is empty...", operaCode);
          //      mqttLogUtil.OPERA_ERROR(StateCode.OPERA_ROUTE_EXCEPTION, operaCode);
                regScheduler.setData(routePath, JSONArray.toJSONBytes(topicArr));
            }
            return result;
        }
    }

    MsgResult operaTarget(String operaCode, String msg, int qos, long timeout, TimeUnit timeUnit, boolean setFlag, boolean async, String groupServerCode) {
        MsgScheduler msgScheduler =iaContext.getIaENV().getMsgScheduler();
        if (async) {
            boolean resultBoolean = mqttSender.sendToMqttBoolean(groupServerCode, operaCode, qos, msg);
            if (resultBoolean)
                return new MsgResult(StateCode.SUCCESS, StateCode.StateCodeEnum.toStateCodeName(StateCode.SUCCESS));
            else
                return new MsgResult(StateCode.FAILED, StateCode.StateCodeEnum.toStateCodeName(StateCode.FAILED));
        } else {
            return setFlag ? mqttSender.sendToMqttSync(groupServerCode, operaCode, qos, msg, timeout, timeUnit)
                    : mqttSender.sendToMqttSync(groupServerCode, operaCode, msg);
        }

    }

    /**
     * @return java.util.List<java.lang.String>
     * @Date 14:05 2020/1/4
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description 构建默认组装信息
     **/
    private List<String> buildOperaMap(String operaCode)  {

        IaENV iaENV= iaContext.getIaENV();
        RegScheduler regScheduler=iaENV.getRegScheduler();
        IaConf iaconf= iaENV.getConf();
        String serverName=iaconf.getServerName();
        String groupCode=iaconf.getGroupCode();


        List<String> result = new ArrayList<>();
        String subPath = preconditionGroupServerPath(TopicPrefix.SUB_PREFIX, groupCode);
        List<String> serverArr =regScheduler.getChildren(subPath); //获取订阅表的服务列表
        //遍历订阅服务列表
        for (String serverCode : serverArr) {
            String groupServerCode = preconditionGroupServerCode(groupCode, serverCode);
            String serverPath = preconditionGroupServerPath(TopicPrefix.SUB_PREFIX, groupServerCode);
            List<String> serverOperaCodeArr = regScheduler.getChildren(serverPath);
            if (serverOperaCodeArr.contains(operaCode)) {
                result.add(groupServerCode);
            }
        }
        //ROOT 节点获取服务接口信息即云管等
        String rootSubPath = preconditionGroupServerPath(TopicPrefix.SUB_PREFIX, GroupCode.ROOT);
        List<String> rootServerArr = regScheduler.getChildren(rootSubPath); //获取订阅表的服务列表
        //遍历订阅服务列表
        for (String serverCode : rootServerArr) {
            String groupServerCode = preconditionGroupServerCode(GroupCode.ROOT, serverCode);
            String serverPath = preconditionGroupServerPath(TopicPrefix.SUB_PREFIX, groupServerCode);
            List<String> serverOperaCodeArr = regScheduler.getChildren(serverPath);
            if (serverOperaCodeArr.contains(operaCode)) {
                result.add(groupServerCode);
            }
        }
        return result;
    }

    @Override
    public void broadcast(String operaCode, String msg) {
        broadcast(operaCode, msg, 0, false);
    }



    @Override
    public MsgResult slogin(String registerGroupServerCode, String sLoginPayload) {
        MsgResult slogin = mqttSender.sendToMqttSync(registerGroupServerCode,
                OperaCode.SLOGIN, 2, sLoginPayload, 30000L, TimeUnit.MILLISECONDS);
        return slogin;
    }

    /**
     * @return com.kongtrolink.framework.entity.MsgResult
     * @Date 16:12 2020/1/4
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description
     **/
    private void broadcast(String operaCode, String msg, int qos, boolean setFlag) {

        IaENV iaENV= iaContext.getIaENV();
        RegScheduler regScheduler=iaENV.getRegScheduler();
        MsgScheduler msgScheduler =iaENV.getMsgScheduler();
        IaConf iaconf= iaENV.getConf();
        String serverName=iaconf.getServerName();
        String groupCode=iaconf.getGroupCode();
        String serverVersion=iaconf.getServerVersion();
        // 获取operaCode 路由表 /mqtt/operaRoute/groupCode/serverCode/operaCode
        String groupCodeServerCode = preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion));
        String routePath = preconditionRoutePath(groupCodeServerCode, operaCode);
        try {
            if (!regScheduler.exists(routePath))
                regScheduler.create(routePath, null, IaConf.EPHEMERAL);
            String data = regScheduler.getData(routePath);
            List<String> topicArr = JSONArray.parseArray(data, String.class);
            if (CollectionUtils.isEmpty(topicArr)) {
                //根据订阅表获取整合的订阅信息 <operaCode,[subTopic1,subTopic2]>
                List<String> subTopicArr = buildOperaMap(operaCode);
                regScheduler.setData(routePath, JSONArray.toJSONBytes(subTopicArr));
                topicArr = subTopicArr;
            }
            //全部广播发送
            topicArr.forEach(groupServerCode -> {
                if (setFlag) mqttSender.sendToMqtt(groupServerCode, operaCode, qos, msg);
                else mqttSender.sendToMqtt(groupServerCode, operaCode, msg);
            });

        } catch ( Exception e) {
            if (logger.isDebugEnabled())
                e.printStackTrace();
            logger.error("[{}] operaCode executor error [{}]", operaCode, e.toString());
        }
    }


    @Override
    public MsgResult opera(String operaCode, String msg, int qos, long timeout, TimeUnit timeUnit) {
        return opera(operaCode, msg, qos, timeout, timeUnit, true, false);
    }

    @Override
    public void operaAsync(String operaCode, String msg) {
        opera(operaCode, msg, 1, 0, null, false, true);
    }
}

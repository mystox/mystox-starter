package tech.mystox.framework.mqtt.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;
import tech.mystox.framework.config.IaConf;
import tech.mystox.framework.core.IaENV;
import tech.mystox.framework.entity.*;
import tech.mystox.framework.scheduler.LoadBalanceScheduler;
import tech.mystox.framework.scheduler.RegScheduler;
import tech.mystox.framework.service.MsgHandler;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static tech.mystox.framework.common.util.MqttUtils.*;

// @Component("MqttHandler")
// @Lazy
public class MqttHandler implements MsgHandler {
    private IaENV iaENV;
    Logger logger = LoggerFactory.getLogger(MqttHandler.class);
    private ChannelHandlerAck mqttHandlerAck;
    private ChannelHandlerSub mqttHandlerImpl;
    private ChannelSenderImpl mqttSenderImpl;
    private ApplicationContext applicationContext;

    public MqttHandler(IaENV iaENV, ApplicationContext applicationContext) {
        this.iaENV = iaENV;
        this.applicationContext = applicationContext;
        this.mqttHandlerAck = applicationContext.getBean(ChannelHandlerAck.class);
        this.mqttHandlerImpl = applicationContext.getBean(ChannelHandlerSub.class);
        this.mqttSenderImpl = applicationContext.getBean(ChannelSenderImpl.class);
    }

    // @Autowired
    // public MqttHandler(IaENV iaENV) {
    //
    //     this.iaENV = iaENV;
    //     // this.mqttHandlerAck = mqttHandlerAck;
    //     // this.mqttHandlerImpl = mqttHandlerImpl;
    //     // this.mqttSenderImpl = mqttSenderImpl;
    // }


    // public ChannelHandlerAck getMqttHandlerAck() {
    //     return mqttHandlerAck;
    // }
    // public ChannelHandlerSub getMqttHandler() {
    //     return mqttHandlerImpl;
    // }
    @Override
    public void addSubTopic(String topic, int qos) {
        mqttHandlerImpl.addSubTopic(topic, qos);
    }

    @Override
    public void removeSubTopic(String... topic) {
        mqttHandlerImpl.removeSubTopic(topic);
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
        return mqttHandlerImpl.isExists(topic);

    }

    @Override
    public void addAckTopic(String topic, int qos) {
        mqttHandlerAck.addSubTopic(topic, qos);
    }

    /////////////IaOperate/////////////
    @Override
    public MsgResult opera(String operaCode, String msg) {
        return opera(operaCode, msg, 1, 0, null, false, false);
    }


    @Override
    public RegisterMsg whereIsCentre() {
        // IaENV iaENV= this.iaENV.getIaENV();
        IaConf iaconf = iaENV.getConf();
        String serverName = iaconf.getServerName();
        String groupCode = iaconf.getGroupCode();

        String registerServerName = iaconf.getRegisterServerName();
        String registerServerVersion = iaconf.getRegisterServerVersion();
        String registerUrl = iaconf.getRegisterUrl();
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
        mqttSenderImpl.sendToMqtt(serverCode,operaCode,payload);
    }

    @Override
    public void sendToMqtt(String serverCode, String operaCode, int qos, String payload) {
        mqttSenderImpl.sendToMqtt(serverCode,operaCode,qos,payload);
    }

    @Override
    public MsgResult sendToMqttSync(String serverCode, String operaCode, String payload) {
        return opera(new OperaContext(operaCode, JSONObject.toJSONString(Collections.singletonList(payload)), 2, 30000, TimeUnit.MILLISECONDS,
                iaENV.getLoadBalanceScheduler(),
                true, false));
    }

    @Override
    public MsgResult sendToMqttSync(String serverCode, String operaCode, int qos, String payload, long timeout, TimeUnit timeUnit) {
        return opera(new OperaContext(operaCode, JSONObject.toJSONString(Collections.singletonList(payload)), qos, timeout, timeUnit,
                iaENV.getLoadBalanceScheduler(),
                true, false));
    }

    @Override
    public boolean sendToMqttBoolean(String serverCode, String operaCode, int qos, String payload) {
        return false;
    }

    public MsgResult opera(OperaContext context) {
        String operaCode = context.getOperaCode();
        LoadBalanceScheduler loadBalanceScheduler = iaENV.getLoadBalanceScheduler();
        ServerMsg chooseServer = loadBalanceScheduler.chooseServer(operaCode);
        // if (chooseServer == null) {
        //     logger.error("[{}] route server is null error...", operaCode);
        //     //       mqttLogUtil.OPERA_ERROR(StateCode.OPERA_ROUTE_EXCEPTION, operaCode);
        //     return new MsgResult(StateCode.OPERA_ROUTE_EXCEPTION, "[" + operaCode + "] route topic list size is null error...");
        // }
        String targetServerCode = "";
        if (chooseServer != null)
            targetServerCode = preconditionGroupServerCode(chooseServer.getGroupCode(),
                    preconditionServerCode(chooseServer.getServerName(), chooseServer.getServerVersion(), chooseServer.getSequence()));

        MsgResult result = loadBalanceScheduler.operaCall((oCode, retryServerCode) -> operaTarget(oCode, context.getMsg(),
                context.getQos(), context.getTimeout(), context.getTimeUnit(),
                context.isSetFlag(), context.isAsync(),
                retryServerCode), targetServerCode, operaCode);
        return result;
        // return loadBalanceScheduler.operaCall(operaTarget(operaCode, context.getMsg(),
        //         context.getQos(), context.getTimeout(), context.getTimeUnit(),
        //         context.isSetFlag(), context.isAsync(),
        //         targetServerCode),this);
        // return operaTarget(operaCode, context.getMsg(),
        //         context.getQos(), context.getTimeout(), context.getTimeUnit(),
        //         context.isSetFlag(), context.isAsync(),
        //         targetServerCode);
    }

    /**
     * @param setFlag 设置超时
     * @return tech.mystox.framework.entity.MsgResult
     * @Date 16:12 2020/1/4
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description
     **/
    private MsgResult opera(String operaCode, String msg, int qos, long timeout, TimeUnit timeUnit, boolean setFlag, boolean async) {
        return opera(new OperaContext(operaCode, msg, qos, timeout, timeUnit, iaENV.getLoadBalanceScheduler(), setFlag, async));
       /* MsgResult result;

        // IaENV iaENV= this.iaENV.getIaENV();
        // RegScheduler regScheduler=iaENV.getRegScheduler();
        // IaConf iaconf= iaENV.getConf();
        // String serverName=iaconf.getServerName();
        // String groupCode=iaconf.getGroupCode();
        // String serverVersion=iaconf.getServerVersion();

        // loadBalanceScheduler.addServers();
        LoadBalanceScheduler loadBalanceScheduler = iaENV.getLoadBalanceScheduler();
        ServerMsg chooseServer = loadBalanceScheduler.chooseServer(operaCode);
        String groupServerCode = preconditionServerCode(chooseServer.getServerName(), chooseServer.getServerVersion());
        result = operaTarget(operaCode, msg, qos, timeout, timeUnit, setFlag, async, groupServerCode);
        return result;*/

        //检查注册中心状态
        // iaENV.getRegScheduler()
        /*try {
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
        return result;*/
    }


    /*private MsgResult operaBalance(String operaCode, String msg, int qos, long timeout, TimeUnit timeUnit, boolean setFlag, boolean async, List<String> topicArr, String routePath)   {
        // IaENV iaENV= this.iaENV.getIaENV();
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
*/
    protected MsgResult operaTarget(String operaCode, String msg, int qos, long timeout, TimeUnit timeUnit, boolean setFlag, boolean async, String groupServerCode) {
        // MsgScheduler msgScheduler =iaENV.getIaENV().getMsgScheduler();
        if (async) { //异步请求
            boolean resultBoolean = mqttSenderImpl.sendToMqttBoolean(groupServerCode, operaCode, qos, msg);
            if (resultBoolean)
                return new MsgResult(StateCode.SUCCESS, StateCode.StateCodeEnum.toStateCodeName(StateCode.SUCCESS));
            else
                return new MsgResult(StateCode.FAILED, StateCode.StateCodeEnum.toStateCodeName(StateCode.FAILED));
        } else {
            return setFlag ? mqttSenderImpl.sendToMqttSync(groupServerCode, operaCode, qos, msg, timeout, timeUnit)
                    : mqttSenderImpl.sendToMqttSync(groupServerCode, operaCode, msg);
        }

    }

    /**
     * @Date 14:05 2020/1/4
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description 构建默认组装信息
     **//*
    private List<String> buildOperaMap(String operaCode)  {

        // IaENV iaENV= this.iaENV.getIaENV();
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
    }*/
    @Override
    public void broadcast(String operaCode, String msg) {
        broadcast(operaCode, msg, 0, false);
    }



   /* @Override
    public MsgResult slogin(String registerGroupServerCode, String sLoginPayload) {
        MsgResult slogin = mqttSender.sendToMqttSync(registerGroupServerCode,
                OperaCode.SLOGIN, 2, sLoginPayload, 30000L, TimeUnit.MILLISECONDS);
        return slogin;
    }*/

    /**
     * @return tech.mystox.framework.entity.MsgResult
     * @Date 16:12 2020/1/4
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description
     **/
    private void broadcast(String operaCode, String msg, int qos, boolean setFlag) {

        // IaENV iaENV= this.iaENV.getIaENV();
        RegScheduler regScheduler = iaENV.getRegScheduler();
        // MsgScheduler msgScheduler =iaENV.getMsgScheduler();
        IaConf iaconf = iaENV.getConf();
        String serverName = iaconf.getServerName();
        String groupCode = iaconf.getGroupCode();
        String serverVersion = iaconf.getServerVersion();
        // 获取operaCode 路由表 /mqtt/operaRoute/groupCode/serverCode/operaCode
        String groupCodeServerCode = preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion));
        String routePath = preconditionRoutePath(groupCodeServerCode, operaCode);
        try {
            if (!regScheduler.exists(routePath))
                regScheduler.create(routePath, null, IaConf.EPHEMERAL);
//        String data = regScheduler.getData(routePath);
//        List<String> topicArr = JSONArray.parseArray(data, String.class);
            List<String> topicArr = iaENV.getLoadBalanceScheduler().getOperaRouteArr(operaCode);
            if (CollectionUtils.isEmpty(topicArr)) {
                //根据订阅表获取整合的订阅信息 <operaCode,[subTopic1,subTopic2]>
                List<String> subTopicArr = regScheduler.buildOperaMap(operaCode);
//                List<String> subTopicArr = iaENV.getLoadBalanceScheduler().getOperaRouteArr(operaCode);
                regScheduler.setData(routePath, JSONArray.toJSONBytes(subTopicArr));
                topicArr = subTopicArr;
            }
            //全部广播发送
            topicArr.forEach(groupServerCode -> {
                if (setFlag) mqttSenderImpl.sendToMqtt(groupServerCode, operaCode, qos, msg);
                else mqttSenderImpl.sendToMqtt(groupServerCode, operaCode, msg);
            });

        } catch (Exception e) {
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

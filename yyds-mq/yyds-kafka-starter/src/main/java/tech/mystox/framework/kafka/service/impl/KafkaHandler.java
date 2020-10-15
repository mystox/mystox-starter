package tech.mystox.framework.kafka.service.impl;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;
import tech.mystox.framework.common.util.KafkaUtils;
import tech.mystox.framework.config.IaConf;
import tech.mystox.framework.core.IaENV;
import tech.mystox.framework.entity.*;
import tech.mystox.framework.scheduler.LoadBalanceScheduler;
import tech.mystox.framework.scheduler.RegScheduler;
import tech.mystox.framework.service.MsgHandler;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static tech.mystox.framework.common.util.MqttUtils.*;

// @Component("MqttHandler")
// @Lazy
public class KafkaHandler implements MsgHandler {
    private IaENV iaENV;
    Logger logger = LoggerFactory.getLogger(KafkaHandler.class);
    private ChannelHandlerAck kafkaHandlerAck;
    private ChannelHandlerSub kafkaHandlerImpl;
    private ChannelSenderImpl kafkaSenderImpl;

    public KafkaHandler(IaENV iaENV, ApplicationContext applicationContext) {
        this.iaENV = iaENV;
        this.kafkaHandlerAck = applicationContext.getBean(ChannelHandlerAck.class);
        this.kafkaHandlerImpl = applicationContext.getBean(ChannelHandlerSub.class);
        this.kafkaSenderImpl = applicationContext.getBean(ChannelSenderImpl.class);
    }

    @Override
    public void addSubTopic(String topic, int qos) {
        kafkaHandlerImpl.addSubTopic(KafkaUtils.toKafkaTopic(topic), qos);
    }

    @Override
    public void removeSubTopic(String... topic) {
        kafkaHandlerImpl.removeSubTopic(topic);
    }

    @Override
    public void removeAckSubTopic(String... topic) {
        kafkaHandlerAck.removeSubTopic(topic);
    }

    @Override
    public boolean isAckExists(String topic) {
        return kafkaHandlerAck.isExists(KafkaUtils.toKafkaTopic(topic));
    }

    @Override
    public boolean isExists(String topic) {
        return kafkaHandlerImpl.isExists(KafkaUtils.toKafkaTopic(topic));

    }

    @Override
    public void addAckTopic(String topic, int qos) {
        kafkaHandlerAck.addSubTopic(KafkaUtils.toKafkaTopic(topic), qos);
    }

    /////////////IaOperate/////////////
    @Override
    public MsgResult opera(String operaCode, String msg) {
        return opera(operaCode, msg, 1, 0, null, false, false);
    }


    @Override
    public RegisterMsg whereIsCentre() {
        IaConf iaconf = iaENV.getConf();
        String serverName = iaconf.getServerName();
        String registerUrl = iaconf.getRegisterUrl();
        RegisterMsg registerMsg = new RegisterMsg();
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

    public MsgResult opera(OperaContext context) {
        String operaCode = context.getOperaCode();
        LoadBalanceScheduler loadBalanceScheduler = iaENV.getLoadBalanceScheduler();
        ServerMsg chooseServer = loadBalanceScheduler.chooseServer(operaCode);
        String targetServerCode = "";
        if (chooseServer != null)
            targetServerCode = preconditionGroupServerCode(chooseServer.getGroupCode(),
                    preconditionServerCode(chooseServer.getServerName(), chooseServer.getServerVersion(), chooseServer.getSequence()));
        MsgResult result = loadBalanceScheduler.operaCall((oCode, retryServerCode) -> operaTarget(oCode, context.getMsg(),
                context.getQos(), context.getTimeout(), context.getTimeUnit(),
                context.isSetFlag(), context.isAsync(),
                retryServerCode), targetServerCode, operaCode);
        return result;
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
    }

    protected MsgResult operaTarget(String operaCode, String msg, int qos, long timeout, TimeUnit timeUnit, boolean setFlag, boolean async, String groupServerCode) {
        // MsgScheduler msgScheduler =iaENV.getIaENV().getMsgScheduler();
        if (async) { //异步请求
            boolean resultBoolean = kafkaSenderImpl.sendToMqttBoolean(groupServerCode, operaCode, qos, msg);
            if (resultBoolean)
                return new MsgResult(StateCode.SUCCESS, StateCode.StateCodeEnum.toStateCodeName(StateCode.SUCCESS));
            else
                return new MsgResult(StateCode.FAILED, StateCode.StateCodeEnum.toStateCodeName(StateCode.FAILED));
        } else {
            return setFlag ? kafkaSenderImpl.sendToMqttSync(groupServerCode, operaCode, qos, msg, timeout, timeUnit)
                    : kafkaSenderImpl.sendToMqttSync(groupServerCode, operaCode, msg);
        }

    }

    @Override
    public void broadcast(String operaCode, String msg) {
        broadcast(operaCode, msg, 0, false);
    }

    /**
     * @return tech.mystox.framework.entity.MsgResult
     * @Date 16:12 2020/1/4
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description
     **/
    private void broadcast(String operaCode, String msg, int qos, boolean setFlag) {

        RegScheduler regScheduler = iaENV.getRegScheduler();
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
                if (setFlag) kafkaSenderImpl.sendToMqtt(groupServerCode, operaCode, qos, msg);
                else kafkaSenderImpl.sendToMqtt(groupServerCode, operaCode, msg);
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

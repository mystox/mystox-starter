package com.kongtrolink.framework.mqtt.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.common.util.MqttUtils;
import com.kongtrolink.framework.entity.*;
import com.kongtrolink.framework.mqtt.service.IMqttSender;
import com.kongtrolink.framework.mqtt.util.MqttLogUtil;
import com.kongtrolink.framework.register.service.ServiceRegistry;
import com.kongtrolink.framework.service.MqttHandler;
import com.kongtrolink.framework.service.MqttSender;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

import static com.kongtrolink.framework.mqtt.config.MqttConfig.CHANNEL_REPLY;

/**
 * Created by mystoxlol on 2019/8/19, 8:28.
 * company: kongtrolink
 * description: 封装的mqtt生产者
 * update record:
 */
@Service
public class MqttSenderImpl implements MqttSender {

    Logger logger = LoggerFactory.getLogger(MqttSenderImpl.class);

    protected static final Map<String, CallBackTopic> CALLBACKS = new ConcurrentHashMap<>();

    @Value("${mqtt.producer.defaultTopic}")
    private String producerDefaultTopic;

    @Value("${server.name}")
    private String serverName;

    @Value("${server.version}")
    private String serverVersion;

    @Value("${server.name}_${server.version}")
    private String serverCode;
    @Autowired
    private IMqttSender mqttSender;
    @Autowired
    @Qualifier("mqttHandlerImpl")
    private MqttHandler mqttHandler;
    @Autowired
    @Qualifier("mqttHandlerAck")
    private MqttHandler mqttHandlerAck;

    @Autowired
    private MqttLogUtil mqttLogUtil;
    @Autowired
    ThreadPoolTaskExecutor mqttExecutor;

    @Override
    public void sendToMqtt(String serverCode, String operaCode, String payload) {
        String localServerCode = this.serverName + "_" + this.serverVersion;
        //组建topicid
        String topic = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
        //组建消息体
        MqttMsg mqttMsg = buildMqttMsg(topic, localServerCode, payload, operaCode);
        String msgId = mqttMsg.getMsgId();
        //获取目标topic列表，判断sub_list是否有人订阅处理
        try {

            if (isExistsBySubList(serverCode, operaCode)) {
                boolean existsByPubList = addPubList(serverCode, operaCode);
                if (existsByPubList) {
                    logger.info("[{}]message send...topic[{}]", msgId, topic, JSONObject.toJSONString(mqttMsg));
                    mqttSender.sendToMqtt(topic, JSONObject.toJSONString(mqttMsg));
                } else {
                    mqttLogUtil.ERROR(msgId, StateCode.UNREGISTY, operaCode, serverCode);
                    logger.error("[{}]message send error[{}]... pub operaCode[{}.{}] is not exists...", msgId, StateCode.UNREGISTY, serverCode, operaCode);
                }
            } else {
                mqttLogUtil.ERROR(msgId, StateCode.UNREGISTY, operaCode, serverCode);
                logger.error("[{}]message send error[{}] sub operaCode[{}.{}] is not exists...", msgId, StateCode.UNREGISTY, serverCode, operaCode);
            }

        } catch (KeeperException e) {
            mqttLogUtil.ERROR(msgId, StateCode.UNREGISTY, operaCode, serverCode);
            logger.error("[{}]message send error[{}]...[{}]", msgId, StateCode.UNREGISTY, e.toString());
            e.printStackTrace();
        } catch (InterruptedException e) {
            mqttLogUtil.ERROR(msgId, StateCode.CONNECT_INTERRUPT, operaCode, serverCode);
            logger.error("[{}]message send error[{}]...[{}]", msgId, StateCode.CONNECT_INTERRUPT, e.toString());
            e.printStackTrace();
        }
    }


    @Override
    public void sendToMqtt(String serverCode, String operaCode,
                           int qos, String payload) {
        String localServerCode = this.serverName + "_" + this.serverVersion;
        //组建topicid
        String topic = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
        //组建消息体
        MqttMsg mqttMsg = buildMqttMsg(topic, localServerCode, payload, operaCode);
        String msgId = mqttMsg.getMsgId();
        try {
            //获取目标topic列表，判断sub_list是否有人订阅处理
            if (isExistsBySubList(serverCode, operaCode)) {
                boolean existsByPubList = addPubList(serverCode, operaCode);
                if (existsByPubList) {
                    mqttSender.sendToMqtt(topic, qos, JSONObject.toJSONString(mqttMsg));
                } else {
                    mqttLogUtil.ERROR(msgId, StateCode.UNREGISTY, operaCode, serverCode);
                    logger.error("[{}]message send error[{}]... pub operaCode[{}.{}] is not exists...", msgId, StateCode.UNREGISTY, serverCode, operaCode);
                }
            } else {
                mqttLogUtil.ERROR(msgId, StateCode.UNREGISTY, operaCode, serverCode);
                logger.error("[{}]message send error[{}] sub operaCode[{}.{}] is not exists...", msgId, StateCode.UNREGISTY, serverCode, operaCode);
            }
        } catch (KeeperException e) {
            mqttLogUtil.ERROR(msgId, StateCode.UNREGISTY, operaCode, serverCode);
            logger.error("[{}]message send error[{}]...[{}]", msgId, StateCode.UNREGISTY, e.toString());
            e.printStackTrace();
        } catch (InterruptedException e) {
            mqttLogUtil.ERROR(msgId, StateCode.CONNECT_INTERRUPT, operaCode, serverCode);
            logger.error("[{}]message send error[{}]...[{}]", msgId, StateCode.CONNECT_INTERRUPT, e.toString());
            e.printStackTrace();
        }
    }

    public boolean sendToMqttBoolean(String serverCode, String operaCode,
                                     int qos, MqttMsg mqttMsg) {
//        String localServerCode = this.serverName + "_" + this.serverVersion;
        String msgId = mqttMsg.getMsgId();
        try {
            //获取目标topic列表，判断sub_list是否有人订阅处理
            if (isExistsBySubList(serverCode, operaCode)) {
                boolean existsByPubList = addPubList(serverCode, operaCode); //将此请求注册至请求列表，
                if (existsByPubList) {
                    //组建topicid
                    String topic = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
                    mqttMsg.setOperaCode(operaCode);
                    String mqttMsgJson = JSONObject.toJSONString(mqttMsg);
                    logger.debug("[{}]message [{}] send...", msgId, mqttMsgJson);
                    mqttSender.sendToMqtt(topic, qos, mqttMsgJson);
                    return true;
                } else {
                    mqttLogUtil.ERROR(msgId, StateCode.UNREGISTY, operaCode, serverCode);
                    logger.error("[{}]message send error[{}]... pub operaCode[{}.{}] is not exists...", msgId, StateCode.UNREGISTY, serverCode, operaCode);
                }
            } else {
                mqttLogUtil.ERROR(msgId, StateCode.UNREGISTY, operaCode, serverCode);
                logger.error("[{}]message send error[{}] sub operaCode[{}.{}] is not exists...", msgId, StateCode.UNREGISTY, serverCode, operaCode);
                return false;
            }
        } catch (KeeperException e) {
            mqttLogUtil.ERROR(msgId, StateCode.UNREGISTY, operaCode, serverCode);
            logger.error("[{}]message send error[{}]...[{}]", msgId, StateCode.UNREGISTY, e.toString());
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            mqttLogUtil.ERROR(msgId, StateCode.CONNECT_INTERRUPT, operaCode, serverCode);
            logger.error("[{}]message send error[{}]...[{}]", msgId, StateCode.CONNECT_INTERRUPT, e.toString());
            e.printStackTrace();
            return false;
        }
        mqttLogUtil.ERROR(msgId, StateCode.UNREGISTY, operaCode, serverCode);
        logger.error("[{}]message send error[{}]...", msgId, StateCode.UNREGISTY);
        return false;
    }


    @Override
    public MsgResult sendToMqttSyn(String serverCode, String operaCode, int qos, String payload, long timeout, TimeUnit timeUnit) {
        String topic = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
//        String localServerCode = this.serverName + "_" + this.serverVersion;
        //组建消息体
//        String topicAck = MqttUtils.preconditionSubTopicId(this.serverCode, operaCode) + "/ack";
        String topicAck = mqttHandlerAck.assembleSubTopic(operaCode);
        if (!mqttHandlerAck.isExists(topicAck))
            mqttHandlerAck.addSubTopic(topicAck);
        //组建消息体
        MqttMsg mqttMsg = buildMqttMsg(topic, this.serverCode, payload, operaCode);
        String msgId = mqttMsg.getMsgId();
        boolean sendResult = sendToMqttBoolean(serverCode, operaCode, qos, mqttMsg);
        if (sendResult) {
            ExecutorService es = Executors.newSingleThreadExecutor();
            CallBackTopic callBackTopic = new CallBackTopic();
            FutureTask<MqttResp> mqttMsgFutureTask = new FutureTask<>(callBackTopic);
            es.submit(mqttMsgFutureTask);
            CALLBACKS.put(msgId, callBackTopic);
            try {
                MqttResp resp = mqttMsgFutureTask.get(timeout, timeUnit);
                return new MsgResult(resp.getStateCode(), resp.getPayload());
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                mqttLogUtil.ERROR(msgId, StateCode.TIMEOUT, operaCode, serverCode);
                logger.error("[{}]message, request timeout: [{}]", msgId, e.toString());
                return new MsgResult(StateCode.TIMEOUT, e.toString());
            } finally {
                CALLBACKS.remove(msgId);
            }
        }
//        mqttLogUtil.ERROR(msgId, StateCode.FAILED, operaCode, serverCode);
        return new MsgResult(StateCode.FAILED, "请求失败");
    }

    @Override
    public MsgResult sendToMqttSyn(String serverCode, String operaCode, String payload) {
        return sendToMqttSyn(serverCode, operaCode, 2, payload, 30000L, TimeUnit.MILLISECONDS);
    }

    private boolean addPubList(String serverCode, String operaCode) throws KeeperException, InterruptedException {
        if (OperaCode.SLOGIN.equals(operaCode) && serverCode.contains(ServerName.AUTH_PLATFORM)) { //注册登录时跳过注册请求列表，因为注册服务客户端还未初始化
            logger.warn("server Slogin to {} jump pubList judged...", serverCode);
            return true;
        }
        if (!serviceRegistry.exists(TopicPrefix.PUB_PREFIX + "/" + serverCode)) return false;
        //是否已经发布,没有发布则往注册中心注册请求列表
        String topicId = MqttUtils.preconditionPubTopicId(serverCode, operaCode);
        if (!serviceRegistry.exists(topicId)) {
            //不存在这个请求列表
            logger.warn("topicId(nodePath) [{}] didn't registered...", topicId);
            serviceRegistry.create(topicId, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        }
        String pubPath = topicId + "/" + this.serverCode; //请求列表的节点path 带上 pub的此服务serverCode做临时节点
        if (!serviceRegistry.exists(pubPath)) {
            logger.warn("pubPath(nodePath) [{}] didn't registered...", pubPath);
            serviceRegistry.create(pubPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        }

        return true;
    }

    @Autowired
    ServiceRegistry serviceRegistry;

    private boolean isExistsBySubList(String serverCode, String operaCode) throws KeeperException, InterruptedException {
        if (OperaCode.SLOGIN.equals(operaCode) && serverCode.contains(ServerName.AUTH_PLATFORM)) {
            logger.warn("server Slogin to {} jump subList judged...", serverCode);
            return true;
        }
        String topicId = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
        if (!serviceRegistry.exists(topicId)) {
            logger.warn("topicId(nodePath) [{}] didn't registered...", topicId);
            return false;
        }
        return true;
    }


    private MqttMsg buildMqttMsg(String topicId, String localServerCode, String payload, String operaCode) {
        MqttMsg mqttMsg = new MqttMsg();
        mqttMsg.setTopic(topicId);
        mqttMsg.setPayloadType(PayloadType.STRING);
        mqttMsg.setOperaCode(operaCode);
        mqttMsg.setPayload(payload);
        mqttMsg.setSourceAddress(localServerCode);
        return mqttMsg;
    }

    /**
     * 回复通道
     *
     * @param message
     */
    @ServiceActivator(inputChannel = CHANNEL_REPLY)
    public void messageReceiver(Message<String> message) {
        mqttExecutor.execute(() -> {
            try {
                String payload = message.getPayload();
                MqttResp resp = JSONObject.parseObject(payload, MqttResp.class);
                String msgId = resp.getMsgId();

                logger.debug("[{}]message ack is [{}]", msgId, payload);
                CallBackTopic callBackTopic = CALLBACKS.get(msgId);
                if (callBackTopic != null) {
                    boolean subpackage = resp.isSubpackage();
                    if (subpackage)
                        callBackTopic.callbackSubPackage(resp);
                    else
                        callBackTopic.callback(resp);
                } else {
                    logger.warn("[{}]message ack [{}] is Invalidation...", msgId);
                }
            } catch (Exception e) {
                logger.warn("message ack receive error[{}] is Invalidation...", e.toString());
                e.printStackTrace();
            }
        });

    }
}

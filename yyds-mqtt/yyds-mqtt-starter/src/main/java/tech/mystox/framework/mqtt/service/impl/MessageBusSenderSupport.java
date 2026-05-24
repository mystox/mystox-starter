package tech.mystox.framework.mqtt.service.impl;

import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import tech.mystox.framework.common.util.ByteUtil;
import tech.mystox.framework.common.util.MqttUtils;
import tech.mystox.framework.config.IaConf;
import tech.mystox.framework.core.IaENV;
import tech.mystox.framework.entity.*;
import tech.mystox.framework.exception.RegisterException;
import tech.mystox.framework.mqtt.service.MessageBusOperator;
import tech.mystox.framework.scheduler.RegScheduler;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static tech.mystox.framework.common.util.MqttUtils.*;

/**
 * Created by mystoxlol on 2019/8/19, 8:28.
 * company: mystox
 * description: 封装的mqtt生产者
 * update record:
 */
//@Service("messageBusOperator")
public class MessageBusSenderSupport implements MessageBusOperator {

    Logger logger = LoggerFactory.getLogger(MessageBusSenderSupport.class);
    //@Value("${mqtt.payload.limit:#{47 * 1024}}")
    private int mqttPayloadLimit;
    protected static final Map<String, CallSubpackageMsg<MsgRsp>> CALLBACKS = new ConcurrentHashMap<>();

    //@Value("${mqtt.callback.maxCount:10000}")
    private long callbackMaxCount;
    final IaENV iaEnv;
    final IaConf iaConf;
    //private final MqttLogUtil mqttLogUtil;

    public MessageBusSenderSupport(IaENV iaEnv, IaConf iaConf) {
        this.iaEnv = iaEnv;
        this.iaConf = iaConf;
        //this.mqttLogUtil = mqttLogUtil;
        this.callbackMaxCount = 10000;
        this.mqttPayloadLimit = 47 * 1024;
    }

    public void setMqttPayloadLimit(int mqttPayloadLimit) {
        this.mqttPayloadLimit = mqttPayloadLimit;
    }

    public void setCallbackMaxCount(long callbackMaxCount) {
        this.callbackMaxCount = callbackMaxCount;
    }

    @Override
    public void sendToMqtt(String serverCode, String operaCode, String payload) throws Exception {
        //组建topicid
        String topic = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
        //组建消息体
        String msgId = assembleMsgId();
        List<MqttMsg> mqttMsgList = buildMqttMsg(msgId, topic, payload, operaCode, false);
        //获取目标topic列表，判断sub_list是否有人订阅处理
        if (isExistsBySubList(serverCode, operaCode)) {
            boolean packageFlag = false; //分包标记
            for (MqttMsg mqttMsg : mqttMsgList) {
                if (packageFlag) {
                    try {
                        Thread.sleep(10L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String mqttMsgJson = JSONObject.toJSONString(mqttMsg);
                logger.debug("[{}]message [{}] send...", msgId, mqttMsgJson);
                publishToBus(topic, mqttMsgJson);
                packageFlag = true;
            }
        } else {
            //mqttLogUtil.ERROR(msgId, StateCode.UNREGISTERED, operaCode, serverCode);
            logger.error("[{}]message send error[{}] sub operaCode[{}.{}] is not exists...", msgId, StateCode.StateCodeEnum.UNREGISTERED, serverCode, operaCode);
        }
    }


    @Override
    public void sendToMqtt(String serverCode, String operaCode, int qos, String payload) throws Exception {
        //组建topicid
        String topic = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
        //组建消息体
        String msgId = assembleMsgId();
        List<MqttMsg> mqttMsgList = buildMqttMsg(msgId, topic, payload, operaCode, false);
        //获取目标topic列表，判断sub_list是否有人订阅处理
        if (isExistsBySubList(serverCode, operaCode)) {
            boolean packageFlag = false; //分包标记
            for (MqttMsg mqttMsg : mqttMsgList) {
                if (packageFlag) {
                    try {
                        Thread.sleep(10L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String mqttMsgJson = JSONObject.toJSONString(mqttMsg);
                logger.debug("[{}]message [{}] send...", msgId, mqttMsgJson);
                publishToBus(topic, qos, mqttMsgJson);
                packageFlag = true;
            }
        } else {
            //mqttLogUtil.ERROR(msgId, StateCode.UNREGISTERED, operaCode, serverCode);
            logger.error("[{}]message send error[{}] sub operaCode[{}.{}] is not exists...", msgId, StateCode.StateCodeEnum.UNREGISTERED, serverCode, operaCode);
        }
    }

    public boolean sendToMqttBoolean(String msgId, String serverCode, String operaCode,
                                     int qos, List<MqttMsg> mqttMsgArr) {
        try {
            //获取目标topic列表，判断sub_list是否有人订阅处理
            if (isExistsBySubList(serverCode, operaCode)) {
                //组建topicid
                String topic = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
                boolean packageFlag = false; //分包标记
                for (MqttMsg mqttMsg : mqttMsgArr) {
                    if (packageFlag) Thread.sleep(10L);
                    String mqttMsgJson = JSONObject.toJSONString(mqttMsg);
                    logger.debug("[{}]message [{}] send...", msgId, mqttMsgJson);
                    publishToBus(topic, qos, mqttMsgJson);
                    packageFlag = true;
                }
                return true;
            } else {
                //mqttLogUtil.ERROR(msgId, StateCode.UNREGISTERED, operaCode, serverCode);
                logger.error("[{}]message send error[{}] sub operaCode[{}.{}] is not exists...", msgId, StateCode.StateCodeEnum.UNREGISTERED, serverCode, operaCode);
                return false;
            }
        } catch (Exception e) {
            //mqttLogUtil.ERROR(msgId, StateCode.MESSAGE_EXCEPTION, operaCode, serverCode);
            logger.error("[{}]message send error[{}]...[{}]", msgId, StateCode.StateCodeEnum.MESSAGE_EXCEPTION, e.toString());
            if (logger.isDebugEnabled()) e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean sendToMqttBoolean(String serverCode, String operaCode, int qos, String payload) {
        //组建topicid
        String topic = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
        //组建消息体
        String msgId = assembleMsgId();
        List<MqttMsg> mqttMsgs = buildMqttMsg(msgId, topic, payload, operaCode, false);
        return sendToMqttBoolean(msgId, serverCode, operaCode, qos, mqttMsgs);
    }


    @Override
    public MsgResult sendToMqttSync(String serverCode, String operaCode, int qos, String payload, long timeout, TimeUnit timeUnit) {
        String topic = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
        //组建消息体
        String msgId = assembleMsgId();
        List<MqttMsg> mqttMsgArr = buildMqttMsg(msgId, topic, payload, operaCode, true);
        CallSubpackageMsg<MsgRsp> callBackTopic = new CallSubpackageMsg<>();
        int size = CALLBACKS.size();
        if (size > callbackMaxCount) {
            //mqttLogUtil.ERROR(msgId, StateCode.CALLBACK_FULL, operaCode, serverCode);
            logger.error("[{}]message, Callback map full[{}]", msgId, size);
            return new MsgResult(StateCode.StateCodeEnum.CALLBACK_FULL.getCode(), StateCode.StateCodeEnum.CALLBACK_FULL.getStateCodeName());
        }

        //ExecutorService es = Executors.newSingleThreadExecutor();
        CALLBACKS.put(msgId, callBackTopic);
        //CompletableFuture<MsgRsp> future = callBackTopic.getFuture();
        //FutureTask<MsgRsp> mqttMsgFutureTask = new FutureTask<>(callBackTopic);
        try {
            boolean sendResult = sendToMqttBoolean(msgId, serverCode, operaCode, qos, mqttMsgArr);
            if (!sendResult) {
                return new MsgResult(
                        StateCode.StateCodeEnum.FAILED.getCode(),
                        "send failed"
                );
            }

            MsgRsp resp = callBackTopic.get(timeout, timeUnit);
            return new MsgResult(resp.getStateCode(), resp.getPayload());

            //if (sendResult) {
            //    es.submit(mqttMsgFutureTask);
            //    MsgRsp resp = mqttMsgFutureTask.get(timeout, timeUnit);
            //    return new MsgResult(resp.getStateCode(), resp.getPayload());
            //}
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            //mqttLogUtil.ERROR(msgId, StateCode.TIMEOUT, operaCode, serverCode);
            logger.error("[{}]message{},{}, request timeout: [{}][{}]", msgId, serverCode, operaCode, timeout, e.toString());
            if (logger.isDebugEnabled()) e.printStackTrace();
            return new MsgResult(StateCode.StateCodeEnum.TIMEOUT.getCode(), timeout + "|" + e.toString());
        } catch (Exception e) {
            //mqttLogUtil.ERROR(msgId, StateCode.FAILED, operaCode, serverCode);
            logger.error("[{}]message, request exception: [{}]", msgId, e.toString());
            if (logger.isDebugEnabled()) e.printStackTrace();
            return new MsgResult(StateCode.StateCodeEnum.FAILED.getCode(), e.toString());
        } finally {
            //mqttMsgFutureTask.cancel(true);
            //es.shutdown();
            //if (!future.isDone()) {
            //    future.cancel(true);
            //}
            CALLBACKS.remove(msgId);
        }
        //        mqttLogUtil.ERROR(msgId, StateCode.FAILED, operaCode, serverCode);

    }


    @Override
    public MsgResult sendToMqttSync(String serverCode, String operaCode, String payload) {
        return sendToMqttSync(serverCode, operaCode, 2, payload, 30000L, TimeUnit.MILLISECONDS);
    }

    protected void publishToBus(String topic, String payload) throws Exception {
        throw new UnsupportedOperationException("MessageBusSenderSupport requires publishToBus implementation");
    }

    protected void publishToBus(String topic, int qos, String payload) throws Exception {
        throw new UnsupportedOperationException("MessageBusSenderSupport requires publishToBus implementation");
    }

    /*private boolean addPubList(String serverCode, String operaCode) throws KeeperException, InterruptedException {
        RegScheduler regScheduler=iaContext.getIaENV().getRegScheduler();
        if (OperaCode.SLOGIN.equals(operaCode) && serverCode.contains(ServerName.AUTH_PLATFORM)) { //注册登录时跳过注册请求列表，因为注册服务客户端还未初始化
            logger.warn("server Slogin to {} jump pubList judged...", serverCode);
            return true;
        }
        if (!regScheduler.exists(TopicPrefix.PUB_PREFIX + "/" + serverCode)) return false;
        //是否已经发布,没有发布则往注册中心注册请求列表
        String topicId = MqttUtils.preconditionPubTopicId(serverCode, operaCode);
        if (!regScheduler.exists(topicId)) {
            //不存在这个请求列表
            logger.warn("topicId(nodePath) [{}] didn't registered...", topicId);
            regScheduler.create(topicId, null,  IaConf.PERSISTENT);

        }
        String pubPath = topicId + "/" + this.serverCode; //请求列表的节点path 带上 pub的此服务serverCode做临时节点
        if (!regScheduler.exists(pubPath)) {
            logger.debug("pubPath(nodePath) [{}] didn't registered...", pubPath);
            regScheduler.create(pubPath, null, IaConf.EPHEMERAL);
        }

        return true;
    }*/

    //    @Autowired
    //    ServiceRegistry serviceRegistry;

    private boolean isExistsBySubList(String serverCode, String operaCode) throws RegisterException /*throws KeeperException, InterruptedException */ {
        RegScheduler regScheduler = iaEnv.getRegScheduler();
        /*if (OperaCode.SLOGIN.equals(operaCode) && serverCode.contains(ServerName.AUTH_PLATFORM)) {
           logger.warn("server Slogin to {} jump subList judged...", serverCode);
            return true;
        }*/
        String topicId = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
        if (!regScheduler.exists(topicId)) {
            logger.warn("topicId(nodePath) [{}] didn't registered...", topicId);
            return false;
        }
        return true;
    }


    private List<MqttMsg> buildMqttMsg(String msgId, String topicId, String payload, String operaCode, Boolean hashAck) {
        byte[] bytes = payload.getBytes(StandardCharsets.UTF_8);
        int length = bytes.length;
        if (length > mqttPayloadLimit) {
            //分包
            return subpackage(bytes, topicId, operaCode, msgId, hashAck);
        }
        MqttMsg mqttMsg = new MqttMsg();
        mqttMsg.setMsgId(msgId);
        mqttMsg.setTopic(topicId);
        mqttMsg.setPayloadType(PayloadType.STRING);
        mqttMsg.setOperaCode(operaCode);
        mqttMsg.setPayload(payload);
        mqttMsg.setHasAck(hashAck);
        mqttMsg.setSourceAddress(preconditionGroupServerCode(iaConf.getGroupCode(),
                preconditionServerCode(iaConf.getServerName(), iaConf.getServerVersion(), iaConf.getSequence())));
        return Collections.singletonList(mqttMsg);
    }

    /**
     * 发送消息过大分包处理
     *
     * 
     *  hashAck
     * @return
     */
    private List<MqttMsg> subpackage(byte[] bytes, String topicId, String operaCode, String msgId, Boolean hashAck) {
        List<MqttMsg> result = new ArrayList<>();
        int crc = ByteUtil.getCRC(bytes);
        int length = bytes.length;
        int count = length / mqttPayloadLimit + 1;
        logger.warn("[{}]Subpackage mqtt msg length[{}] is large than mqtt payload limit[{}], count[{}]", msgId, length, mqttPayloadLimit, count);
        for (int i = 0; i < count; i++) {
            byte[] subArray = ArrayUtils.subarray(bytes, i * mqttPayloadLimit, mqttPayloadLimit * (i + 1));
            MqttMsg mqttMsg = new MqttMsg(msgId, subArray, true, i, count, crc);
            mqttMsg.setMsgId(msgId);
            mqttMsg.setTopic(topicId);
            mqttMsg.setPayloadType(PayloadType.STRING);
            mqttMsg.setOperaCode(operaCode);
            mqttMsg.setHasAck(hashAck);
            mqttMsg.setSourceAddress(preconditionGroupServerCode(iaConf.getGroupCode(),
                    preconditionServerCode(iaConf.getServerName(), iaConf.getServerVersion(), iaConf.getSequence())));
            result.add(mqttMsg);
        }
        return result;
    }

    /**
     * 回复通道
     *
     *  message
     */
    //@ServiceActivator(inputChannel = MqttConfig.CHANNEL_REPLY)
    @Override
    public void messageReceiver(Message<String> message) {
        Thread.startVirtualThread(() -> {
            try {
                String payload = message.getPayload();
                MsgRsp resp = JSONObject.parseObject(payload, MsgRsp.class);
                String msgId = resp.getMsgId();
                logger.debug("[{}]message ack is [{}]", msgId, payload);
                CallSubpackageMsg<MsgRsp> callBackTopic = CALLBACKS.get(msgId); //使用删除返回已经设置的callback对象
                if (callBackTopic != null) {
                    boolean subpackage = resp.isSubpackage();
                    if (subpackage)
                        callBackTopic.callbackSubPackage(resp);
                    else
                        callBackTopic.callback(resp);
                } else {
                    logger.warn("[{}]message[{}] ack find callback is null...", msgId, resp.getTopic());
                }
            } catch (Exception e) {
                logger.warn("message ack receive error[{}] is invalidation...", e.toString());
                e.printStackTrace();
            }
        });
        //        int activeCount = mqttSenderAckExecutor.getActiveCount();
        //        if (activeCount>=50 && activeCount % 5 == 0)
        //            logger.warn("mqtt ack executor pool active count is [{}]", activeCount);

    }

    @Override
    public Map<String, CallSubpackageMsg<MsgRsp>> getCALLBACKS() {
        return CALLBACKS;
    }
}

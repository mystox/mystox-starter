package tech.mystox.framework.kafka.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import tech.mystox.framework.common.util.KafkaUtils;
import tech.mystox.framework.common.util.MqttUtils;
import tech.mystox.framework.config.IaConf;
import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.core.MqttLogUtil;
import tech.mystox.framework.entity.*;
//import tech.mystox.framework.mqtt.config.MqttConfig;
//import tech.mystox.framework.mqtt.service.IMqttSender;
import tech.mystox.framework.kafka.config.KafkaConfig;
import tech.mystox.framework.kafka.service.KafkaSender;
import tech.mystox.framework.scheduler.RegScheduler;

import java.util.Map;
import java.util.concurrent.*;

import static tech.mystox.framework.common.util.MqttUtils.preconditionGroupServerCode;
import static tech.mystox.framework.common.util.MqttUtils.preconditionServerCode;

/**
 * Created by mystoxlol on 2019/8/19, 8:28.
 * company: mystox
 * description: 封装的mqtt生产者
 * update record:
 */
@Service("kafkaSenderImpl")
public class ChannelSenderImpl {


    @Autowired
    IaContext iaContext;

    Logger logger = LoggerFactory.getLogger(ChannelSenderImpl.class);

    protected static final Map<String, CallBackTopic> CALLBACKS = new ConcurrentHashMap<>();

    @Autowired
    private KafkaSender kafkaSender;

    @Value("${mqtt.callback.maxCount:10000}")
    private long callbackMaxCount;

    @Autowired
    private MqttLogUtil mqttLogUtil;
    @Autowired
    private ThreadPoolTaskExecutor mqSenderAckExecutor;


    public void sendToMqtt(String serverCode, String operaCode, String payload) {
        //组建topicid
        String topic = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
        //组建消息体
        MqttMsg mqttMsg = buildMqttMsg(topic, payload, operaCode);
        String msgId = mqttMsg.getMsgId();
        //获取目标topic列表，判断sub_list是否有人订阅处理
        // try {
            if (isExistsBySubList(serverCode, operaCode)) {
                // boolean existsByPubList = addPubList(serverCode, operaCode);
                // if (existsByPubList) {
                    logger.debug("[{}]message send...topic[{}], msg[{}]", msgId, topic, JSONObject.toJSONString(mqttMsg));
                    kafkaSender.sendToKafka(KafkaUtils.toKafkaTopic(topic), JSONObject.toJSONString(mqttMsg));
                // } else {
                //     mqttLogUtil.ERROR(msgId, StateCode.UNREGISTERED, operaCode, serverCode);
                //     logger.error("[{}]message send error[{}]... pub operaCode[{}.{}] is not exists...", msgId, StateCode.UNREGISTERED, serverCode, operaCode);
                // }
            } else {
                mqttLogUtil.ERROR(msgId, StateCode.UNREGISTERED, operaCode, serverCode);
                logger.error("[{}]message send error[{}] sub operaCode[{}.{}] is not exists...", msgId, StateCode.UNREGISTERED, serverCode, operaCode);
            }

        /*} catch (KeeperException e) {
            mqttLogUtil.ERROR(msgId, StateCode.UNREGISTERED, operaCode, serverCode);
            logger.error("[{}]message send error[{}]...[{}]", msgId, StateCode.UNREGISTERED, e.toString());
            e.printStackTrace();
        } catch (InterruptedException e) {
            mqttLogUtil.ERROR(msgId, StateCode.CONNECT_INTERRUPT, operaCode, serverCode);
            logger.error("[{}]message send error[{}]...[{}]", msgId, StateCode.CONNECT_INTERRUPT, e.toString());
            e.printStackTrace();
        }*/
    }



    public void sendToMqtt(String serverCode, String operaCode,
                           int qos, String payload)
    {
        //组建topicid
        String topic = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
        //组建消息体
        MqttMsg mqttMsg = buildMqttMsg(topic, payload, operaCode);
        String msgId = mqttMsg.getMsgId();
        // try {
            //获取目标topic列表，判断sub_list是否有人订阅处理
            if (isExistsBySubList(serverCode, operaCode)) {
                // boolean existsByPubList = addPubList(serverCode, operaCode);
                // if (existsByPubList) {
                    kafkaSender.sendToKafka(KafkaUtils.toKafkaTopic(topic), JSONObject.toJSONString(mqttMsg));
                // } else {
                //     mqttLogUtil.ERROR(msgId, StateCode.UNREGISTERED, operaCode, serverCode);
                //     logger.error("[{}]message send error[{}]... pub operaCode[{}.{}] is not exists...", msgId, StateCode.UNREGISTERED, serverCode, operaCode);
                // }
            } else {
                mqttLogUtil.ERROR(msgId, StateCode.UNREGISTERED, operaCode, serverCode);
                logger.error("[{}]message send error[{}] sub operaCode[{}.{}] is not exists...", msgId, StateCode.UNREGISTERED, serverCode, operaCode);
            }
      /*  } catch (KeeperException e) {
            mqttLogUtil.ERROR(msgId, StateCode.UNREGISTERED, operaCode, serverCode);
            logger.error("[{}]message send error[{}]...[{}]", msgId, StateCode.UNREGISTERED, e.toString());
            e.printStackTrace();*/
       /* } catch (InterruptedException e) {
            mqttLogUtil.ERROR(msgId, StateCode.CONNECT_INTERRUPT, operaCode, serverCode);
            logger.error("[{}]message send error[{}]...[{}]", msgId, StateCode.CONNECT_INTERRUPT, e.toString());
            e.printStackTrace();
        }*/
    }

    public boolean sendToMqttBoolean(String serverCode, String operaCode,
                                     int qos, MqttMsg mqttMsg)
    {
//        String localServerCode = this.serverName + "_" + this.serverVersion;
        String msgId = mqttMsg.getMsgId();
        try {
            //获取目标topic列表，判断sub_list是否有人订阅处理
            if (isExistsBySubList(serverCode, operaCode)) {
                // boolean existsByPubList = addPubList(serverCode, operaCode); //将此请求注册至请求列表，
                // if (existsByPubList) {
                    //组建topicid
                    String topic = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
                    mqttMsg.setOperaCode(operaCode);
                    String mqttMsgJson = JSONObject.toJSONString(mqttMsg);
                    logger.debug("[{}]message [{}] send...", msgId, mqttMsgJson);
                    kafkaSender.sendToKafka(KafkaUtils.toKafkaTopic(topic), mqttMsgJson);
                    return true;
                // } else {
                //     mqttLogUtil.ERROR(msgId, StateCode.UNREGISTERED, operaCode, serverCode);
                //     logger.error("[{}]message send error[{}]... pub operaCode[{}.{}] is not exists...", msgId, StateCode.UNREGISTERED, serverCode, operaCode);
                // }
            } else {
                mqttLogUtil.ERROR(msgId, StateCode.UNREGISTERED, operaCode, serverCode);
                logger.error("[{}]message send error[{}] sub operaCode[{}.{}] is not exists...", msgId, StateCode.UNREGISTERED, serverCode, operaCode);
                return false;
            }
       /* } catch (KeeperException e) {
            mqttLogUtil.ERROR(msgId, StateCode.UNREGISTERED, operaCode, serverCode);
            logger.error("[{}]message send error[{}]...[{}]", msgId, StateCode.UNREGISTERED, e.toString());
            if (logger.isDebugEnabled()) e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            mqttLogUtil.ERROR(msgId, StateCode.CONNECT_INTERRUPT, operaCode, serverCode);
            logger.error("[{}]message send error[{}]...[{}]", msgId, StateCode.CONNECT_INTERRUPT, e.toString());
            if (logger.isDebugEnabled()) e.printStackTrace();
            return false;*/
        } catch (MessagingException e) {
            mqttLogUtil.ERROR(msgId, StateCode.MESSAGE_EXCEPTION, operaCode, serverCode);
            logger.error("[{}]message send error[{}]...[{}]", msgId, StateCode.MESSAGE_EXCEPTION, e.toString());
            if (logger.isDebugEnabled()) e.printStackTrace();
            return false;
        }
        // mqttLogUtil.ERROR(msgId, StateCode.UNREGISTERED, operaCode, serverCode);
        // logger.error("[{}]message send error[{}]...", msgId, StateCode.UNREGISTERED);
        // return false;
    }


    public boolean sendToMqttBoolean(String serverCode, String operaCode, int qos, String payload) {
        //组建topicid
        String topic = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
        //组建消息体
        MqttMsg mqttMsg = buildMqttMsg(topic, payload, operaCode);
        return sendToMqttBoolean(serverCode, operaCode, qos, mqttMsg);
    }


    public MsgResult sendToMqttSync(String serverCode, String operaCode, int qos, String payload, long timeout, TimeUnit timeUnit) {
        String topic = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
        //组建消息体
        MqttMsg mqttMsg = buildMqttMsg(topic, payload, operaCode);
        mqttMsg.setHasAck(true);
        String msgId = mqttMsg.getMsgId();
        CallBackTopic callBackTopic = new CallBackTopic();
        int size = CALLBACKS.size();
        if (size > callbackMaxCount) {
            mqttLogUtil.ERROR(msgId, StateCode.CALLBACK_FULL, operaCode, serverCode);
            logger.error("[{}]message, system callback map is full[{}]", msgId);
            return new MsgResult(StateCode.CALLBACK_FULL, StateCode.StateCodeEnum.toStateCodeName(StateCode.CALLBACK_FULL));
        }
        ExecutorService es = Executors.newSingleThreadExecutor();
        CALLBACKS.put(msgId, callBackTopic);
        FutureTask<MqttResp> mqttMsgFutureTask = new FutureTask<>(callBackTopic);
        try {
            boolean sendResult = sendToMqttBoolean(serverCode, operaCode, qos, mqttMsg);
            if (sendResult) {
                es.submit(mqttMsgFutureTask);
                MqttResp resp = mqttMsgFutureTask.get(timeout, timeUnit);
                return new MsgResult(resp.getStateCode(), resp.getPayload());
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            mqttLogUtil.ERROR(msgId, StateCode.TIMEOUT, operaCode, serverCode);
            logger.error("[{}]message{},{}, request timeout: [{}]", msgId, serverCode, operaCode, e.toString());
            if (logger.isDebugEnabled()) e.printStackTrace();
            return new MsgResult(StateCode.TIMEOUT, e.toString());
        } catch (Exception e) {
            mqttLogUtil.ERROR(msgId, StateCode.FAILED, operaCode, serverCode);
            logger.error("[{}]message, request exception: [{}]", msgId, e.toString());
            if (logger.isDebugEnabled()) e.printStackTrace();
            return new MsgResult(StateCode.FAILED, e.toString());
        } finally {
            mqttMsgFutureTask.cancel(true);
            es.shutdown();
            CALLBACKS.remove(msgId);
        }
//        mqttLogUtil.ERROR(msgId, StateCode.FAILED, operaCode, serverCode);

        return new MsgResult(StateCode.FAILED, "request failed");
    }


    public MsgResult sendToMqttSync(String serverCode, String operaCode, String payload) {
        return sendToMqttSync(serverCode, operaCode, 2, payload, 30000L, TimeUnit.MILLISECONDS);
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

    private boolean isExistsBySubList(String serverCode, String operaCode) /*throws KeeperException, InterruptedException */{
        RegScheduler regScheduler=iaContext.getIaENV().getRegScheduler();
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


    private MqttMsg buildMqttMsg(String topicId, String payload, String operaCode) {
        MqttMsg mqttMsg = new MqttMsg();
        mqttMsg.setTopic(topicId);
        mqttMsg.setPayloadType(PayloadType.STRING);
        mqttMsg.setOperaCode(operaCode);
        mqttMsg.setPayload(payload);
        IaConf iaConf = iaContext.getIaENV().getConf();
        mqttMsg.setSourceAddress(preconditionGroupServerCode(iaConf.getGroupCode(),
                preconditionServerCode(iaConf.getServerName(), iaConf.getServerVersion(), iaConf.getSequence())));
        return mqttMsg;
    }
    /**
     * 回复通道
     *
     * @param message
     */
    @ServiceActivator(inputChannel = KafkaConfig.CHANNEL_REPLY)
    public void messageReceiver(Message<String> message) {
        mqSenderAckExecutor.execute(() -> {
            try {
                String payload = message.getPayload();
                MqttResp resp = JSONObject.parseObject(payload, MqttResp.class);
                String msgId = resp.getMsgId();
                logger.debug("[{}]message ack is [{}]", msgId, payload);
                CallBackTopic callBackTopic = CALLBACKS.get(msgId); //使用删除返回已经设置的callback对象
                if (callBackTopic != null) {
                    boolean subpackage = resp.isSubpackage();
                    if (subpackage)
                        callBackTopic.callbackSubPackage(resp);
                    else
                        callBackTopic.callback(resp);
                } else {
                    logger.warn("[{}]message ack is null...", msgId);
                }
            } catch (Exception e) {
                logger.warn("message ack receive error[{}] is Invalidation...", e.toString());
                e.printStackTrace();
            }
        });
//        int activeCount = mqttSenderAckExecutor.getActiveCount();
//        if (activeCount>=50 && activeCount % 5 == 0)
//            logger.warn("mqtt ack executor pool active count is [{}]", activeCount);

    }

    public Map<String, CallBackTopic> getCALLBACKS() {
        return CALLBACKS;
    }
}

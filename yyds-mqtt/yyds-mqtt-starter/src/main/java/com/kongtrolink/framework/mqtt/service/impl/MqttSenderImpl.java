package com.kongtrolink.framework.mqtt.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.common.util.MqttUtils;
import com.kongtrolink.framework.entity.*;
import com.kongtrolink.framework.mqtt.service.IMqttSender;
import com.kongtrolink.framework.register.service.ServiceRegistry;
import com.kongtrolink.framework.service.MqttHandler;
import com.kongtrolink.framework.service.MqttSender;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
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

    @Autowired
    private IMqttSender mqttSender;
    @Autowired
    MqttHandler mqttHandler;
    @Autowired
    @Qualifier("mqttHandlerAck")
    MqttHandler mqttHandlerAck;


    @Override
    public void sendToMqtt(String serverCode, String operaCode, String payload) {
        String localServerCode = this.serverName + "_" + this.serverVersion;
        boolean existsByPubList = isExistsByPubList(localServerCode, operaCode);
        if (existsByPubList) {
            //获取目标topic列表，判断sub_list是否有人订阅处理
            if (isExistsBySubList(serverCode, operaCode)) {
                //组建topicid
                String topic = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
                //组建消息体
                MqttMsg mqttMsg = buildMqttMsg(topic, localServerCode, payload);
                logger.info("message send...topic[{}]", topic, JSONObject.toJSONString(mqttMsg));
                mqttSender.sendToMqtt(topic, JSONObject.toJSONString(mqttMsg));
            }
        } else {
            logger.error("message send error[{}]...", StateCode.FAILED);
        }
    }


    @Override
    public void sendToMqtt(String serverCode, String operaCode,
                           int qos, String payload) {
        String localServerCode = this.serverName + "_" + this.serverVersion;
        boolean existsByPubList = isExistsByPubList(localServerCode, operaCode);
        if (existsByPubList) {
            //获取目标topic列表，判断sub_list是否有人订阅处理
            if (isExistsBySubList(serverCode, operaCode)) {
                //组建topicid
                String topic = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
                //组建消息体
                MqttMsg mqttMsg = buildMqttMsg(topic, localServerCode, payload);
                mqttSender.sendToMqtt(topic, qos, JSONObject.toJSONString(mqttMsg));
            }
        }
    }

    public boolean sendToMqtt(String serverCode, String operaCode,
                              int qos, MqttMsg mqttMsg) {
        String localServerCode = this.serverName + "_" + this.serverVersion;
        boolean existsByPubList = isExistsByPubList(localServerCode, operaCode);
        if (existsByPubList) {
            //获取目标topic列表，判断sub_list是否有人订阅处理
            if (isExistsBySubList(serverCode, operaCode)) {
                //组建topicid
                String topic = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
                String mqttMsgJson = JSONObject.toJSONString(mqttMsg);
                logger.debug("message [{}] send...", mqttMsgJson);
                mqttSender.sendToMqtt(topic, qos, mqttMsgJson);
                return true;
            }
            return false;
        }
        return false;
    }


    @Override
    public MsgResult sendToMqttSyn(String serverCode, String operaCode, int qos, String payload, long timeout, TimeUnit timeUnit) {
        String topic = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
        String localServerCode = this.serverName + "_" + this.serverVersion;
        //组建消息体
        String topicAck = topic + "/ack";
        if (!mqttHandlerAck.isExists(topicAck))
            mqttHandlerAck.addSubTopic(topicAck);
        //组建消息体
        MqttMsg mqttMsg = buildMqttMsg(topic, localServerCode, payload);
        boolean sendResult = sendToMqtt(serverCode, operaCode, qos, mqttMsg);
        if (sendResult) {
            ExecutorService es = Executors.newSingleThreadExecutor();
            CallBackTopic callBackTopic = new CallBackTopic();
            FutureTask<MqttResp> mqttMsgFutureTask = new FutureTask<>(callBackTopic);
            es.submit(mqttMsgFutureTask);
            CALLBACKS.put(mqttMsg.getMsgId(), callBackTopic);
            try {
                MqttResp resp = mqttMsgFutureTask.get(timeout, timeUnit);
                return new MsgResult(StateCode.SUCCESS, resp.getPayload());
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                logger.error("msgId: [{}], request timeout: [{}]", mqttMsg.getMsgId(), e.toString());
                return new MsgResult(StateCode.FAILED, e.toString());
            } finally {
                CALLBACKS.remove(mqttMsg.getMsgId());
            }
        }
        return new MsgResult(StateCode.FAILED, "请求失败");
    }

    @Override
    public MsgResult sendToMqttSyn(String serverCode, String operaCode, String payload) {
        return sendToMqttSyn(serverCode, operaCode, 2, payload, 30000L, TimeUnit.MILLISECONDS);
    }

    private boolean isExistsByPubList(String serverCode, String operaCode) {
        //todo
        //是否已经发布
        return true;
    }

    @Autowired
    ServiceRegistry serviceRegistry;

    private boolean isExistsBySubList(String serverCode, String operaCode) {
        try {
            if (OperaCode.SLOGIN.equals(operaCode) && serverCode.contains(ServerName.AUTH_PLATFORM)) {
                logger.warn("server Slogin to {} jump sublist judged...", serverCode);
                return true;
            }
            String topicId = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
            if (!serviceRegistry.exists(topicId)) {
                logger.error("topicId(nodePath) [{}] didn't registered...", topicId);
            }
            return true;
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }


    private MqttMsg buildMqttMsg(String topicId, String localServerCode, String payload) {
        MqttMsg mqttMsg = new MqttMsg();
        mqttMsg.setTopic(topicId);
        mqttMsg.setPayloadType(PayloadType.STRING);
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
        String payload = message.getPayload();
        MqttResp resp = JSONObject.parseObject(payload, MqttResp.class);
        String msgId = resp.getMsgId();
        logger.debug("message [{}] ack is [{}]", msgId, payload);
        CallBackTopic callBackTopic = CALLBACKS.get(msgId);
        if (callBackTopic != null) {
            callBackTopic.callback(resp);
        }
    }
}

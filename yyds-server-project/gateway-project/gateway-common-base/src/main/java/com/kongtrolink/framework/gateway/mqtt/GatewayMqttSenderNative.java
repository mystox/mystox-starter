package com.kongtrolink.framework.gateway.mqtt;

import com.kongtrolink.framework.entity.MqttResp;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.entity.StateCode;
import com.kongtrolink.framework.mqtt.service.impl.CallBackTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by mystoxlol on 2019/8/19, 8:28.
 * company: kongtrolink
 * description: 封装的mqtt生产者
 * update record:
 */
@Service
public class GatewayMqttSenderNative{

    private Logger logger = LoggerFactory.getLogger(GatewayMqttSenderNative.class);

    private static final Map<String, CallBackTopic> CALLBACKS = new ConcurrentHashMap<>();

    @Autowired
    private GatewayMqttSender gatewayMqttSender;

    public void sendToMqtt(String payload, String topic) {
        try {
            gatewayMqttSender.sendToMqtt(topic, payload);
        } catch (Exception e) {
            logger.error("message send topic:{} payload: {}", topic, payload);
            e.printStackTrace();
        }
    }

    public void sendToMqtt(String payload,String topic, int qos) {
        try {
            gatewayMqttSender.sendToMqtt(topic, qos,payload);
        } catch (Exception e) {
            logger.error("message send qos:{}  topic:{} payload: {}", qos,topic, payload);
            e.printStackTrace();
        }
    }


    private boolean sendToMqttBoolean(String payload,String topic,int qos) {
        try {
            gatewayMqttSender.sendToMqtt(topic, qos, payload);
            return true;
        } catch (Exception e) {
            logger.error("message send qos:{}  topic:{} payload: {}", qos,topic, payload);
            e.printStackTrace();
            return false;
        }
    }

    public MsgResult sendToMqttSyn(String msgId,String payload,String topic,int qos, long timeout, TimeUnit timeUnit) {

        boolean sendResult = sendToMqttBoolean(payload, topic, qos);
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
                logger.error("[{}]message, request timeout: [{}]", msgId, e.toString());
                return new MsgResult(StateCode.TIMEOUT, e.toString());
            } finally {
                CALLBACKS.remove(msgId);
            }
        }
        return new MsgResult(StateCode.FAILED, "请求失败");
    }

    public MsgResult sendToMqttSyn(String msgId,String payload,String topic) {
        return sendToMqttSyn(msgId, payload, topic,2, 30000L, TimeUnit.MILLISECONDS);
    }

}

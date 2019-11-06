package com.kongtrolink.framework.gateway.mqtt;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.MqttResp;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.entity.StateCode;
import com.kongtrolink.framework.mqtt.service.impl.CallBackTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

import static com.kongtrolink.framework.gateway.mqtt.GatewayMqttConfig.CHANNEL_REPLY;


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
    @Autowired
    ThreadPoolTaskExecutor mqttExecutor;

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
            logger.info("put msgId:{}" ,msgId );
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

    /**
     * 回复通道
     *
     * @param message
     */
    @ServiceActivator(inputChannel = CHANNEL_REPLY)
    public void messageReceiver(Message<String> message) {
        mqttExecutor.execute(()->{
            try {
                String payload = message.getPayload();
                MqttResp resp = JSONObject.parseObject(payload, MqttResp.class);
                String msgId = resp.getMsgId();
                logger.debug("[{}] message  ack is [{}]", msgId, payload);
                CallBackTopic callBackTopic = CALLBACKS.get(msgId);
                if (callBackTopic != null) {
                    boolean subpackage = resp.isSubpackage();
                    if (subpackage)
                        callBackTopic.callbackSubPackage(resp);
                    else
                        callBackTopic.callback(resp);
                } else {
                    logger.warn("[{}] message ack [{}] is Invalidation...", msgId);
                }
            } catch (Exception e) {
                logger.warn("message ack receive error[{}] is Invalidation...",e.toString());
                e.printStackTrace();
            }
        });
    }
}

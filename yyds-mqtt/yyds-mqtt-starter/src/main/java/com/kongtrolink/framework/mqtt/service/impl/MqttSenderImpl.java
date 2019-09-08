package com.kongtrolink.framework.mqtt.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.common.util.MqttUtils;
import com.kongtrolink.framework.entity.MqttMsg;
import com.kongtrolink.framework.entity.MqttResp;
import com.kongtrolink.framework.entity.PayloadType;
import com.kongtrolink.framework.entity.StateCode;
import com.kongtrolink.framework.mqtt.service.IMqttSender;
import com.kongtrolink.framework.mqtt.service.MqttSender;
import com.kongtrolink.framework.register.service.ServiceRegistry;
import com.kongtrolink.framework.service.MqttHandler;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
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

    public void sendToMqtt(String serverCode, String operaCode,
                           int qos, MqttMsg mqttMsg) {
        String localServerCode = this.serverName + "_" + this.serverVersion;
        boolean existsByPubList = isExistsByPubList(localServerCode, operaCode);
        if (existsByPubList) {
            //获取目标topic列表，判断sub_list是否有人订阅处理
            if (isExistsBySubList(serverCode, operaCode)) {
                //组建topicid
                String topic = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
                mqttSender.sendToMqtt(topic, qos, JSONObject.toJSONString(mqttMsg));
            }
        }
    }

    @Autowired
    MqttHandler mqttHandler;
    @Autowired
    @Qualifier("mqttHandlerAck")
    MqttHandler mqttHandlerAck;

//    @Autowired
//    CallBackTopic callBackTopic;
    @Override
    public String sendToMqttSyn(String serverCode, String operaCode, int qos, String payload) {

        String topic = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
        String localServerCode = this.serverName + "_" + this.serverVersion;
        //组建消息体
        String topicAck = topic + "/ack";
        if (!mqttHandlerAck.isExists(topicAck))
            mqttHandlerAck.addSubTopic(topicAck);
//        messagingTemplate.sendAndReceive()
        String s2 = mqttHandlerAck.toString();

//        String topic = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
        //组建消息体
        MqttMsg mqttMsg = buildMqttMsg(topic, localServerCode, payload);
        sendToMqtt(serverCode, operaCode, qos, mqttMsg);

        ExecutorService es = Executors.newSingleThreadExecutor();
//        CallBackTopic callBackTopic = new CallBackTopic(topic);
        CallBackTopic callBackTopic = new CallBackTopic();
        FutureTask<MqttResp> mqttMsgFutureTask = new FutureTask<>(callBackTopic);
        es.submit(mqttMsgFutureTask);
        CALLBACKS.put(mqttMsg.getMsgId(), callBackTopic);
        try {
            MqttResp resp = mqttMsgFutureTask.get(10000, TimeUnit.MILLISECONDS);
            System.out.println("收到回调结果" + JSONObject.toJSONString(resp));
            return resp.getPayload();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {

        }
        return "";
    }

    /*@ServiceActivator(inputChannel = CHANNEL_REPLY)
    public void messageReceiver1(Message<?> message) {
        System.out.println("messageReceiver1");
        System.out.println(message.toString());
//        result = message.getPayload().toString();
    }
    @ServiceActivator(inputChannel = CHANNEL_REPLY)
    public void messageReceiver2(Message<?> message) {
        System.out.println("messageReceiver2");
        System.out.println(message.toString());
//        result = message.getPayload().toString();
    }*/
//    @ServiceActivator(inputChannel = MqttConfig.CHANNEL_NAME_OUT)
//    public void replyReceiver(Message<?> message) {
//        System.out.println("收到回复"+message);
//
//    }
    @Autowired
    MqttPahoMessageHandler messageHandler;

    @Override
    public String sendToMqttSyn2(String serverCode, String operaCode, int qos, String payload) {
        String topic = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
        String localServerCode = this.serverName + "_" + this.serverVersion;
        //组建消息体
        MqttMsg mqttMsg = buildMqttMsg(topic, localServerCode, payload);
        return "";
    }

    private boolean isExistsByPubList(String serverCode, String operaCode) {
        //todo
        //是否已经发布
        return true;

    }

    @Autowired
    ServiceRegistry serviceRegistry;

    private boolean isExistsBySubList(String serverCode, String operaCode) {
        String topicId = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
        try {
            if (serviceRegistry.exists(topicId)) {
                return true;
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.error("topicId(nodePath) [{}] didn't registered...", topicId);
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

    @ServiceActivator(inputChannel = CHANNEL_REPLY)
    public void messageReceiver(Message<String> message) {
        System.out.println("CallBackTopic");
        System.out.println(message.toString());
        String payload = message.getPayload();
        MqttResp resp = JSONObject.parseObject(payload,MqttResp.class);
        String msgId = resp.getMsgId();
        CallBackTopic callBackTopic = CALLBACKS.get(msgId);
        if (callBackTopic!=null)
            callBackTopic.callback(resp);
    }
}

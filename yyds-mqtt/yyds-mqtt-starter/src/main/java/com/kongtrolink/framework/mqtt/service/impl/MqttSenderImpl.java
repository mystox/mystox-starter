package com.kongtrolink.framework.mqtt.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.common.util.MqttUtils;
import com.kongtrolink.framework.entity.MqttMsg;
import com.kongtrolink.framework.entity.PayloadType;
import com.kongtrolink.framework.entity.StateCode;
import com.kongtrolink.framework.mqtt.config.MqttConfig;
import com.kongtrolink.framework.mqtt.service.IMqttSender;
import com.kongtrolink.framework.mqtt.service.MqttSender;
import com.kongtrolink.framework.register.service.ServiceRegistry;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by mystoxlol on 2019/8/19, 8:28.
 * company: kongtrolink
 * description: 封装的mqtt生产者
 * update record:
 */
@Service
public class MqttSenderImpl implements MqttSender {

    Logger logger = LoggerFactory.getLogger(MqttSenderImpl.class);
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
                String topic = MqttUtils.preconditionSubTopicId(localServerCode, operaCode);
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

    @Autowired
            @Qualifier("replyProducer")
    MessageProducer reply;

    @Override
    public String sendToMqttSyn(String serverCode, String operaCode, int qos, String payload) {




        /*String topic = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
        sendToMqtt(serverCode, operaCode, qos, payload);
        ExecutorService es = Executors.newSingleThreadExecutor();
        CallBackTopic callBackTopic = new CallBackTopic(topic,reply);
        Future<MqttMsg> mqttMsgFuture = es.submit(callBackTopic);
        try {
            MqttMsg mqttMsg = mqttMsgFuture.get(10000, TimeUnit.MILLISECONDS);
            System.out.println(mqttMsg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }*/

        String topic = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
        String localServerCode = this.serverName + "_" + this.serverVersion;
        //组建消息体
        MqttMsg mqttMsg = buildMqttMsg(topic, localServerCode, payload);
        Future<?> stringFuture = mqttSender.sendToMqttSyn(topic, qos, JSONObject.toJSONString(mqttMsg));
        String s = null;
        try {
            Object o = stringFuture.get();
            System.out.println(o);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println(s);
        return "";
    }
    @ServiceActivator(inputChannel = MqttConfig.CHANNEL_REPLY)
    public void replyReceiver(Message<?> message) {
        System.out.println("收到回复"+message);

    }
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
            if(serviceRegistry.exists(topicId)){
                return true;
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.error("topicId(nodePath) [{}] didn't registered...",topicId);
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




}

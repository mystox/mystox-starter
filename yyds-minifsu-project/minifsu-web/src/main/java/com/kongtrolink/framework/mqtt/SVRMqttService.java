/**
 * *****************************************************
 * Copyright (C) Kongtrolink techology Co.ltd - All Rights Reserved
 *
 * This file is part of Kongtrolink techology Co.Ltd property.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************
 */
package com.kongtrolink.framework.mqtt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.mqtt.base.AbstractMqttService;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mosaico
 */
@Service("sVRMqttService")
public class SVRMqttService extends AbstractMqttService {



    @Value("${mqtt.broker.host}") private String broker;
    @Value("${mqtt.sub.ywcl.topicId}") private String clientSE;
    @Value("${mqtt.client.appServer.id}") private String clientSC;
    @Value("${mqtt.client.appServer.topic}") private String scTopic;

    private static final Logger LOGGER = LoggerFactory.getLogger(SVRMqttService.class);



    /**
     * MQTT 服务初始化
     * 在 SVRMqttModelService 中 进行初始化
     */
    @PostConstruct
    public void init() throws Exception{
//        clientSC = "app_server_develop_01";
//        clientSE = "111";
        String[] topics = getTopic();
        if(StringUtils.isBlank(broker) || StringUtils.isBlank(clientSC)){
            throw new Exception("MQTT错误，服务器或客户端ID异常,broker："+ broker +" clientId:"+clientSC);
        }
        super.initTopicFix(clientSC,clientSE);//下发报文的前后缀
        super.start(broker, clientSC, topics);//启动 mqtt 注册
    }

    /**
     * MQTT 客户机订阅主题，并设置报文到达时的回调方法
     * * 新增一个MQTT报文 - 直接与FSU交互 获取 VPN ID
     * "EnterpriseFSUCode/RspVPNIPSyncSVR/ScloudID_product_01"
     * mark:20171019(相关修改 都注释这个)
     * 返回:
     * {
     reqId："" ,//随机ID，Scloud-web 向FSU请求vp ip以及端口报文的随机ID ，原样返回
     FsuCode："",
     ip："",//FSU的VPN IP
     http端口：8088,//
     onvif端口: 554 //
     }
     */
    private String[] getTopic(){
        MqttRecvTopic[] recvTopics = MqttRecvTopic.values();
        List<String> list = new ArrayList<>();
        /*for (int i = 0; i < recvTopics.length; i++) {
            String topicStr = recvTopics[i].getTopicString();
            if(topicStr.equals(MqttRecvTopic.REPORT_ALARM_END.getTopicString())
                    ||topicStr.equals(MqttRecvTopic.REPORT_FSU_LOGOUT_ALARM.getTopicString())){
                continue;//取出 已经单独拿出去的 topic
            }
            StringBuilder builder = new StringBuilder();
            if("RspVPNIPSyncSVR".equals(topicStr)){//新增订阅的报文
                builder.append("EnterpriseFSUCode").append("/")
                        .append(topicStr)
                        .append("/").append(clientSC);
            }else{
                builder.append(clientSE).append("/")
                        .append(topicStr)
                        .append("/").append(clientSC);
            }
            list.add(builder.toString());
        }*/
            list.add(scTopic +"/"+ clientSC);
        String[] topics = new String[list.size()];
        for(int i =0;i<list.size();i++){
            topics[i] = list.get(i);
        }
        return topics;
    }

    /**
     * MQTT 消息处理方法体
     * @param topic 收到的主题
     * @param message 收到的消息体
     * @throws Exception
     */
    @Override
    public  void  setMessageArrived(String topic, MqttMessage message) throws Exception{
        try{
            String jsonStr = new String(message.getPayload(), "UTF-8");
            LOGGER.info("[MQTT] Message arrived. topic: {} message: {}", topic, jsonStr);
            final JSONObject json = JSON.parseObject(jsonStr);
            if (topic.contains(MqttRecvTopic.REPORT_ALARM.getTopicString())) {
                /*LOGGER.info(">>>>>>>>>>>>>>>>>>>[MQTT]实时告警数据处理>>>>>>>>");
                taskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        // 告警上报报文
                        String alarmStr = json.getString("alarmId");
                        List<String> alarmIds = JSONObject.parseArray(alarmStr, String.class);
                        String uniqueCode = json.getString("uniqueCode");
                        alarmMqttService.reportAlarm(uniqueCode, alarmIds);
                    }
                });*/
            }else {
//                LOGGER.info("MQTT 未匹配到报文类型");
                // 发送报文为空时，处理空指针异常
                if (json == null) {
                    LOGGER.warn("[MQTT] Result that massage parsed to Json is null!");
                    return ;
                }
                String requestId = json.getString("reqId");
                MessageArrivedCallback cb = CALLBACKS.get(requestId);
                if (cb == null) {
                    LOGGER.warn("[MQTT] Callback not found. reqId: {}", requestId);
                } else {
                    cb.onMessage(jsonStr);
                    CALLBACKS.remove(requestId);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 发送声光告警报文
     * @param uniqueCode 告警所在的企业
     * @param avAlarmMsg 告警信息
     * @return 发送成功/失败
     */
    public boolean sendAVAlarm(String uniqueCode, String avAlarmMsg) {
        String topic = "/" + uniqueCode + "/AVAlarm";
        return super.contentSendMessage(topic,avAlarmMsg);
    }

}
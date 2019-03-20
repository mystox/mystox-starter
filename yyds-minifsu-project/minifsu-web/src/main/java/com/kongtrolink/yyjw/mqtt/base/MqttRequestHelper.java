package com.kongtrolink.yyjw.mqtt.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.yyjw.exception.CommunicateException;
import com.kongtrolink.yyjw.mqtt.MessageArrivedCallback;
import com.kongtrolink.yyjw.mqtt.MqttPubTopic;
import com.kongtrolink.yyjw.mqtt.message.MqttStandardMessage;
import com.kongtrolink.yyjw.mqtt.message.YwclMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.alibaba.fastjson.JSON.parseObject;

/**
 * 同步等待 MQTT返回消息 处理类
 * @author Mag
 */
@Component
public class MqttRequestHelper {

    private   int timeOut = 10 * 1000; //默认时长 单位 毫秒

    @Autowired
    @Qualifier(value = "sVRMqttService")
    private  AbstractMqttService mqttService;
    @Value("${mqtt.client.appServer.id}") private String clientSC;
    @Value("${mqtt.client.appServer.topic}") private String scTopic;

    private static final Logger LOGGER = LoggerFactory.getLogger(MqttRequestHelper.class);

    /**
     * 发送 MQTT 报文，并异步等待响应
     *
     * @param message 报文内容
     * @param topic 主题
     * @param time 时间限制
     * @return 响应状态码
     */
    private String syncRequest(MqttStandardMessage message, String topic, int time) {
        ((YwclMessage) message).setResp_topic(scTopic +"/"+ clientSC);
        long t1 = System.currentTimeMillis();
        final StringBuffer result = new StringBuffer("");	// 获取回调方法中的状态码
        final Thread currentThread = Thread.currentThread();

//        // 生成唯一值 requestId
//        StringBuilder builder = new StringBuilder();
//        builder.append(System.currentTimeMillis()).append("-")
//                .append(currentThread.getName()).append("-")
//                .append((int) (Math.random() * 10000));
        String requestId = UUID.randomUUID().toString().replaceAll("-","");
        mqttService.sendMessage(message,topic, new MessageArrivedCallback(requestId) {
            @Override
            public void onMessage(String message) throws Exception {
                result.setLength(0);//清空buffer内容
                result.append(message);
                synchronized (currentThread) {
                    currentThread.notify();
                }
            }
        });
        try {
            synchronized (currentThread) {
                currentThread.wait(time);
            }
        } catch (InterruptedException ex) {
            LOGGER.error(ex.getMessage());
        }

        long t2 = System.currentTimeMillis() - t1;

        if (t2 > time) {
            // 超时日志
            LOGGER.error("mqtt 超时异常({} ms),限定时: {}, 发送消息: {}", t2, time, JSON.toJSONString(message, true));
            return null;
        } else {
            LOGGER.info(" mqtt returned in {} ms, ReqId: {}", t2, requestId);
        }
        return result.toString();
    }

    /**
     * 发送 MQTT 报文，并异步等待响应
     *
     * @param message 报文内容
     * @param topic 主题
     * @param time 时间限制
     * @return 响应状态码
     */
    private String syncRequest(MqttStandardMessage message, MqttPubTopic topic, int time) {
        String publishTopic = mqttService.getPushTopic(topic);//组装下发topic的 前后缀
        return syncRequest(message,publishTopic,time);
    }

    /**
     * 发送 MQTT 报文，并异步等待响应数据
     * @param <T> 响应数据类型
     * @param message 报文内容
     * @param topic 主题
     * @param clazz 响应数据类型
     * @return 响应数据
     */
    public <T> T syncRequestData(MqttStandardMessage message, String topic, Class<T> clazz,int time) {
        try{
            String value = syncRequest(message,topic,time);
            if(value ==null){
                return null;
            }
            T data = JSON.parseObject(value, clazz);
            return  data;
        }catch (Exception e){
            LOGGER.error("message 解析报错 {}");
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 发送 MQTT 报文，并异步等待响应数据
     * @param <T> 响应数据类型
     * @param message 报文内容
     * @param topic 主题
     * @param clazz 响应数据类型
     * @return 响应数据
     */
    public <T> T syncRequest(MqttStandardMessage message, String topic, Class<T> clazz,int time) {
        try{
            String value = syncRequest(message,topic,time);
            if(value ==null){
                return null;
            }
            T data = JSON.parseObject(value, clazz);
            return  data;
        }catch (Exception e){
            LOGGER.error("message 解析报错 {}");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 发送 MQTT 报文，并异步等待响应数据
     * @param <T> 响应数据类型
     * @param message 报文内容
     * @param topic 主题
     * @param clazz 响应数据类型
     * @return 响应数据
     */
    public <T> T syncRequestData(MqttStandardMessage message,  MqttPubTopic topic, Class<T> clazz,int time) {
        String publishTopic = mqttService.getPushTopic(topic); //组装下发topic的 前后缀
        return syncRequestData(message,publishTopic,clazz,time);
    }

    /**
     * 兼容 老版本的 消息格式以及返回
     * @param message 消息体
     * @param topic 消息报文
     * @return 消息返回提
     */
    public  boolean syncRequestDataInt(MqttStandardMessage message, String topic) throws CommunicateException {
        String value = syncRequest(message,topic,timeOut);
        if(value == null){
            return false;
        }
        JSONObject json = parseObject(value);
        if (json.get("message").equals("SUCCESS")) {
            try {
                return true;
            } catch (RuntimeException e) {
                return false;
            }
        } else if (json.get("message").equals("COMMUNICATION_ERROR")) {
            throw new CommunicateException();
        } else {
            LOGGER.error("Callback information: FAILED!");
            return false;
        }

    }

    /**
     * 兼容 老版本的 消息格式以及返回
     * @param message 消息体
     * @param topic 消息报文
     * @return 消息返回提
     */
    public  boolean syncRequestDataInt(MqttStandardMessage message, MqttPubTopic topic) throws CommunicateException {
        String publishTopic = mqttService.getPushTopic(topic); //组装下发topic的 前后缀
        return syncRequestDataInt(message,publishTopic);


    }

    public <T> T syncRequestData(MqttStandardMessage message, String topicType, Class<T> clazz) {
        return syncRequestData(message,topicType,clazz,timeOut);
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public AbstractMqttService getMqttService() {
        return mqttService;
    }

    public void setMqttService(AbstractMqttService mqttService) {
        this.mqttService = mqttService;
    }

    public JSONObject sendContext(String topicId, String requestBody)
    {
        boolean result = mqttService.contentSendMessage(topicId, requestBody);
        LOGGER.info("下发结果:{}" , result);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", result);
        return jsonObject;
    }
}

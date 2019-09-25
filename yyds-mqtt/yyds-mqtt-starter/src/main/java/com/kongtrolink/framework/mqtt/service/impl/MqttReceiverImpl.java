package com.kongtrolink.framework.mqtt.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.common.util.MqttUtils;
import com.kongtrolink.framework.entity.*;
import com.kongtrolink.framework.mqtt.service.IMqttSender;
import com.kongtrolink.framework.mqtt.service.MqttReceiver;
import com.kongtrolink.framework.mqtt.util.SpringContextUtil;
import com.kongtrolink.framework.register.service.ServiceRegistry;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;

import javax.annotation.Resource;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.kongtrolink.framework.mqtt.config.MqttConfig.CHANNEL_NAME_IN;

/**
 * Created by mystoxlol on 2019/8/13, 11:05.
 * company: kongtrolink
 * description:
 * update record:
 */
@MessageEndpoint
//@PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:jarRes.yml")
public class MqttReceiverImpl implements MqttReceiver {
    private static final Logger logger = LoggerFactory.getLogger(MqttReceiverImpl.class);

    private final static int MQTT_PAYLOAD_LIMIT = 47 * 1024; //消息体（byte payload）最长大小
    @Value("${jarResources.path:./jarResources}")
    private String jarPath;

    /**
     * 注入发送MQTT的Bean
     */
    @Resource
    private IMqttSender iMqttSender;

    @Autowired
    private ServiceRegistry serviceRegistry;

    @Override
    public MqttResp receive(String topic, MqttMsg payload) {
        logger.info("receive... ..." + JSONObject.toJSONString(payload));
        String unit = getUnitBySubList(topic);
        MqttResp result = null;
        try {
            if (unit.startsWith(UnitHead.LOCAL)) { //执行本地函数和方法
                result = localExecute(unit, payload);
            } else if (unit.startsWith(UnitHead.JAR)) {//亦可执行本地和远程的jar，远程可执行jar以仓库的方式开放。
                result = jarExecute(unit, payload);
            } else if (unit.startsWith(UnitHead.HTTP)) {
                //todo 执行远程的http服务器
            }
        } catch (Exception e) {
            logger.error("msg execute error: [{}]", payload.getMsgId(), e.toString());
            result = new MqttResp(payload.getMsgId(), e.toString());
            result.setStateCode(StateCode.FAILED);
            e.printStackTrace();
        }
       /* String topic_ack = getAcyBySubList();
        if (StringUtils.isNotBlank(topic_ack))
            iMqttSender.sendToMqtt(topic_ack, 2, result);*/
        return result;
    }

    MqttResp localExecute(String unit, MqttMsg mqttMsg) {
        String[] entity = unit.replace(UnitHead.LOCAL, "").split("/");
        String className = entity[0];
        String methodName = entity[1];
        String result = "";
        try {
            Class<?> clazz = Class.forName(className);
            Object bean = SpringContextUtil.getBean(clazz);
            Method method = clazz.getDeclaredMethod(methodName, String.class);
            Object invoke = method.invoke(bean, mqttMsg.getPayload());
            result = invoke instanceof String ? (String) invoke : JSONObject.toJSONString(invoke);
            MqttResp resp = new MqttResp(mqttMsg.getMsgId(), result);
//            logger.info("local result: {}", result);
            return resp;
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    MqttResp jarExecute(String unit, MqttMsg mqttMsg) {
        String[] entity = unit.replace(UnitHead.JAR, "").split("/");
        String jarName = entity[0];
        String className = entity[1];
        String methodName = entity[2];
        String url = jarPath + "/" + jarName;
        File file = new File(url);
        String result = "";
        try {
            URL fileUrl = file.toURI().toURL();
            ClassLoader classLoader = new URLClassLoader(new URL[]{fileUrl});
            Thread.currentThread().setContextClassLoader(classLoader);
            Class<?> clazz = classLoader.loadClass(className);// 使用loadClass方法加载class,这个class是在urls参数指定的classpath下边。
            Method taskMethod = clazz.getMethod(methodName, String.class);
            Object invoke = taskMethod.invoke(clazz.newInstance(), mqttMsg.getPayload());
            result = invoke instanceof String ? (String) invoke : JSONObject.toJSONString(invoke);
            MqttResp resp = new MqttResp(mqttMsg.getMsgId(), result);
            logger.info("jar result: {}", result);
            return resp;
        } catch (MalformedURLException | InstantiationException | IllegalAccessException
                | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUnitBySubList(String topic) {

//        return SpringContextUtil.getServiceMap().get(topic);
        //todo
        try {
            String data = serviceRegistry.getData(topic);
            if (StringUtils.isNotBlank(data)) {
                RegisterSub sub = JSONObject.parseObject(data, RegisterSub.class);
                if (sub != null)
                    return sub.getExecuteUnit();
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "";

        /*Map<String, String> serviceMap = SpringContextUtil.getServiceMap();
        String s = serviceMap.get(topic);
        return s;*/
        //注册中心获取topic对应的jar单元
        /*if ("YYTD_MQTT_DEMO_1.0.0/sayHello".equals(topic))
            return "jar://mystoxUtil.jar/tech.mystox.test.util.MystoxUtil/sayHello";
        else {
            return "local://com.kongtrolink.framework.api.LocalService/hello";
        }*/
    }


    public String getAcyBySubList() {
        return "topic_ack";
    }


    @ServiceActivator(inputChannel = CHANNEL_NAME_IN/*, outputChannel = CHANNEL_NAME_OUT*/)
    public void messageReceiver(Message<String> message) {
        //至少送达一次存在重复发送的几率，所以订阅服务需要判断消息订阅的幂等性,幂等性可以通过消息属性判断是否重复发送
        Boolean mqtt_duplicate = (Boolean) message.getHeaders().get("mqtt_duplicate");
        if (mqtt_duplicate) {
            logger.warn("message receive duplicate [{}]", message);
        }
        String topic = message.getHeaders().get("mqtt_topic").toString();
        String payload = message.getPayload();
        MqttMsg mqttMsg = JSONObject.parseObject(payload, MqttMsg.class);
        MqttResp result = receive(topic, mqttMsg);
        String ackTopic = MqttUtils.preconditionSubTopicId(mqttMsg.getSourceAddress(), mqttMsg.getOperaCode()) + "/ack";
        result.setTopic(ackTopic);
        String ackPayload = JSONObject.toJSONString(result);
        logger.info("[{}] message execute result: [{}]", mqttMsg.getMsgId(), ackPayload);

        int length = ackPayload.getBytes(Charset.forName("utf-8")).length;
        if (length > MQTT_PAYLOAD_LIMIT) {
            //分包
            List<MqttResp> resultArr = subpackage(result);
            logger.info("[{}] message subpackage, package count:[{}]", mqttMsg.getMsgId(), resultArr.size());
            int size = resultArr.size();
            for (int i = 0; i < size; i++) {
                MqttResp resp = resultArr.get(i);
                iMqttSender.sendToMqtt(ackTopic, 2, JSONObject.toJSONString(resp));
            }
//            MqttResp resp = resultArr.get(size - 1);
//            result.setSubpackage(true);
            //最后一包直接返回
//            return MessageBuilder
//                    .withPayload(JSONObject.toJSONString(resp))
//                    .setHeader(MqttHeaders.TOPIC, ackTopic)
//                    .setHeader(MqttHeaders.QOS, 2)
//                    .build();

        } else
            iMqttSender.sendToMqtt(ackTopic, 2, ackPayload);
    }

    /**
     * 分包
     *
     * @param ackPayload
     * @return
     */
    private List<MqttResp> subpackage(MqttResp ackPayload) {
        List<MqttResp> result = new ArrayList<>();
        String payload = ackPayload.getPayload();
        byte[] bytes = payload.getBytes(Charset.forName("utf-8"));
        int length = bytes.length;
        int count = length / MQTT_PAYLOAD_LIMIT + 1;
        for (int i = 0; i < count; i++) {
            byte[] subarray = ArrayUtils.subarray(bytes, i * MQTT_PAYLOAD_LIMIT, MQTT_PAYLOAD_LIMIT * (i + 1));
            MqttResp resp = new MqttResp(ackPayload.getMsgId(), subarray, true, i, count);
            result.add(resp);
        }
        return result;
    }

}
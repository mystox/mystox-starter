package tech.mystox.framework.mqtt.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import tech.mystox.framework.common.util.ByteUtil;
import tech.mystox.framework.common.util.MqttUtils;
import tech.mystox.framework.common.util.SpringContextUtil;
import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.core.MqttLogUtil;
import tech.mystox.framework.entity.*;
import tech.mystox.framework.mqtt.config.MqttConfig;
import tech.mystox.framework.mqtt.service.IMqttSender;
import tech.mystox.framework.scheduler.RegScheduler;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by mystoxlol on 2019/8/13, 11:05.
 * company: mystox
 * description:
 * update record:
 */
@MessageEndpoint
public class MqttReceiver {
    private static final Logger logger = LoggerFactory.getLogger(MqttReceiver.class);
    private final static int MQTT_PAYLOAD_LIMIT = 47 * 1024; //消息体（byte payload）最长大小
    @Value("${jarResources.path:./jarResources}")
    private String jarPath;
    @Value("${server.name}_${server.version}")
    private String serverCode;

    @Autowired
    IaContext iaContext;
    /**
     * 注入发送MQTT的Bean
     */
    @Resource
    private IMqttSender iMqttSender;
    @Autowired
    ThreadPoolTaskExecutor mqttExecutor;
    @Autowired
    private MqttLogUtil mqttLogUtil;

    public MqttResp receive(String topic, MqttMsg mqttMsg) {
        logger.debug("receive... ..." + JSONObject.toJSONString(mqttMsg));

        String unit = getUnitBySubList(topic);
        MqttResp result = null;
        try {
            if (unit.startsWith(UnitHead.LOCAL)) { //执行本地函数和方法
                result = localExecute(unit, mqttMsg);
            } else if (unit.startsWith(UnitHead.JAR)) {//亦可执行本地和远程的jar，远程可执行jar以仓库的方式开放。
                result = jarExecute(unit, mqttMsg);
            } else if (unit.startsWith(UnitHead.HTTP)) {
                //todo 执行远程的http服务器
            }
        } catch (Exception e) {
            mqttLogUtil.ERROR(result.getMsgId(), StateCode.EXCEPTION, mqttMsg.getOperaCode(), mqttMsg.getSourceAddress());
            logger.error(" [{}] msg execute error: [{}]", mqttMsg.getMsgId(), e.toString());
            result = new MqttResp(mqttMsg.getMsgId(), e.toString());
            result.setStateCode(StateCode.FAILED);
            e.printStackTrace();
        }
        logger.debug("[{}] message execute result: [{}]", mqttMsg.getMsgId(), JSONObject.toJSONString(result));
        return result;
    }

    MqttResp localExecute(String unit, MqttMsg mqttMsg) {
        String[] entity = unit.replace(UnitHead.LOCAL, "").split("/");
        String className = entity[0];
        String methodName = entity[1];
        String paramsTypeStr = "";
        if (entity.length > 2)
            paramsTypeStr = entity[2];
        else
            paramsTypeStr = "[\"java.lang.String\"]";

        String result = "";
        MqttResp resp;
        try {
            Class<?> clazz = Class.forName(className);
            Object bean = SpringContextUtil.getBean(clazz);//这里会是性能瓶颈
            List<Class> classes = JSON.parseArray(paramsTypeStr, Class.class);
            Method method = clazz.getDeclaredMethod(methodName, classes.toArray(new Class[0]));
            String payload = mqttMsg.getPayload();
            JSONArray jsonArray = JSONObject.parseArray(payload);
            Object invoke = method.invoke(bean, jsonArray.toArray());
            result = invoke instanceof String ? (String) invoke : JSON.toJSONString(invoke);
            resp = new MqttResp(mqttMsg.getMsgId(), result);
            return resp;
        } catch (Exception e) {
            mqttLogUtil.ERROR(mqttMsg.getMsgId(), StateCode.EXCEPTION, mqttMsg.getOperaCode(), serverCode);
            logger.error("[{}]local execute exception:{}", mqttMsg.getMsgId(), e.toString());
            resp = new MqttResp(mqttMsg.getMsgId(), e.toString());
            resp.setStateCode(StateCode.FAILED);
            e.printStackTrace();
        }
        return resp;
    }
    // private Object deserialize(String msg) {
    //     if (String.class == returnType) return msg;
    //     Object parse = JSON.parse(msg);
    //     if (parse instanceof JSONObject) {
    //         return ((JSONObject) parse).toJavaObject(returnType);
    //     } else if (parse instanceof JSONArray) {
    //         if (returnType == List.class) {
    //             Type type = ((ParameterizedType) returnType).getActualTypeArguments()[0];
    //             return ((JSONArray) parse).toJavaList(type.getClass());
    //         } else if (returnType == Map.class) {
    //             return parse;
    //         } else if (returnType == Set.class) {
    //             Type type = ((ParameterizedType) returnType).getActualTypeArguments()[0];
    //             return ((JSONArray) parse).toJavaList(type.getClass());
    //         }
    //     } else if (returnType.getClass().isInstance(parse)) {
    //         return parse;
    //     }
    //     return parse;
    // }

    MqttResp jarExecute(String unit, MqttMsg mqttMsg) {
        String[] entity = unit.replace(UnitHead.JAR, "").split("/");
        String jarName = entity[0];
        String className = entity[1];
        String methodName = entity[2];
        String paramsTypeStr = "";
        if (entity.length > 3)
            paramsTypeStr = entity[3];
        else
            paramsTypeStr = "[\"java.lang.String\"]";
        String url = jarPath + "/" + jarName;
        File file = new File(url);
        String result = "";
        try {
            URL fileUrl = file.toURI().toURL();
            ClassLoader classLoader = new URLClassLoader(new URL[]{fileUrl});
            Thread.currentThread().setContextClassLoader(classLoader);
            Class<?> clazz = classLoader.loadClass(className);// 使用loadClass方法加载class,这个class是在urls参数指定的classpath下边。
            List<Class> classes = JSON.parseArray(paramsTypeStr, Class.class);
            Method taskMethod = clazz.getDeclaredMethod(methodName, classes.toArray(new Class[0]));
            String payload = mqttMsg.getPayload();
            JSONArray jsonArray = JSONObject.parseArray(payload);
            Object invoke = taskMethod.invoke(clazz.newInstance(), jsonArray.toArray());
//            Object invoke = taskMethod.invoke(clazz.newInstance(), mqttMsg.getPayload());
            result = invoke instanceof String ? (String) invoke : JSON.toJSONString(invoke);
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
        RegScheduler regScheduler = iaContext.getIaENV().getRegScheduler();
//        return SpringContextUtil.getServiceMap().get(topic);
        //todo
        String data = regScheduler.getData(topic);
        if (StringUtils.isNotBlank(data)) {
            RegisterSub sub = JSONObject.parseObject(data, RegisterSub.class);
            if (sub != null)
                return sub.getExecuteUnit();
        }
        return "";

    }


    public String getAcyBySubList() {
        return "topic_ack";
    }


    @ServiceActivator(inputChannel = MqttConfig.CHANNEL_NAME_IN/*, outputChannel = CHANNEL_NAME_OUT*/)
    public void messageReceiver(Message<String> message) {

        mqttExecutor.execute(() -> {
            //至少送达一次存在重复发送的几率，所以订阅服务需要判断消息订阅的幂等性,幂等性可以通过消息属性判断是否重复发送
            Boolean mqtt_duplicate = (Boolean) message.getHeaders().get("mqtt_duplicate");
            if (mqtt_duplicate != null && mqtt_duplicate) {
                logger.warn("message receive duplicate [{}]", message);
                return;
            }
            Object mqtt_receivedTopic = message.getHeaders().get("mqtt_topic");//1.2.*版本获取topic方式不一样
            if (mqtt_receivedTopic == null) {
                logger.error("message mqtt_topic is null [{}]", message);
                return;
            }
            String topic = mqtt_receivedTopic.toString();
            String payload = message.getPayload();
            MqttMsg mqttMsg = JSONObject.parseObject(payload, MqttMsg.class);
            MqttResp result = receive(topic, mqttMsg);
            if (!mqttMsg.getHasAck()) return; //如果不需返回
            String ackTopic = MqttUtils.preconditionSubTopicId(mqttMsg.getSourceAddress(), mqttMsg.getOperaCode()) + "/ack";
            result.setTopic(ackTopic);
            // String ackPayload = JSONObject.toJSONString(result);

            String ackPayload = result.getPayload();

            byte[] bytes = ackPayload.getBytes(StandardCharsets.UTF_8);
            int length = bytes.length;
            try {
                if (length > MQTT_PAYLOAD_LIMIT) {
                    //分包
                    List<MqttResp> resultArr = subpackage(bytes, mqttMsg.getMsgId());
                    logger.info("[{}] message subpackage, package count:[{}]", mqttMsg.getMsgId(), resultArr.size());
                    int size = resultArr.size();
                    for (int i = 0; i < size; i++) {
                        MqttResp resp = resultArr.get(i);
                        Thread.sleep(10L);
                        iMqttSender.sendToMqtt(ackTopic, 1, JSONObject.toJSONString(resp));
                    }
                } else
                    iMqttSender.sendToMqtt(ackTopic, 1, JSONObject.toJSONString(result));
            } catch (Exception e) {
                logger.error("[{}] message ", e.toString());
            }
        });

    }



    /**
     * 分包
     *
     * @param
     * @return
     */
    private List<MqttResp> subpackage(byte[] bytes, String msgId) {
        List<MqttResp> result = new ArrayList<>();
        int crc = ByteUtil.getCRC(bytes);
        int length = bytes.length;
        int count = length / MQTT_PAYLOAD_LIMIT + 1;
        for (int i = 0; i < count; i++) {
            byte[] subArray = ArrayUtils.subarray(bytes, i * MQTT_PAYLOAD_LIMIT, MQTT_PAYLOAD_LIMIT * (i + 1));
            MqttResp resp = new MqttResp(msgId, subArray, true, i, count, crc);
            result.add(resp);
        }
        return result;
    }


    @PreDestroy
    public void destroy() {
        logger.info("mqtt receiver destroy...");
        try {
            logger.info("destroy inChanel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (mqttExecutor.getActiveCount() > 0) {
            int mqttExecutorActiveCount = mqttExecutor.getActiveCount();
            if (mqttExecutorActiveCount >= 50) {
                logger.warn("mqtt task executor status: pool size:[{}], active count:[{}], max pool size:[{}] ",
                        mqttExecutor.getPoolSize(), mqttExecutorActiveCount, mqttExecutor.getMaxPoolSize());
            }
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}

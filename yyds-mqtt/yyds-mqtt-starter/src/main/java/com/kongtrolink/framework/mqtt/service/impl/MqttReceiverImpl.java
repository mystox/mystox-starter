package com.kongtrolink.framework.mqtt.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.mqtt.entity.MqttMsg;
import com.kongtrolink.framework.mqtt.service.IMqttSender;
import com.kongtrolink.framework.mqtt.service.MqttReceiver;
import com.kongtrolink.framework.mqtt.util.SpringContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by mystoxlol on 2019/8/13, 11:05.
 * company: kongtrolink
 * description:
 * update record:
 */
@Component
public class MqttReceiverImpl implements MqttReceiver {
    private static final Logger logger = LoggerFactory.getLogger(MqttReceiverImpl.class);
    private static String LOCAL = "local://";
    private static String JAR = "jar://";
    private static String HTTP = "http://";

    @Value("${jar.path:./jarResources}")
    private String jarPath;

    /**
     * 注入发送MQTT的Bean
     */
    @Resource
    private IMqttSender iMqttSender;

    @Override
    public String receive(String topic, String payload) {
        logger.info("receive... ..." + payload);
        String unit = getUnitBySubList();
        String result = "";
        if (unit.startsWith(LOCAL)) { //执行本地函数和方法
            String[] entity = unit.replace(LOCAL, "").split("/");
            String className = entity[0];
            String methodName = entity[1];
            try {
                Class<?> clazz = Class.forName(className);
                Object bean = SpringContextUtil.getBean(clazz);
                Method method = clazz.getDeclaredMethod(methodName, MqttMsg.class);
                MqttMsg mqttMsg = JSONObject.parseObject(payload, MqttMsg.class);
                Object invoke = method.invoke(bean, mqttMsg.getPayload());
                result = JSONObject.toJSONString(invoke);
                logger.info("local result: {}", result);
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else if (unit.startsWith(JAR)) //亦可执行本地和远程的jar，远程可执行jar以仓库的方式开放。
        {
            String[] entity = unit.replace(JAR, "").split("/");
            String jarName = entity[0];
            String className = entity[1];
            String methodName = entity[2];
            String url = jarPath + "/" + jarName;
            File file = new File(url);
            try {
                URL fileUrl = file.toURI().toURL();
                ClassLoader classLoader = new URLClassLoader(new URL[]{fileUrl});
                Thread.currentThread().setContextClassLoader(classLoader);
                Class<?> clazz = classLoader.loadClass(className);// 使用loadClass方法加载class,这个class是在urls参数指定的classpath下边。
                Method taskMethod = clazz.getMethod(methodName, String.class);
                MqttMsg mqttMsg = JSONObject.parseObject(payload, MqttMsg.class);
                Object invoke = taskMethod.invoke(clazz.newInstance(), mqttMsg.getPayload());
                result = JSONObject.toJSONString(invoke);
                logger.info("jar result: {}", result);
            } catch (MalformedURLException | InstantiationException | IllegalAccessException
                    | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }

        } else if (unit.startsWith(HTTP)) {
            //todo 执行远程的http服务器
        }

        String topic_ack = getAcyBySubList();

        if (StringUtils.isNotBlank(topic_ack))
            iMqttSender.sendToMqtt(topic_ack, 2, result);

        return "ok";
    }


    public String getUnitBySubList() {
        //注册中心获取topic对应的jar单元
//        return "local://com.kongtrolink.framework.api.LocalService/hello";
        return "jar://mystoxUtil.jar/tech.mystox.test.util.MystoxUtil/sayHello";
    }


    public String getAcyBySubList() {
        return "topic_ack";
    }
}

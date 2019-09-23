package com.kongtrolink.framework.register.runner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.common.util.MqttUtils;
import com.kongtrolink.framework.entity.*;
import com.kongtrolink.framework.exception.RegisterAnalyseException;
import com.kongtrolink.framework.register.entity.RegisterMsg;
import com.kongtrolink.framework.register.entity.RegisterType;
import com.kongtrolink.framework.register.service.ServiceRegistry;
import com.kongtrolink.framework.service.MqttHandler;
import com.kongtrolink.framework.service.MqttSender;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by mystoxlol on 2019/8/28, 13:31.
 * company: kongtrolink
 * description: 注册启动类
 * update record:
 */
@Component
public class RegisterRunner implements ApplicationRunner {
    private Logger logger = LoggerFactory.getLogger(RegisterRunner.class);


    @Value("${server.name}")
    private String serverName;

    @Value("${server.version}")
    private String serverVersion;

    @Value("${server.name}_${server.version}")
    private String serverCode;

    @Value("${register.url:zookeeper://172.16.5.26:2181,172.16.5.26:2182,172.16.5.26:2183}")
    private String registerUrl;

    @Value("${register.serverName:" + ServerName.AUTH_PLATFORM + "}")
    private String registerServerName;
    @Value("${register.version:1.0.0}")
    private String registerServerVersion;

    @Value("${spring.profiles.active:dev}")
    private String devFlag;


    @Autowired
    ServiceRegistry serviceRegistry;
    @Autowired
    ServiceScanner localServiceScanner;
    @Autowired
    ServiceScanner jarServiceScanner;



    private MqttSender mqttSender;
    @Autowired(required = false)
    public void setMqttSender(MqttSender mqttSender) {
        this.mqttSender = mqttSender;
    }


    MqttHandler mqttHandler;
    @Autowired(required = false)
    @Qualifier("mqttHandlerImpl")
    public void setMqttHandler(MqttHandler mqttHandler) {
        this.mqttHandler = mqttHandler;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<RegisterSub> subList = scanSubList();
        RegisterMsg registerMsg = getRegisterMsg();
        if (registerMsg == null)
            System.exit(0);
        register(registerMsg, subList);//注册操作码信息
        subTopic(subList);//订阅操作码对应topic
        registerServer(registerMsg); //注册服务信息
        logger.info("register successfully..." + serverCode);
    }

    private void registerServer(RegisterMsg registerMsg) {
        //todo
       /* RegisterType registerType = registerMsg.getRegisterType();
        if (RegisterType.ZOOKEEPER.equals(registerType)) {
            serviceRegistry.build(registerMsg.getRegisterUrl());
            for (RegisterSub sub : subList) {
                setDataToRegistry(sub);
            }
            //往服务节点注册服务信息

        } else {
            throw new RegisterAnalyseException(registerMsg.getRegisterUrl());
        }*/
    }


    /**
     * 重连注册
     */
    public void multiRegister() throws InterruptedException, IOException, KeeperException {
        List<RegisterSub> subList = scanSubList();
        for (RegisterSub sub : subList) {
            setDataToRegistry(sub);
        }
    }

    /**
     * 往注册中心注册数据
     *
     * @param sub
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void setDataToRegistry(RegisterSub sub) throws KeeperException, InterruptedException {
        String operaCode = sub.getOperaCode();
        String nodePath = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
        if (!serviceRegistry.exists("/mqtt"))
            serviceRegistry.create("/mqtt", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        if (!serviceRegistry.exists("/mqtt/sub"))
            serviceRegistry.create("/mqtt/sub", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        if (!serviceRegistry.exists("/mqtt/sub/" + serverCode))
            serviceRegistry.create("/mqtt/sub/" + serverCode, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        if (!serviceRegistry.exists(nodePath))
            serviceRegistry.create(nodePath, JSONObject.toJSONBytes(sub), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        else {
            serviceRegistry.setData(nodePath, JSONObject.toJSONBytes(sub));
        }
    }

    private void subTopic(List<RegisterSub> subList) {
        subList.forEach(sub -> {
            String operaCode = sub.getOperaCode();
            String topicId = MqttUtils.preconditionSubTopicId(serverCode, operaCode);
            if (mqttHandler != null) {
                if (!mqttHandler.isExists(topicId))
                mqttHandler.addSubTopic(topicId, 2);
            }
        });

    }

    /**
     * 注册中心注册节点信息
     *
     * @param registerMsg
     * @param subList
     * @throws IOException
     * @throws InterruptedException
     * @throws KeeperException
     */
    private void register(RegisterMsg registerMsg, List<RegisterSub> subList) throws IOException, InterruptedException, KeeperException {
        RegisterType registerType = registerMsg.getRegisterType();
        if (RegisterType.ZOOKEEPER.equals(registerType)) {
            serviceRegistry.build(registerMsg.getRegisterUrl());
            for (RegisterSub sub : subList) {
            //往服务节点注册服务信息
                setDataToRegistry(sub);
            }

        } else {
            throw new RegisterAnalyseException(registerMsg.getRegisterUrl());
        }
    }

    /**
     * 扫描服务能力
     *
     * @return
     */
    private List<RegisterSub> scanSubList() {
        List<RegisterSub> subList = new ArrayList<>();
        subList.addAll(scanLocal());
        subList.addAll(scanJar());
        subList.addAll(scanHttp());
        return subList;
    }

    private List<RegisterSub> scanHttp() {
        List<RegisterSub> subList = new ArrayList<>();
        //todo 暂不实现
        return subList;
    }

    private List<RegisterSub> scanLocal() {
        return localServiceScanner.getSubList();
    }

    private List<RegisterSub> scanJar() {
        List<RegisterSub> subList = jarServiceScanner.getSubList();
        return subList;
    }

    /**
     * 通过云管注册服务获取注册中心地址
     *
     * @return
     */
    public RegisterMsg getRegisterMsg() {
        RegisterMsg registerMsg = new RegisterMsg();
        if (!serverName.equals(registerServerName)) {
            JSONObject server = new JSONObject();
            server.put("serverName", serverName);
            server.put("serverVersion", serverVersion);
            String sLoginPayload = server.toJSONString();
            MsgResult slogin = mqttSender.sendToMqttSyn(
                    MqttUtils.preconditionServerCode(registerServerName, registerServerVersion),
                    OperaCode.SLOGIN, 2, sLoginPayload, 30000L, TimeUnit.MILLISECONDS);
            int stateCode = slogin.getStateCode();
            if (StateCode.SUCCESS == stateCode) {
                String msg = slogin.getMsg();
                Object parse = JSON.parse(msg);
                if (parse instanceof JSONObject)
                    registerUrl = ((JSONObject) parse).getString("registerUrl");
                logger.info("get slogin result(registerUrl) is [{}]", registerUrl);
            } else {
                logger.error("slogin failed state[{}], msg: [{}]", stateCode, slogin.getMsg());
                if ("dev".equals(devFlag)) {
                    logger.warn("environment ${spring.profiles.active} is dev, set registerUrl is [{}]", registerUrl);
                } else {
                    return null;
                }
            }
        }
        logger.info("{} registerUrl is: [{}]", serverName, registerUrl);
        String[] split = registerUrl.split("://");
        String registerUrlHeader = split[0];
        String registerHosts = split[1];
        if (StringUtils.equals(RegisterType.ZOOKEEPER.toString(), registerUrlHeader.toUpperCase()))
            registerMsg.setRegisterType(RegisterType.ZOOKEEPER);
        if (StringUtils.equals(RegisterType.REDIS.toString(), registerUrlHeader.toUpperCase()))
            registerMsg.setRegisterType(RegisterType.REDIS);

        registerMsg.setRegisterUrl(registerHosts);
        return registerMsg;
    }

}

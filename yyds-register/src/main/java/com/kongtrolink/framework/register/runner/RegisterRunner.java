package com.kongtrolink.framework.register.runner;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.common.util.MqttUtils;
import com.kongtrolink.framework.entity.RegisterSub;
import com.kongtrolink.framework.register.entity.RegisterMsg;
import com.kongtrolink.framework.register.entity.RegisterType;
import com.kongtrolink.framework.register.service.ServiceRegistry;
import com.kongtrolink.framework.service.MqttHandler;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private String version;


    @Autowired
    ServiceRegistry serviceRegistry;
    @Autowired
    ServiceScanner localServiceScanner;
    @Autowired
    ServiceScanner jarServiceScanner;


    MqttHandler mqttHandler;

    @Autowired(required = false)
    public void setMqttHandler(MqttHandler mqttHandler) {
        this.mqttHandler = mqttHandler;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<RegisterSub> subList = scanSubList();
        RegisterMsg registerMsg = getRegisterMsg();
        register(registerMsg, subList);
        subTopic(subList);
//        subTopic(subList);
        logger.info("register successfully...");
    }


    /**
     * 重连注册
     */
    public void multiRegister() throws InterruptedException, IOException, KeeperException {
        List<RegisterSub> subList = scanSubList();
        String serverCode = MqttUtils.preconditionServerCode(serverName, version);
        for (RegisterSub sub : subList) {
            setDataToRegistry(sub, serverCode);
        }
    }

    /**
     * 往注册中心注册数据
     * @param sub
     * @param serverCode
     * @throws KeeperException
     * @throws InterruptedException
     */
    void setDataToRegistry(RegisterSub sub, String serverCode) throws KeeperException, InterruptedException {
        String operaCode = sub.getOperaCode();
        String nodePath = MqttUtils.preconditionSubTopicId(MqttUtils.preconditionServerCode(serverName, version), operaCode);
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
            String topicId = MqttUtils.preconditionSubTopicId(MqttUtils.preconditionServerCode(serverName, version), operaCode);
            if (mqttHandler != null) {
                mqttHandler.addSubTopic(topicId, 2);
            }
        });

    }

    /**
     * 注册中心注册节点信息
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
            String serverCode = MqttUtils.preconditionServerCode(serverName, version);
            for (RegisterSub sub : subList) {
                setDataToRegistry(sub, serverCode);
            }

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

    public RegisterMsg getRegisterMsg() {
        RegisterMsg registerMsg = new RegisterMsg();
        registerMsg.setRegisterType(RegisterType.ZOOKEEPER);
        registerMsg.setRegisterUrl("172.16.5.26:2181,172.6.5.26:2182,172.6.5.26:2183");
        return registerMsg;
    }

}

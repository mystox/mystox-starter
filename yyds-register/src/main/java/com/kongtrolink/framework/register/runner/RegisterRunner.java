package com.kongtrolink.framework.register.runner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.common.util.MqttUtils;
import com.kongtrolink.framework.entity.*;
import com.kongtrolink.framework.exception.RegisterAnalyseException;
import com.kongtrolink.framework.register.config.WebPrivFuncConfig;
import com.kongtrolink.framework.register.entity.PrivFuncEntity;
import com.kongtrolink.framework.register.entity.RegisterMsg;
import com.kongtrolink.framework.register.entity.RegisterType;
import com.kongtrolink.framework.register.entity.ServerMsg;
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
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InterruptedIOException;
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
@Order(1)
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

    @Value("${server.host:127.0.0.1}")
    private String host;

    @Value("${server.port}")
    private int port;

    @Value("${server.title:}")
    private String title;
    @Value("${server.serverUri:}")
    private String serverUri;

    @Value("${server.pageRoute:}")
    private String pageRoute;

    @Value("${server.routeMark:}")
    private String routeMark;


    @Autowired
    ServiceRegistry serviceRegistry;
    @Autowired
    ServiceScanner localServiceScanner;
    @Autowired
    ServiceScanner jarServiceScanner;


    WebPrivFuncConfig webPrivFuncConfig;

    @Autowired
    public void setWebPrivFuncConfig(WebPrivFuncConfig webPrivFuncConfig) {
        this.webPrivFuncConfig = webPrivFuncConfig;
    }

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
    public void run(ApplicationArguments args) {
        try {
            RegisterMsg registerMsg = getRegisterMsg();
            List<RegisterSub> subList = scanSubList();
            if (registerMsg == null)
                System.exit(0);
            register(registerMsg, subList);//注册操作码信息
            subTopic(subList);//订阅操作码对应topic
            registerWebPriv();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        //验证服务能力（pubList）
        logger.info("register successfully...serverCode[{}]", serverCode);
    }

    /**
     * 注册web 功能权限
     */
    private void registerWebPriv() {
        PrivFuncEntity privFunc = webPrivFuncConfig.getPrivFunc();
        if (privFunc != null) {
            //往云管注册功能权限
            JSONObject registerMsg = new JSONObject();
//            String privFuncJson = JSONObject.toJSONString(privFunc);
            registerMsg.put("serverCode", serverCode);
            registerMsg.put("webPrivFunc", privFunc);
            MsgResult registerWeb = mqttSender.sendToMqttSyn(
                    MqttUtils.preconditionServerCode(registerServerName, registerServerVersion),
                    OperaCode.REGISTER_WEB_PRIV_FUNC, 2, registerMsg.toJSONString(), 30000L, TimeUnit.MILLISECONDS);
            logger.info("register web privilege function result:{}",JSONObject.toJSONString(registerWeb));
        } else {
            logger.warn("web privilege function config is null...");
        }
    }

    /**
     * 注册服务信息
     *
     * @throws KeeperException
     * @throws InterruptedException
     * @throws IOException
     */
    private void registerServer() throws KeeperException, InterruptedException, IOException {

        //获取服务信息
//        ServerMsg serverMsg = new ServerMsg(host, port, serverName, serverVersion, routeMark, pageRoute, serverUri, title);

        if (!serviceRegistry.exists(TopicPrefix.TOPIC_PREFIX))
            serviceRegistry.create(TopicPrefix.TOPIC_PREFIX, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        //订阅列表目录
        if (!serviceRegistry.exists(TopicPrefix.SUB_PREFIX))
            serviceRegistry.create(TopicPrefix.SUB_PREFIX, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
       /* String subPath = TopicPrefix.SUB_PREFIX + "/" + serverCode;
        if (!serviceRegistry.exists(subPath))
            serviceRegistry.create(subPath, JSONObject.toJSONBytes(serverMsg), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        else
            serviceRegistry.setData(TopicPrefix.SUB_PREFIX + "/" + serverCode, JSONObject.toJSONBytes(serverMsg));*/
        //在线标志
        if (!serviceRegistry.exists(TopicPrefix.SERVER_STATUS))
            serviceRegistry.create(TopicPrefix.SERVER_STATUS, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        /*String onlineStatus = TopicPrefix.SERVER_STATUS + "/" + OperaCode.ONLINE;
        if (serviceRegistry.exists(onlineStatus))
            throw new InterruptedIOException();
        else
            serviceRegistry.create(onlineStatus, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);*/

        //请求列表目录
        if (!serviceRegistry.exists(TopicPrefix.PUB_PREFIX))
            serviceRegistry.create(TopicPrefix.PUB_PREFIX, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        String pubPath = TopicPrefix.PUB_PREFIX + "/" + serverCode;
        if (!serviceRegistry.exists(pubPath))
            serviceRegistry.create(pubPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }


    /**
     * 重连注册
     */
    public void multiRegister() throws InterruptedException, IOException, KeeperException {
        List<RegisterSub> subList = scanSubList();//重新扫描
        for (RegisterSub sub : subList) {
            setDataToRegistry(sub);
        }
        registerServerMsg();

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
            registerServer(); //注册服务基本信息
            for (RegisterSub sub : subList) {
                //往服务节点注册服务信息
                setDataToRegistry(sub);
            }
            registerServerMsg();
        } else {
            throw new RegisterAnalyseException(registerMsg.getRegisterUrl());
        }
    }

    private void registerServerMsg() throws KeeperException, InterruptedException, InterruptedIOException {

        //获取服务信息并注册
        ServerMsg serverMsg = new ServerMsg(host, port, serverName, serverVersion, routeMark, pageRoute, serverUri, title);
        String subPath = TopicPrefix.SUB_PREFIX + "/" + serverCode;
        if (!serviceRegistry.exists(subPath))
            serviceRegistry.create(subPath, JSONObject.toJSONBytes(serverMsg), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        else
            serviceRegistry.setData(TopicPrefix.SUB_PREFIX + "/" + serverCode, JSONObject.toJSONBytes(serverMsg));


        //在线状态
        String onlineStatus = TopicPrefix.SERVER_STATUS + "/" + MqttUtils.preconditionServerCode(serverName, serverVersion);
        if (serviceRegistry.exists(onlineStatus))
            throw new InterruptedIOException();
        else
            serviceRegistry.create(onlineStatus, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
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
            ServerMsg serverMsg = new ServerMsg(host, port, serverName, serverVersion, routeMark, pageRoute, serverUri, title);
            String sLoginPayload = JSONObject.toJSONString(serverMsg);
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

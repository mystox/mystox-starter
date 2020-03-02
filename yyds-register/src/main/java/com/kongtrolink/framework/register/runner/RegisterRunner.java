package com.kongtrolink.framework.register.runner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.common.util.MqttUtils;
import com.kongtrolink.framework.entity.*;
import com.kongtrolink.framework.exception.RegisterAnalyseException;
import com.kongtrolink.framework.register.config.OperaRouteConfig;
import com.kongtrolink.framework.register.config.WebPrivFuncConfig;
import com.kongtrolink.framework.register.entity.PrivFuncEntity;
import com.kongtrolink.framework.register.entity.RegisterMsg;
import com.kongtrolink.framework.register.entity.RegisterType;
import com.kongtrolink.framework.register.entity.ServerMsg;
import com.kongtrolink.framework.register.service.ServiceRegistry;
import com.kongtrolink.framework.service.MqttHandler;
import com.kongtrolink.framework.service.MqttOpera;
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
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.*;

import static com.kongtrolink.framework.common.util.MqttUtils.*;

/**
 * Created by mystoxlol on 2019/8/28, 13:31.
 * company: kongtrolink
 * description: 注册启动类
 * update record:
 */
@Component
@DependsOn(value = "mqttOutbound")
@Order(1)
public class RegisterRunner implements ApplicationRunner {
    private Logger logger = LoggerFactory.getLogger(RegisterRunner.class);


    @Value("${server.name}")
    private String serverName;

    @Value("${server.version}")
    private String serverVersion;

    @Value("${server.mark:*}")
    private String serverMark;

    @Value("${server.groupCode}")
    private String groupCode;

//    @Value("${server.name}_${server.version}")
//    private String serverCode;

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
    @Autowired
    @Qualifier("mqttHandlerAck")
    private MqttHandler mqttHandlerAck;

    WebPrivFuncConfig webPrivFuncConfig;

    @Autowired
    public void setWebPrivFuncConfig(WebPrivFuncConfig webPrivFuncConfig) {
        this.webPrivFuncConfig = webPrivFuncConfig;
    }

    public WebPrivFuncConfig getWebPrivFuncConfig() {
        return webPrivFuncConfig;
    }

//    private MqttSender mqttSender;
//
//    @Autowired(required = false)
//    public void setMqttSender(MqttSender mqttSender) {
//        this.mqttSender = mqttSender;
//    }

    private OperaRouteConfig operaRouteConfig;

    @Autowired
    public void setOperaRouteConfig(OperaRouteConfig operaRouteConfig) {
        this.operaRouteConfig = operaRouteConfig;
    }

    private MqttOpera mqttOpera;

    @Autowired(required = false)
    public void setMqttOpera(MqttOpera mqttOpera) {
        this.mqttOpera = mqttOpera;
    }

    private MqttHandler mqttHandler;

    @Autowired(required = false)
    @Qualifier("mqttHandlerImpl")
    public void setMqttHandler(MqttHandler mqttHandler) {
        this.mqttHandler = mqttHandler;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            ackTopic();//ack操作嘛对应的topic注册 格式样例 /mqtt/sub/GROUP_CODE/SERVER_CODE/+/ack
            RegisterMsg registerMsg = getRegisterMsg();
            if (registerMsg == null) {
                logger.error("register message is null");
                System.exit(0);

            }
            List<RegisterSub> subList = scanSubList();
            register(registerMsg, subList);//注册操作码信息
            subTopic(subList);//订阅操作码对应topic
            // 注册操作路由信息
            registerOperaRoute();
            //web注册置于最后，适应运管的启动顺序
           /* if (result != null) {
                if (result.getStateCode() == StateCode.SUCCESS) {
                    logger.info("register web privilege function result:{}", result.getResult());
                } else {
                    logger.error("register web privilege function result:{}", result.getResult());
                    System.exit(0);
                }
            }*/
        } catch (Exception e) {
            if (logger.isDebugEnabled())
                e.printStackTrace();
            logger.error("register failed...serverCode[{}]", e.toString());
            System.exit(0);
        }
        logger.info("register successfully...serverCode[{}]", preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion)));
    }

    /**
     * @return void
     * @Date 20:28 2020/1/5
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description 注册操作路由信息
     **/
    private void registerOperaRoute() throws KeeperException, InterruptedException {
        Map<String, List<String>> operaRoute = operaRouteConfig.getOperaRoute();
        String groupCodeServerCode = preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion));
        if (operaRoute != null) {
            Set<String> operaCodes = operaRoute.keySet();
            for (String operaCode : operaCodes) {
                List<String> subServerArr = operaRoute.get(operaCode);
                String routePath = preconditionRoutePath(groupCodeServerCode, operaCode);
                if (!serviceRegistry.exists(routePath))
                    serviceRegistry.create(routePath, JSONArray.toJSONBytes(subServerArr), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                else
                    serviceRegistry.setData(routePath, JSONArray.toJSONBytes(subServerArr));
            }
        }
    }

    /**
     * 注册web 功能权限
     */
    public OperaResult registerWebPriv() throws KeeperException, InterruptedException {
        PrivFuncEntity privFunc = webPrivFuncConfig.getPrivFunc();
        if (privFunc != null) {
            //往云管注册功能权限（放弃）
//            JSONObject registerMsg = new JSONObject();
////            String privFuncJson = JSONObject.toJSONString(privFunc);
//            ServerMsg serverMsg = new ServerMsg(host, port, serverName, serverVersion,
//                    routeMark, pageRoute, serverUri, title);
//            registerMsg.put("serverCode", serverMsg);
//            registerMsg.put("webPrivFunc", privFunc);
            //获取服务信息并注册至注册中心
            String privPath = TopicPrefix.PRIV_PREFIX + "/" +
                    preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion));
            if (serviceRegistry.exists(privPath))
                serviceRegistry.setData(privPath, JSONObject.toJSONBytes(privFunc));
            else
                serviceRegistry.create(privPath, JSONObject.toJSONBytes(privFunc), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
           /* MsgResult registerWeb = mqttSender.sendToMqttSyn(
                    MqttUtils.preconditionServerCode(registerServerName, registerServerVersion),
                    OperaCode.REGISTER_WEB_PRIV_FUNC, 2, registerMsg.toJSONString(), 30000L, TimeUnit.MILLISECONDS);
            String msg = registerWeb.getMsg();
            if (StateCode.SUCCESS == registerWeb.getStateCode()) {
                OperaResult result = JSONObject.parseObject(msg, OperaResult.class);
                return result;
            }*/
        } else {
            logger.warn("web privilege function config is null...");
        }
        return null;
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
        //顶层目录
        if (!serviceRegistry.exists(TopicPrefix.TOPIC_PREFIX))
            serviceRegistry.create(TopicPrefix.TOPIC_PREFIX, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        //订阅列表目录
        String serverCode = preconditionServerCode(serverName, serverVersion);
        createPath(TopicPrefix.SUB_PREFIX, groupCode, serverCode);
        //请求列表目录
        createPath(TopicPrefix.PUB_PREFIX, groupCode, serverCode);
        //功能权限目录
        createPath(TopicPrefix.PRIV_PREFIX, groupCode, null);
        //在线标志目录
        createPath(TopicPrefix.SERVER_STATUS, groupCode, null);
        //操作请求路由表目录
        createPath(TopicPrefix.OPERA_ROUTE, groupCode, serverCode);
    }

    void createPath(String topicPrefix, String groupCode, String serverCode) throws KeeperException, InterruptedException {
        if (!serviceRegistry.exists(topicPrefix))
            serviceRegistry.create(topicPrefix, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        if (StringUtils.isNotBlank(groupCode)) {
            String groupPath = topicPrefix + "/" + groupCode;
            if (!serviceRegistry.exists(groupPath))
                serviceRegistry.create(groupPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            if (StringUtils.isNotBlank(serverCode)) {
                String serverPath = groupPath + "/" + serverCode;
                if (!serviceRegistry.exists(serverPath))
                    serviceRegistry.create(serverPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        }
    }

    /**
     * 重连注册
     */
    public void multiRegister() throws InterruptedException, IOException, KeeperException {
        ackTopic();
        List<RegisterSub> subList = scanSubList();//重新扫描
        for (RegisterSub sub : subList) {
            setDataToRegistry(sub);
        }
        registerServerMsg();
        registerWebPriv();
        registerOperaRoute();
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
        String nodePath = MqttUtils.preconditionSubTopicId(
                preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion)), operaCode);
        if (!serviceRegistry.exists(nodePath))
            serviceRegistry.create(nodePath, JSONObject.toJSONBytes(sub), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        else {
            serviceRegistry.setData(nodePath, JSONObject.toJSONBytes(sub));
        }
    }

    /**
     * @Date 0:22 2020/1/6
     * @Param No such property: code for class: Script1
     * @return void
     * @Author mystox
     * @Description 订阅订阅表信息
     **/
    private void subTopic(List<RegisterSub> subList) {
        subList.forEach(sub -> {
            String operaCode = sub.getOperaCode();
            String topicId = MqttUtils.preconditionSubTopicId(
                    preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion)), operaCode);
            if (mqttHandler != null) {
                if (!mqttHandler.isExists(topicId))
                    //logger.info("订阅了:{} ",topicId);
                    mqttHandler.addSubTopic(topicId, 2);
            }
        });


    }

    /**
     * @Date 0:22 2020/1/6
     * @Param No such property: code for class: Script1
     * @return void
     * @Author mystox
     * @Description 订阅统一ackTopic
     **/
    private void ackTopic() {
        String ackTopicId = preconditionSubACKTopicId(preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion)));
        logger.info("add ackTopicId is [{}]", ackTopicId);
        if (!mqttHandlerAck.isExists(ackTopicId))
            mqttHandlerAck.addSubTopic(ackTopicId, 2);
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
            registerServer(); //初始化目录信息
            registerServerMsg(); //注册服务信息
            registerWebPriv(); //注册服务目录
            for (RegisterSub sub : subList) {
                //往服务节点注册订阅服务信息
                setDataToRegistry(sub);
            }
        } else {
            throw new RegisterAnalyseException(registerMsg.getRegisterUrl());
        }
    }

    /**
     * @Date 0:21 2020/1/6
     * @Param No such property: code for class: Script1
     * @return void
     * @Author mystox
     * @Description 注册服务信息
     **/
    private void registerServerMsg() throws KeeperException, InterruptedException, InterruptedIOException {

        //获取服务信息并注册
        ServerMsg serverMsg = new ServerMsg(host, port, serverName, serverVersion,
                routeMark, pageRoute, serverUri, title, groupCode);
//        String subPath = TopicPrefix.SERVER_STATUS + "/" + serverCode;
//        if (serviceRegistry.exists(subPath))
//            serviceRegistry.setData(subPath, JSONObject.toJSONBytes(serverMsg));

        //在线状态
//        String serverCode = MqttUtils.preconditionServerCode(serverName, serverVersion);
//        String onlineStatus = TopicPrefix.SERVER_STATUS + "/" + preconditionGroupServerCode(groupCode, serverCode);
        String onlineStatus = preconditionGroupServerPath(TopicPrefix.SERVER_STATUS,
                preconditionGroupServerCode(groupCode,
                        preconditionServerCode(serverName, serverVersion)));
        if (serviceRegistry.exists(onlineStatus)) { //检测和创建服务在线的临时节点判断
            logger.error("server[{}] status is already online ... exits", preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion)));
            throw new InterruptedIOException();
        } else {
            serviceRegistry.create(onlineStatus, JSONObject.toJSONBytes(serverMsg), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
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
            ServerMsg serverMsg = new ServerMsg(host, port, serverName, serverVersion,
                    routeMark, pageRoute, serverUri, title, groupCode);
            String sLoginPayload = JSONObject.toJSONString(serverMsg);
//            MsgResult slogin = mqttSender.sendToMqttSyn(
//                    MqttUtils.preconditionServerCode(registerServerName, registerServerVersion),
//                    OperaCode.SLOGIN, 2, sLoginPayload, 30000L, TimeUnit.MILLISECONDS);
            MsgResult slogin = mqttOpera.slogin(
                    preconditionGroupServerCode(GroupCode.ROOT, preconditionServerCode(registerServerName, registerServerVersion)),
                    sLoginPayload);
            int stateCode = slogin.getStateCode();
            if (StateCode.SUCCESS == stateCode) {
                String msg = slogin.getMsg();
                Object parse = JSON.parse(msg);
                if (parse instanceof JSONObject) {
                    registerUrl = ((JSONObject) parse).getString("registerUrl");
                    if (StringUtils.isBlank(registerUrl)) {
                        String errorMsg = ((JSONObject) parse).getString("errorMsg");
                        logger.error("slogin failed state[{}], msg: [{}]", stateCode, errorMsg);
                        return null;
                    }
                    logger.info("get slogin result(registerUrl) is [{}]", registerUrl);
                }
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

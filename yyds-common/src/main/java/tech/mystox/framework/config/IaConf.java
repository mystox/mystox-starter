package tech.mystox.framework.config;

import tech.mystox.framework.core.ServiceScanner;
import tech.mystox.framework.entity.ServerName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties
public class IaConf {


    //    @Autowired
//    @Qualifier("remotezkHandlerImpl")
//    RegHandler zkhandlerImpl;
    private String myId="0";

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }

    /**
     * The znode will not be automatically deleted upon client's disconnect.
     */
    public static final int  PERSISTENT =0;
    /**
     * The znode will not be automatically deleted upon client's disconnect,
     * and its name will be appended with a monotonically increasing number.
     */
    public static final int PERSISTENT_SEQUENTIAL=2;
    /**
     * The znode will be deleted upon the client's disconnect.
     */
    public static final int EPHEMERAL =1;
    /**
     * The znode will be deleted upon the client's disconnect, and its name
     * will be appended with a monotonically increasing number.
     */
    public static final int EPHEMERAL_SEQUENTIAL =3;

    public static final String MqttMsgBus="mqtt";
    public static final String ZkRegType ="zookeeper";

    //MQTT相关~~~~~~~~~~~~~~~~~~~~~~~~~~
    private static final byte[] WILL_DATA;
    static {
        WILL_DATA = "offline".getBytes();
    }
    @Value("${server.name}")
    private String serverName;
    @Value("${server.version}")
    private String serverVersion;
    @Value("${server.mark:*}")
    private String serverMark;
    @Value("${server.groupCode}")
    private  String groupCode;

    @Value("${mqtt.username:root}")
    private String username;
    @Value("${mqtt.password:123456}")
    private String password;
    @Value("${mqtt.url}")
    private String url;
    @Value("${mqtt.producer.clientId}")
    private String producerClientId;
    @Value("${mqtt.producer.defaultTopic}")
    private String producerDefaultTopic;
    @Value("${mqtt.consumer.clientId}")
    private String consumerClientId;
    @Value("${mqtt.consumer.defaultTopic}")
    private String consumerDefaultTopic;
    @Value("${mqtt.completionTimeout}")
    private int completionTimeout;   //mqtt连接超时
    @Value("${zookeeper.sessionTimeout:100}")
    private int regSessionTimeout; //zookeeper session超时时间
    /**
     * 订阅的bean名称
     */
    public static final String CHANNEL_NAME_IN = "mqttInboundChannel";
    /**
     * 发布的bean名称
     */
    public static final String CHANNEL_NAME_OUT = "mqttOutboundChannel";
    public static final String CHANNEL_REPLY = "mqttReplyBoundChannel";




    @Value("${register.type:zookeeper}")
    private String registerType;
    @Value("${register.url:}")
    private String registerUrl;
    @Value("${register.serverName:" + ServerName.AUTH_PLATFORM + "}")
    private String registerServerName;
    @Value("${register.version:1.0.0}")
    private String registerServerVersion;




    @Value("${register.MsgType:mqtt}")
    private String MsgType;

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
//    @Autowired
//    ServiceRegistry serviceRegistry;
    @Autowired
ServiceScanner localServiceScanner;
    @Autowired
    ServiceScanner jarServiceScanner;

//    @Autowired
//    @Qualifier("mqttHandlerAck")
//    private MqttHandler mqttHandlerAck;
//    private MqttHandler mqttHandlerImpl;
//    @Autowired(required = false)
//    @Qualifier("mqttHandlerImpl")
//    public void setMqttHandler(MqttHandler mqttHandler) {
//        this.mqttHandlerImpl = mqttHandler;
//    }


    @Value("${server.MsgBus:mqtt}")
    private String  MsgBusType;

    WebPrivFuncConfig webPrivFuncConfig;
    // 向注册中心注册功能列表
    @Autowired
    public void setWebPrivFuncConfig(WebPrivFuncConfig webPrivFuncConfig) {
        this.webPrivFuncConfig = webPrivFuncConfig;
    }
    public WebPrivFuncConfig getWebPrivFuncConfig() {
        return webPrivFuncConfig;
    }
    private OperaRouteConfig operaRouteConfig;
    @Autowired
    public void setOperaRouteConfig(OperaRouteConfig operaRouteConfig) {
        this.operaRouteConfig = operaRouteConfig;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }


    public String getMsgBusType() {
        return MsgBusType;
    }

    public void setMsgBusType(String msgBusType) {
        MsgBusType = msgBusType;
    }

    public String getServerMark() {
        return serverMark;
    }

    public void setServerMark(String serverMark) {
        this.serverMark = serverMark;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getRegisterType() {
        return registerType;
    }

    public void setRegisterType(String registerType) {
        this.registerType = registerType;
    }
    public String getRegisterUrl() {
        return registerUrl;
    }

    public void setRegisterUrl(String registerUrl) {
        this.registerUrl = registerUrl;
    }

    public String getRegisterServerName() {
        return registerServerName;
    }

    public void setRegisterServerName(String registerServerName) {
        this.registerServerName = registerServerName;
    }

    public String getRegisterServerVersion() {
        return registerServerVersion;
    }

    public void setRegisterServerVersion(String registerServerVersion) {
        this.registerServerVersion = registerServerVersion;
    }

    public String getDevFlag() {
        return devFlag;
    }

    public void setDevFlag(String devFlag) {
        this.devFlag = devFlag;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getServerUri() {
        return serverUri;
    }

    public void setServerUri(String serverUri) {
        this.serverUri = serverUri;
    }

    public String getPageRoute() {
        return pageRoute;
    }

    public void setPageRoute(String pageRoute) {
        this.pageRoute = pageRoute;
    }

    public String getRouteMark() {
        return routeMark;
    }

    public void setRouteMark(String routeMark) {
        this.routeMark = routeMark;
    }



    public ServiceScanner getLocalServiceScanner() {
        return localServiceScanner;
    }


    public String getMsgType() {
        return MsgType;
    }

    public void setMsgType(String msgType) {
        MsgType = msgType;
    }


    public void setLocalServiceScanner(ServiceScanner localServiceScanner) {
        this.localServiceScanner = localServiceScanner;
    }

    public ServiceScanner getJarServiceScanner() {
        return jarServiceScanner;
    }

    public void setJarServiceScanner(ServiceScanner jarServiceScanner) {
        this.jarServiceScanner = jarServiceScanner;
    }

    public int getRegSessionTimeout() {
        return regSessionTimeout;
    }

    public void setRegSessionTimeout(int regSessionTimeout) {
        this.regSessionTimeout = regSessionTimeout;
    }

    //    public MqttHandler getMqttHandlerAck() {
//        return mqttHandlerAck;
//    }
//
//    public void setMqttHandlerAck(MqttHandler mqttHandlerAck) {
//        this.mqttHandlerAck = mqttHandlerAck;
//    }
//
//    public MqttHandler getMqttHandlerImpl() {
//        return mqttHandlerImpl;
//    }
//
//    public void setMqttHandlerImpl(MqttHandler mqttHandlerImpl) {
//        this.mqttHandlerImpl = mqttHandlerImpl;
//    }

    public OperaRouteConfig getOperaRouteConfig() {
        return operaRouteConfig;
    }

}

package tech.mystox.framework.config;

import tech.mystox.framework.config.autoconfigure.*;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class IaConf {


    private String myId = UUID.randomUUID().toString();
    final OperaRouteProperties operaRouteProperties;
    final ServerProperties serverProperties;
    final RegisterProperties registerProperties;
    final WebPrivFuncConfig webPrivFuncConfig;
    Properties properties;

    public IaConf(RegisterProperties registerProperties, ServerProperties serverProperties,
                  OperaRouteProperties operaRouteProperties,
                  WebPrivFuncConfig webPrivFuncConfig) {
        this.operaRouteProperties = operaRouteProperties;
        this.serverProperties = serverProperties;
        this.registerProperties = registerProperties;
        this.webPrivFuncConfig = webPrivFuncConfig;
    }


    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }


    public enum LoadBalanceType {
        BASE, RANDOM, RETRY, BAEST_AVAILABLE, AVAILABILITY_FILTERING, RESPONSE_TIME_WEIGHTED, ZONE_AVOIDANCE;
    }

    private Long sequence;


    public WebPrivFuncConfig getWebPrivFuncConfig() {
        return webPrivFuncConfig;
    }

    public Map<String, Object> getExtensionConfig() {
        return registerProperties.getExtension();
    }




    public String[] getWebExtension() {
        return registerProperties.getWebExtension().toArray(new String[0]);
    }


    public String getLoadBalancerType() {
        return registerProperties.getBalancer();
    }

    public String getServerName() {
        return serverProperties.getName();
    }

    public String getServerVersion() {
        return serverProperties.getVersion();
    }

    public String getMsgBusType() {
        return serverProperties.getMsgBus();
    }

    public String getServerMark() {
        return serverProperties.getMark();
    }

    public String getGroupCode() {
        return serverProperties.getGroupCode();
    }

    public String getRegisterType() {
        return registerProperties.getType();
    }


    public String getRegisterUrl() {
        return registerProperties.getUrl();
    }

    public String getHost() {
        return serverProperties.getHost();
    }


    public int getPort() {
        return serverProperties.getPort();
    }

    public String getTitle() {
        return serverProperties.getTitle();
    }

    public String getServerUri() {
        return serverProperties.getServerUri();
    }

    public String getPageRoute() {
        return serverProperties.getPageRoute();
    }


    public String getRouteMark() {
        return serverProperties.getRouteMark();
    }


    public String getMsgType() {
        return registerProperties.getMsgType();
    }


    public boolean isDuplicate() {
        return registerProperties.isDuplicate();
    }


    public int getRegSessionTimeout() {
        return registerProperties.getSessionTimeout();
    }

    public Properties getMqMsgProperties() {
        return this.properties;
    }
    public void setMqMsgProperties(Properties properties) {
        this.properties = properties;
    }

    //public void setMqMsgProperties(Properties mqMsgProperties) {
    //    this.mqMsgProperties = mqMsgProperties;
    //}

    public OperaRouteProperties getOperaRouteConfig() {
        return operaRouteProperties;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public String scanBasePackage() {
        return this.registerProperties.getScanBasePackage();
    }

}

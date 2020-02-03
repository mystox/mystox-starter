package com.kongtrolink.framework.gateway.tower.server.service.transverter;

import com.chinatowercom.scservice.InvokeResponse;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.service.MqttOpera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by mystoxlol on 2019/10/16, 15:25.
 * company: kongtrolink
 * description:
 * update record:
 */
public abstract class TransverterHandler implements TransverterService {

    @Value("${gateway.businessCode}")
    private String businessCode; //必须配置
    @Value("${gateway.enterpriseCode}")
    private String enterpriseCode; //必须配置
    @Value("${gateway.deviceType:null}")
    private String deviceType;
    @Value("${gateway.deviceModel:null}")
    private String deviceModel;
    @Value("${gateway.regionCode:null}")
    private String regionCode;
    @Value("${gateway.user:null}")
    private String user;
    @Value("${gateway.fsuType:38}")
    private String fsuType;
    @Value("${server.name}")
    private String serverName;
    @Value("${server.version}")
    private String serverVersion;
    @Value("${server.host}")
    private String serverHost;
    @Value("${gateway.fsuOffAlarmName}")
    private String fsuOffAlarmName;
    @Value("${gateway.fsuOffAlarmLevel}")
    private String fsuOffAlarmLevel;
    @Autowired
    MqttOpera mqttOpera;

    protected void reportMsg(String serverCode, String operaCode, String payload) {
//        mqttSender.sendToMqtt(serverCode,operaCode,payload);
        mqttOpera.broadcast(operaCode, payload);
    }

    protected MsgResult reportMsgSyn(String serverCode, String operaCode, String payload) {
        return mqttOpera.opera(operaCode, payload);
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getFsuType() {
        return fsuType;
    }

    public void setFsuType(String fsuType) {
        this.fsuType = fsuType;
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public String getFsuOffAlarmName() {
        return fsuOffAlarmName;
    }

    public void setFsuOffAlarmName(String fsuOffAlarmName) {
        this.fsuOffAlarmName = fsuOffAlarmName;
    }

    public String getFsuOffAlarmLevel() {
        return fsuOffAlarmLevel;
    }

    public void setFsuOffAlarmLevel(String fsuOffAlarmLevel) {
        this.fsuOffAlarmLevel = fsuOffAlarmLevel;
    }

    public void offlineAlarmEnd(String sn){

    }
}

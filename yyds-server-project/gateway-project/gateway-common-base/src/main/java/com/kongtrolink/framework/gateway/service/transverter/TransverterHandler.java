package com.kongtrolink.framework.gateway.service.transverter;

import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.gateway.entity.ParseProtocol;
import com.kongtrolink.framework.service.MqttSender;
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
    @Autowired
    MqttSender mqttSender;


    public void transfer(ParseProtocol parseProtocol){
        transferExecute(parseProtocol);
    }

    protected abstract void transferExecute(ParseProtocol parseProtocol);

    protected void reportMsg(String serverCode,String operaCode,String payload) {
        mqttSender.sendToMqtt(serverCode,operaCode,payload);
    }

    protected MsgResult reportMsgSyn(String serverCode, String operaCode, String payload) {
        return mqttSender.sendToMqttSyn(serverCode,operaCode,payload);
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
}

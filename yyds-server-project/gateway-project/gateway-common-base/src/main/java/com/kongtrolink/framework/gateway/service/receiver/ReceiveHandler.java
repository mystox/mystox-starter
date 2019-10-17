package com.kongtrolink.framework.gateway.service.receiver;

import com.kongtrolink.framework.gateway.service.parse.ParseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by mystoxlol on 2019/10/16, 9:11.
 * company: kongtrolink
 * description:
 * update record:
 */
public abstract class ReceiveHandler implements ReceiveService {
    @Value("${gateway.businessCode}")
    private String businessCode; //必须配置
    @Value("${gateway.enterpriseCode}")
    private String enterpriseCode; //必须配置
    @Value("${gateway.port:9999}")
    private int port; //接收器端口

    @Value("${gateway.deviceType:null}")
    private String deviceType;
    @Value("${gateway.deviceModel:null}")
    private String deviceModel;
    @Value("${gateway.regionCode:null}")
    private String regionCode;

    @Autowired
    ParseService parseService;


    public ReceiveHandler port(int port) {
        setPort(port);
        return this;
    }

    public ReceiveHandler deviceType(String deviceType) {
        setDeviceType(deviceType);
        return this;
    }


    /**
     * 接收方法案例，使用parseService调用解析器对报文进行非标协议解析
     */
    void receive() {
        String payload;
        parseService.execute("");
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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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

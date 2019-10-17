package com.kongtrolink.framework.gateway.service.parse;

import org.springframework.beans.factory.annotation.Value;

/**
 * Created by mystoxlol on 2019/10/16, 9:11.
 * company: kongtrolink
 * description:
 * update record:
 */
public abstract class ParseHandler implements ParseService {
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

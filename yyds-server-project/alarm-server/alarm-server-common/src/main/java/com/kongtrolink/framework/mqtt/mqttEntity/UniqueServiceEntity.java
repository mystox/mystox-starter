package com.kongtrolink.framework.mqtt.mqttEntity;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/25 18:16
 * @Description:企业封装类
 */
public class UniqueServiceEntity {

    private String title;
    private String enterpriseCode;
    private List<ServerEntity> serverCodes;

    public UniqueServiceEntity(String title, String enterpriseCode) {
        this.title = title;
        this.enterpriseCode = enterpriseCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public List<ServerEntity> getServerCodes() {
        return serverCodes;
    }

    public void setServerCodes(List<ServerEntity> serverCodes) {
        this.serverCodes = serverCodes;
    }
}

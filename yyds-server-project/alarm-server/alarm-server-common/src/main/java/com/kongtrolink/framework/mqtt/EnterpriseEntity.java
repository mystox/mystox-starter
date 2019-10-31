package com.kongtrolink.framework.mqtt;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/10/16 09:30
 * @Description:
 */
public class EnterpriseEntity {

    private String title;       //企业名称
    private String enterpriseCode;
    private List<ServerEntity> serverCodes;

    public EnterpriseEntity(String title, String enterpriseCode) {
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

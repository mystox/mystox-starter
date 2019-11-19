package com.kongtrolink.framework.reports.entity;

/**
 * Created by mystoxlol on 2019/11/5, 10:56.
 * company: kongtrolink
 * description: 报表配置页面的json数据保存实体
 * update record:
 */
public class ReportWebConfig {
    private String id;
    private String serverCode;
    private String enterpriseCode;
    private String configData;

    public ReportWebConfig() {
    }

    public ReportWebConfig(String serverCode, String enterpriseCode, String configData) {
        this.serverCode = serverCode;
        this.enterpriseCode = enterpriseCode;
        this.configData = configData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public String getConfigData() {
        return configData;
    }

    public void setConfigData(String configData) {
        this.configData = configData;
    }
}

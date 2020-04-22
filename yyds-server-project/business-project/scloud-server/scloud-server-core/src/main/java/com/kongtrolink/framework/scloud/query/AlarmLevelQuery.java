package com.kongtrolink.framework.scloud.query;

/**
 * @Auther: liudd
 * @Date: 2020/4/3 08:32
 * @Description:
 */
public class AlarmLevelQuery {

    private String enterpriseCode;
    private String serverCode;

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }
}

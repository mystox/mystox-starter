package com.kongtrolink.framework.enttiy;


/**
 * @Auther: liudd
 * @Date: 2019/10/29 16:50
 * @Description:
 */
public class Operate {
    private String enterpriseCode;
    private String serverCode;
    private String serverVerson;
    private String operaCode;

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

    public String getServerVerson() {
        return serverVerson;
    }

    public void setServerVerson(String serverVerson) {
        this.serverVerson = serverVerson;
    }

    public String getOperaCode() {
        return operaCode;
    }

    public void setOperaCode(String operaCode) {
        this.operaCode = operaCode;
    }

    @Override
    public String toString() {
        return "Operate{" +
                "enterpriseCode='" + enterpriseCode + '\'' +
                ", serverCode='" + serverCode + '\'' +
                ", serverVerson='" + serverVerson + '\'' +
                ", operaCode='" + operaCode + '\'' +
                '}';
    }
}

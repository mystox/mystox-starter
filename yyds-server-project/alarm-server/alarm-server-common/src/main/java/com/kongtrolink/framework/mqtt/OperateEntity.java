package com.kongtrolink.framework.mqtt;

/**
 * @Auther: liudd
 * @Date: 2019/10/30 14:39
 * @Description:
 */
public class OperateEntity {

    private String enterServerCode;
    private String serverVerson;
    private String operaCode;
    private String desc;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getEnterServerCode() {
        return enterServerCode;
    }

    public void setEnterServerCode(String enterServerCode) {
        this.enterServerCode = enterServerCode;
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
}

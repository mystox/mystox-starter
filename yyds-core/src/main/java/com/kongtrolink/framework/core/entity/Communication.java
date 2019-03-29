package com.kongtrolink.framework.core.entity;

/**
 * @Auther: liudd
 * @Date: 2019/3/28 16:30
 * @Description:通讯信息实体类
 */
public class Communication {

    private String id;
    private String gwip;        //网关ip
    private String uuid;        //uuid
    private String bip;         //业务服务地址
    private int status;         //状态（0-未注册；1-已注册）

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGwip() {
        return gwip;
    }

    public void setGwip(String gwip) {
        this.gwip = gwip;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getBip() {
        return bip;
    }

    public void setBip(String bip) {
        this.bip = bip;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

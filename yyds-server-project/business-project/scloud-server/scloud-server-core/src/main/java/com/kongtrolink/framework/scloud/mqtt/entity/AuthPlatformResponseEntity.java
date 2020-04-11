package com.kongtrolink.framework.scloud.mqtt.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 从【云管】获取用户信息 响应数据实体类
 * Created by Eric on 2020/4/10.
 */
public class AuthPlatformResponseEntity {

    @JSONField(name = "info")
    private String info;
    @JSONField(name = "message")
    private String message;
    @JSONField(name = "success")
    private Boolean success;
    @JSONField(name = "data")
    private String data;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

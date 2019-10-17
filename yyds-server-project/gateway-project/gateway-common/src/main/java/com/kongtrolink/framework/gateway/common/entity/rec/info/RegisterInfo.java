package com.kongtrolink.framework.gateway.common.entity.rec.info;


import com.alibaba.fastjson.annotation.JSONField;

/**
 * 注册信息
 * Created by Mag on 2019/10/14.
 */
public class RegisterInfo {

    @JSONField(name = "sn")
    private String sn;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }
}

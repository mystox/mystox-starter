package com.kongtrolink.framework.gateway.iaiot.core.rec;


import com.kongtrolink.framework.gateway.iaiot.core.rec.info.RegisterInfo;

/**
 * 注册报文
 * Created by Mag on 2019/10/14.
 */
public class Register extends RecBase {

    private static final long serialVersionUID = -2285543331107110904L;
    private RegisterInfo payload;

    public RegisterInfo getPayload() {
        return payload;
    }

    public void setPayload(RegisterInfo payload) {
        this.payload = payload;
    }
}

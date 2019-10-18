package com.kongtrolink.framework.gateway.tower.entity.rec.base;

import java.io.Serializable;

/**
 * 接收消息
 * Created by Mag on 2019/10/14.
 */
public class RecServerBase implements Serializable{

    private String sn;
    private String payload;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}

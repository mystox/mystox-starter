package com.kongtrolink.gateway.nb.cmcc.entity.iot;

import java.io.Serializable;

/**
 * xx
 * by Mag on 2019/2/14.
 */
public class AddDeviceAck implements Serializable {
    private static final long serialVersionUID = 7455680058497078675L;
    private String device_id;
    private String psk;

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getPsk() {
        return psk;
    }

    public void setPsk(String psk) {
        this.psk = psk;
    }
}

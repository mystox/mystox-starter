package com.kongtrolink.gateway.nb.cmcc.entity;

import java.io.Serializable;

/**
 * xx
 * by Mag on 2018/12/5.
 */
public class ProfileEntity implements Serializable {
    private static final long serialVersionUID = -8623635532361805300L;

    private String opid ;//报文长度
    private PackageData pdu;//报文内容

    public PackageData getPdu() {
        return pdu;
    }

    public void setPdu(PackageData pdu) {
        this.pdu = pdu;
    }

    public String getOpid() {
        return opid;
    }

    public void setOpid(String opid) {
        this.opid = opid;
    }
}

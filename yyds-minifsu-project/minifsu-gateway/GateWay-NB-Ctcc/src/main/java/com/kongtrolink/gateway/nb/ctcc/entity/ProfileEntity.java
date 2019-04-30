package com.kongtrolink.gateway.nb.ctcc.entity;

import java.io.Serializable;

/**
 * xx
 * by Mag on 2018/12/5.
 */
public class ProfileEntity implements Serializable {
    private static final long serialVersionUID = -8623635532361805300L;

    private int dataLen ;//报文长度
    private PackageData dataStr;//报文内容

    public PackageData getDataStr() {
        return dataStr;
    }

    public void setDataStr(PackageData dataStr) {
        this.dataStr = dataStr;
    }

    public int getDataLen() {
        return dataLen;
    }

    public void setDataLen(int dataLen) {
        this.dataLen = dataLen;
    }
}

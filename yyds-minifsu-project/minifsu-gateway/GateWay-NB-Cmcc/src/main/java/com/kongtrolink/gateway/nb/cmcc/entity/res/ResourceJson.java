package com.kongtrolink.gateway.nb.cmcc.entity.res;

import java.io.Serializable;

/**
 * xx
 * by Mag on 2019/2/19.
 */
public class ResourceJson implements Serializable {
    private static final long serialVersionUID = 5918550617135882790L;

    private String fsuid;
    private String imei;
    private long TimeStamp;

    public String getFsuid() {
        return fsuid;
    }

    public void setFsuid(String fsuid) {
        this.fsuid = fsuid;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        TimeStamp = timeStamp;
    }
}

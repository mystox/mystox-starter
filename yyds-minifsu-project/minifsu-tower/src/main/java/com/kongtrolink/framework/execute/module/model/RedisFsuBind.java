package com.kongtrolink.framework.execute.module.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fengw
 * redis中fsuId、sn和设备ID列表绑定信息
 * 新建文件 2019-4-17 19:28:55
 */
public class RedisFsuBind {
    private String fsuId;
    private String sn;
    private List<String> deviceIdList;

    public RedisFsuBind() {
        this.deviceIdList = new ArrayList<>();
    }

    public String getFsuId() {
        return fsuId;
    }

    public void setFsuId(String fsuId) {
        this.fsuId = fsuId;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public List<String> getDeviceIdList() {
        return deviceIdList;
    }

    public void setDeviceIdList(List<String> deviceIdList) {
        this.deviceIdList = deviceIdList;
    }
}

package com.kongtrolink.framework.scloud.entity.multRoom;

import java.io.Serializable;
import java.util.List;

/**
 * 综合机房 自定义设置 以设备ID为主键
 * by Mag on 2018/11/12.
 */
public class RoomSignalTypeConfig implements Serializable{

    private static final long serialVersionUID = 477673618826699795L;

    private String id;
    private String deviceId;
    private List<RoomSignalTypeDevice> signals;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public List<RoomSignalTypeDevice> getSignals() {
        return signals;
    }

    public void setSignals(List<RoomSignalTypeDevice> signals) {
        this.signals = signals;
    }
}

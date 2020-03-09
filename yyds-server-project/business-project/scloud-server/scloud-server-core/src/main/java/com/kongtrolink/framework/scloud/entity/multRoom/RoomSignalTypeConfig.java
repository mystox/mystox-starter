package com.kongtrolink.framework.scloud.entity.multRoom;

import com.kongtrolink.framework.scloud.base.GeneratedValue;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;

/**
 * 综合机房 自定义设置 以设备ID为主键
 * by Mag on 2018/11/12.
 */
public class RoomSignalTypeConfig implements Serializable{

    private static final long serialVersionUID = 477673618826699795L;

    @Field(value = "id")
    @GeneratedValue
    private int id; //主键ID
    private int deviceId;
    private List<RoomSignalTypeDevice> signals;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public List<RoomSignalTypeDevice> getSignals() {
        return signals;
    }

    public void setSignals(List<RoomSignalTypeDevice> signals) {
        this.signals = signals;
    }
}

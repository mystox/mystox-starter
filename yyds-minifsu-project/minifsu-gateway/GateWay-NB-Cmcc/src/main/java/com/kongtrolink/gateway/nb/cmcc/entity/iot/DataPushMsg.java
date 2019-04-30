package com.kongtrolink.gateway.nb.cmcc.entity.iot;

import java.io.Serializable;

/**
 * 推送消息返回 消息
 * by Mag on 2019/2/15.
 */
public class DataPushMsg implements Serializable{
    private static final long serialVersionUID = -111148047004338742L;
    /**
     *  1：设备上传数据点消息
        2：设备上下线消息
        7：缓存命令下发后结果上报（仅支持NB设备）
     */
    private int type;
    private String imei;
    private int dev_id; //设备ID
    private String ds_id;//数据流名称
    private long at;//平台时间戳,单位ms
    private Object value; //具体数据部分，为设备上传至平台或触发的相关数据
    /**
     * 设备上下线标识
     0：设备下线
     1：设备上线
     */
    private int status;
    /**
     * 设备登陆协议类型
     * 1-EDP, 6-MODBUS, 7-MQTT, 10-NB-IoT
     */
    private int login_type;
    /**
     * 命令响应的类型
     * 1：设备收到cmd的ACK响应信息
       2：设备收到cmd的Confirm响应信息
     */
    private int	cmd_type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDev_id() {
        return dev_id;
    }

    public void setDev_id(int dev_id) {
        this.dev_id = dev_id;
    }

    public String getDs_id() {
        return ds_id;
    }

    public void setDs_id(String ds_id) {
        this.ds_id = ds_id;
    }

    public long getAt() {
        return at;
    }

    public void setAt(long at) {
        this.at = at;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getLogin_type() {
        return login_type;
    }

    public void setLogin_type(int login_type) {
        this.login_type = login_type;
    }

    public int getCmd_type() {
        return cmd_type;
    }

    public void setCmd_type(int cmd_type) {
        this.cmd_type = cmd_type;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }



    @Override
    public String toString() {
        return "DataPushMsg{" +
                "type=" + type +
                ", imei=" + imei +
                ", dev_id=" + dev_id +
                ", ds_id=" + ds_id +
                ", at=" + at +
                ", value=" + value +
                ", status=" + status +
                ", login_type=" + login_type +
                ", cmd_type=" + cmd_type +
                '}';
    }
}

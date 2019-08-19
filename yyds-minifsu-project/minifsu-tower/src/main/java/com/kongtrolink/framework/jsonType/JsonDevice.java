package com.kongtrolink.framework.jsonType;

/**
 * @author fengw
 * 铁塔设备ID列表信息
 * 新建文件 2019-4-16 18:15:34
 */
public class JsonDevice {
    //fsuId
    private String fsuId;
    //铁塔设备ID
    private String deviceId;
    //端口
    private String port;
    //设备类型
    private int type;
    //资源编号
    private int resNo;

    /**
     * 默认构造函数
     */
    public JsonDevice() {
    }

    /**
     * 构造函数
     * @param fsuId fsuId
     * @param deviceId 铁塔设备id
     */
    public JsonDevice(String fsuId, String deviceId) {
        this.fsuId = fsuId;
        this.deviceId = deviceId;
    }

    /**
     * 构造函数
     * @param deviceInfo 内部上报设备信息
     */
    public JsonDevice(String deviceInfo) {
        String[] infos = deviceInfo.split("-");
        this.type = Integer.parseInt(infos[0]);
        this.port = infos[1];
        this.resNo = Integer.parseInt(infos[3]);
    }

    public String getFsuId() {
        return fsuId;
    }

    public void setFsuId(String fsuId) {
        this.fsuId = fsuId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getResNo() {
        return resNo;
    }

    public void setResNo(int resNo) {
        this.resNo = resNo;
    }
}

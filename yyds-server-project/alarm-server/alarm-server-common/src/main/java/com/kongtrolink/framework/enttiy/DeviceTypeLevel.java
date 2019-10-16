package com.kongtrolink.framework.enttiy;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/20 10:03
 * @Description:企业，服务，设备型号拥有告警等级数量
 */
public class DeviceTypeLevel {
    private String id;
    private String enterpriseCode;
    private String enterpriseName;
    private String serverCode;
    private String serverName;
    private String deviceType;      //设备类型
    private String deviceModel;     //设备型号
    private List<String> levels;    //设备等级列表

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public List<String> getLevels() {
        return levels;
    }

    public void setLevels(List<String> levels) {
        this.levels = levels;
    }
}

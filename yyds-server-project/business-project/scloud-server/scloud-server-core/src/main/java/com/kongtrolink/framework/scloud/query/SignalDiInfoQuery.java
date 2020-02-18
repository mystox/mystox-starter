package com.kongtrolink.framework.scloud.query;

import java.util.List;

/**
 * 遥测信号点统计表 查询表
 * Created by Mg on 2018/5/25.
 */
public class SignalDiInfoQuery extends Paging{

    private String systemName;//所属系统

    private String deviceName;//设备名称

    private String deviceCode;//设备编码

    private String signalCode;//遥测信号值

    private List<String> tierCodes;// 区域codes 查询一个区域选的

    private List<String> siteIds2;//站点IDs 选择了部分站点
    private String typeCode;//设备类型

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getSignalCode() {
        return signalCode;
    }

    public void setSignalCode(String signalCode) {
        this.signalCode = signalCode;
    }


    public List<String> getTierCodes() {
        return tierCodes;
    }

    public void setTierCodes(List<String> tierCodes) {
        this.tierCodes = tierCodes;
    }

    public List<String> getSiteIds2() {
        return siteIds2;
    }

    public void setSiteIds2(List<String> siteIds2) {
        this.siteIds2 = siteIds2;
    }
}

package com.kongtrolink.framework.scloud.entity.multRoom;

import java.io.Serializable;

/**
 * 设备列表 详细信息
 * 最多显示3个值  2个AI 1个DO
 * by Mag on 6/21/2018.
 */
public class RoomDeviceInfo implements Serializable{

    private int deviceId;//设备ID
    private String deviceCode;//设备code
    private String deviceName;//设备名称
    private String status;//状态:(在线,离线,告警)
    private String doValue;//值1 (存DO值)
    private String doSinId;//值1 (存DO值)对应的信号点ID
    private String doSinName;//信号点名称
    private String aiValue1;//值2 (存AI值1)
    private String aiSinId1;//值2 (存AI值1)对应的信号点ID
    private String aiUnit1;//值2 单位
    private String aiName1;//值2 名称
    private String aiValue2;//值3 (存AI值2)
    private String aiSinId2;//值3 (存AI值2)对应的信号点ID
    private String aiUnit2;//值2 单位
    private String aiName2;//值3 名称

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDoValue() {
        return doValue;
    }

    public void setDoValue(String doValue) {
        this.doValue = doValue;
    }

    public String getAiValue1() {
        return aiValue1;
    }

    public void setAiValue1(String aiValue1) {
        this.aiValue1 = aiValue1;
    }

    public String getAiValue2() {
        return aiValue2;
    }

    public void setAiValue2(String aiValue2) {
        this.aiValue2 = aiValue2;
    }

    public String getDoSinId() {
        return doSinId;
    }

    public void setDoSinId(String doSinId) {
        this.doSinId = doSinId;
    }

    public String getAiSinId1() {
        return aiSinId1;
    }

    public void setAiSinId1(String aiSinId1) {
        this.aiSinId1 = aiSinId1;
    }

    public String getAiSinId2() {
        return aiSinId2;
    }

    public void setAiSinId2(String aiSinId2) {
        this.aiSinId2 = aiSinId2;
    }

    public String getAiUnit2() {
        return aiUnit2;
    }

    public void setAiUnit2(String aiUnit2) {
        this.aiUnit2 = aiUnit2;
    }

    public String getAiUnit1() {
        return aiUnit1;
    }

    public void setAiUnit1(String aiUnit1) {
        this.aiUnit1 = aiUnit1;
    }

    public String getDoSinName() {
        return doSinName;
    }

    public void setDoSinName(String doSinName) {
        this.doSinName = doSinName;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getAiName1() {
        return aiName1;
    }

    public void setAiName1(String aiName1) {
        this.aiName1 = aiName1;
    }

    public String getAiName2() {
        return aiName2;
    }

    public void setAiName2(String aiName2) {
        this.aiName2 = aiName2;
    }
}

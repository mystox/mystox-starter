package com.kongtrolink.framework.gateway.tower.entity.alarm;

import java.io.Serializable;

/**
 * 3.6.1.6.1.1.	上报告警消息
 * Created by Mag on 2019/10/16.
 */
public class AlarmReportInfo implements Serializable{


    private static final long serialVersionUID = -6669875502380871244L;

    private String name; 			//告警名称
    private String value;			//告警值
    private String level;			//告警等级
    /**
     * sn的规则，如果接入为FSU终端，则上报该终端和该终端所连接设备信息，
     *  应该为终端SN_所接设备编码NUM 组成如果接入终端为单设备接入，则以接入的唯一识别码为SN",
     */
    private String deviceId;		//设备id，即SN
    private String deviceType;		//设备类型，与资管一致
    private String deviceModel;	    //设备型号，如果没有与deviceType一致
    private String signalId;		//信号点id必须有，如果消息报文不包含信号点，则需要根据业务定义相关信号点。以配置形式放置服务
    private int flag;               //告警标志（0：结束，1：开始
    private String serial;			//序列号，服务记录这个告警点产生几次，以自增的方式设置序列号。



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getSignalId() {
        return signalId;
    }

    public void setSignalId(String signalId) {
        this.signalId = signalId;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}

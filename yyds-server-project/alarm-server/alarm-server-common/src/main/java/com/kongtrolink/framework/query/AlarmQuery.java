package com.kongtrolink.framework.query;

import com.kongtrolink.framework.base.Paging;
import com.kongtrolink.framework.enttiy.Alarm;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther: liudd
 * @Date: 2019/9/11 14:44
 * @Description:
 */
public class AlarmQuery extends Paging {

    private String id;
    private String enterpriseCode;              //企业编码
    private String serverCode;                  //服务编码
    private String name;                        //告警名称
    private String deviceType;                  //设备型号
    private String deviceModel;                 //设备类型
    private String state;                       //告警状态
    private String targetLevelName;             //目标等级名称
    private String type;                        //告警类型（实时告警/历史告警）
    private Date startBeginTime;                //发生开始时间
    private Date startEndTime;                  //发生结束时间
    private Date clearBeginTime;                //清除开始时间
    private Date clearEndTime;                  //清除结束时间

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTargetLevelName() {
        return targetLevelName;
    }

    public void setTargetLevelName(String targetLevelName) {
        this.targetLevelName = targetLevelName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getStartBeginTime() {
        return startBeginTime;
    }

    public void setStartBeginTime(Date startBeginTime) {
        this.startBeginTime = startBeginTime;
    }

    public Date getStartEndTime() {
        return startEndTime;
    }

    public void setStartEndTime(Date startEndTime) {
        this.startEndTime = startEndTime;
    }

    public Date getClearBeginTime() {
        return clearBeginTime;
    }

    public void setClearBeginTime(Date clearBeginTime) {
        this.clearBeginTime = clearBeginTime;
    }

    public Date getClearEndTime() {
        return clearEndTime;
    }

    public void setClearEndTime(Date clearEndTime) {
        this.clearEndTime = clearEndTime;
    }
}

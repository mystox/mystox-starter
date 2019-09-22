package com.kongtrolink.enttiy;

import com.kongtrolink.base.FacadeView;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/11 14:59
 * @Description:
 */
public class AlarmLevel {

    private String id;                      //id
    private String uniqueCode;              //所属企业
    private String service;                 //服务
    private String deviceName;              //设备类型名称
    private String deviceType;              //设备型号
//    private List<String> sourceLevelList;   //原等级列表字符串（12345），不添加“，”号等分隔符
    private String sourceLevel;             //源等级
    private String targetLevel;             //目标等级，一条自定义只包含一个目标等级，否则不好判定目标等级重复定义
    private String color;                   //告警颜色，在确定目标等级时，确定颜色
    private Date updateTime;                   //创建时间
    private FacadeView creator;             //创建者

    public String getSourceLevel() {
        return sourceLevel;
    }

    public void setSourceLevel(String sourceLevel) {
        this.sourceLevel = sourceLevel;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public FacadeView getCreator() {
        return creator;
    }

    public void setCreator(FacadeView creator) {
        this.creator = creator;
    }

    public String getTargetLevel() {
        return targetLevel;
    }

    public void setTargetLevel(String targetLevel) {
        this.targetLevel = targetLevel;
    }
}

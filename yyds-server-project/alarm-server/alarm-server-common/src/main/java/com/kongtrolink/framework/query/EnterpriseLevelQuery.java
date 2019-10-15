package com.kongtrolink.framework.query;

import com.kongtrolink.framework.base.Paging;
import com.kongtrolink.framework.enttiy.DeviceTypeLevel;

import java.util.Date;

/**
 * @Auther: liudd
 * @Date: 2019/9/20 10:44
 * @Description:
 */
public class EnterpriseLevelQuery extends Paging{

    private String id;
    private String uniqueCode;
    private String service;
    private String level;
    private String color;
    private Date beginTime;
    private Date endTime;
    private String defaultLevel;       //默认告警等级

    public String getDefaultLevel() {
        return defaultLevel;
    }

    public void setDefaultLevel(String defaultLevel) {
        this.defaultLevel = defaultLevel;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public static EnterpriseLevelQuery deviceLevel2EnterpriseLevel(DeviceTypeLevel deviceTypeLevel){
        if(null == deviceTypeLevel){
            return null;
        }
        EnterpriseLevelQuery enterpriseLevelQuery = new EnterpriseLevelQuery();
        enterpriseLevelQuery.setUniqueCode(deviceTypeLevel.getUniqueCode());
        enterpriseLevelQuery.setService(deviceTypeLevel.getService());
        enterpriseLevelQuery.setLevel(deviceTypeLevel.getLevel());
        return enterpriseLevelQuery;
    }
}

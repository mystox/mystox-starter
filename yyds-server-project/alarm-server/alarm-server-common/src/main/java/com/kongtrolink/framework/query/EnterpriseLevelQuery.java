package com.kongtrolink.framework.query;

import com.kongtrolink.framework.base.Paging;
import com.kongtrolink.framework.enttiy.DeviceTypeLevel;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/20 10:44
 * @Description:
 */
public class EnterpriseLevelQuery extends Paging{

    private String id;
    private String code;
    private String name;
    private List<String> codeList;
    private String enterpriseCode;
    private String enterpriseName;
    private String serverCode;
    private String serverName;
    private Integer level;
    private String color;
    private Date beginTime;
    private Date endTime;
    private String operatorName;        //操作用户姓名
    private String state;                           //启用，禁用

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getCodeList() {
        return codeList;
    }

    public void setCodeList(List<String> codeList) {
        this.codeList = codeList;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

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

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
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
        enterpriseLevelQuery.setEnterpriseCode(deviceTypeLevel.getEnterpriseCode());
        enterpriseLevelQuery.setServerCode(deviceTypeLevel.getServerCode());
//        enterpriseLevelQuery.setLevel(deviceTypeLevel.getLevel());
        return enterpriseLevelQuery;
    }
}
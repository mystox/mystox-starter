package com.kongtrolink.framework.enttiy;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.FacadeView;
import com.kongtrolink.framework.base.StringUtil;

import java.util.Date;

/**
 * @Auther: liudd
 * @Date: 2019/10/23 14:25
 * @Description:消息模板
 */
public class MsgTemplate {
    private String _id;
    private String name;                //名称
    private String enterpriseCode;      //企业编码
    private String enterpriseName;      //企业名称
    private String serverCode;          //服务编码
    private String serverName;          //服务名称
    private String templateType;        //模板类型（系统/手动）
    private String type;                //消息类型（短信，邮件，APP）
    private String url;                 //接口地址
    private String serverVerson;        //服务和版本
    private String operaCode;           //服务操作码(服务码#操作码)
    private String reportCode;          //告警上报编码
    private String reportModel;         //告警上报模板
    private String resolveCode;         //告警消除编码
    private String resolveModel;        //告警消除模板
    private String offlineCode;         //fsu离线编码
    private String offlineModel;        //fsu离线模板
    private FacadeView operator;
    private Date updateTime;

    public String getServerVerson() {
        return serverVerson;
    }

    public void setServerVerson(String serverVerson) {
        this.serverVerson = serverVerson;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public String getOperaCode() {
        return operaCode;
    }

    public void setOperaCode(String operaCode) {
        this.operaCode = operaCode;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public FacadeView getOperator() {
        return operator;
    }

    public void setOperator(FacadeView operator) {
        this.operator = operator;
    }
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getReportCode() {
        return reportCode;
    }

    public void setReportCode(String reportCode) {
        this.reportCode = reportCode;
    }

    public String getReportModel() {
        return reportModel;
    }

    public void setReportModel(String reportModel) {
        this.reportModel = reportModel;
    }

    public String getResolveCode() {
        return resolveCode;
    }

    public void setResolveCode(String resolveCode) {
        this.resolveCode = resolveCode;
    }

    public String getResolveModel() {
        return resolveModel;
    }

    public void setResolveModel(String resolveModel) {
        this.resolveModel = resolveModel;
    }

    public String getOfflineCode() {
        return offlineCode;
    }

    public void setOfflineCode(String offlineCode) {
        this.offlineCode = offlineCode;
    }

    public String getOfflineModel() {
        return offlineModel;
    }

    public void setOfflineModel(String offlineModel) {
        this.offlineModel = offlineModel;
    }

    @Override
    public String toString() {
        return "MsgTemplate{" +
                "name='" + name + '\'' +
                ", enterpriseCode='" + enterpriseCode + '\'' +
                ", enterpriseName='" + enterpriseName + '\'' +
                ", serverCode='" + serverCode + '\'' +
                ", serverName='" + serverName + '\'' +
                ", templateType='" + templateType + '\'' +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", serverVerson='" + serverVerson + '\'' +
                ", operaCode='" + operaCode + '\'' +
                ", reportCode='" + reportCode + '\'' +
                ", resolveCode='" + resolveCode + '\'' +
                ", offlineCode='" + offlineCode + '\'' +
                ", operator=" + operator +
                '}';
    }
}

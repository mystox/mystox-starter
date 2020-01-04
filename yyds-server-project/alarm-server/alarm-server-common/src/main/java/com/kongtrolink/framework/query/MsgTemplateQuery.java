package com.kongtrolink.framework.query;

import com.kongtrolink.framework.base.Paging;

/**
 * @Auther: liudd
 * @Date: 2019/10/23 14:31
 * @Description:
 */
public class MsgTemplateQuery extends Paging {
    private String _id;
    private String name;                //名称
    private String enterpriseCode;      //企业编码
    private String enterpriseName;      //企业名称
    private String serverCode;          //服务编码
    private String serverName;          //服务名称
    private String type;                //消息类型（短信，邮件，APP）
    private String url;                 //接口地址
    private String reportCode;          //告警上报编码
    private String resolveCode;         //告警消除编码
    private String offlineCode;         //fsu离线编码

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

    public String getResolveCode() {
        return resolveCode;
    }

    public void setResolveCode(String resolveCode) {
        this.resolveCode = resolveCode;
    }

    public String getOfflineCode() {
        return offlineCode;
    }

    public void setOfflineCode(String offlineCode) {
        this.offlineCode = offlineCode;
    }
}
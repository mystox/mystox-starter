package com.kongtrolink.framework.reports.entity;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by mystoxlol on 2019/10/23, 9:41.
 * company: kongtrolink
 * description:
 * update record:
 */
public class ReportConfig {


    private String operaCode;

    private String serverCode;

    private String enterpriseCode;

    private Long operaValidity;

    private String reportType; //与任务类型应该一致

    private String reportName;//报表名称

    private Boolean asyn;

    private JSONObject condition;

    private String resultTopic;


    public ReportConfig() {
    }


    public String getOperaCode() {
        return operaCode;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public JSONObject getCondition() {
        return condition;
    }

    public void setCondition(JSONObject condition) {
        this.condition = condition;
    }

    public void setOperaCode(String operaCode) {
        this.operaCode = operaCode;
    }

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public Long getOperaValidity() {
        return operaValidity;
    }

    public void setOperaValidity(Long operaValidity) {
        this.operaValidity = operaValidity;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public Boolean getAsyn() {
        return asyn;
    }

    public void setAsyn(Boolean asyn) {
        this.asyn = asyn;
    }
}

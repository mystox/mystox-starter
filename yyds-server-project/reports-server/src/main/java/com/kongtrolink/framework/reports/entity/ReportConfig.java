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

    private ExecutorType executorType; //查询|执行

    private JSONObject condition;

    private String resultTopic;

    private Boolean skipAspect = false;

    private Boolean validity = false;

    private Integer rhythm;
    private Long startTime;

    private String reportServerCode;


    public String getReportServerCode() {
        return reportServerCode;
    }

    public void setReportServerCode(String reportServerCode) {
        this.reportServerCode = reportServerCode;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public ReportConfig() {
    }

    public Integer getRhythm() {
        return rhythm;
    }

    public void setRhythm(Integer rhythm) {
        this.rhythm = rhythm;
    }

    public Boolean getValidity() {
        return validity;
    }

    public void setValidity(Boolean validity) {
        this.validity = validity;
    }

    public String getResultTopic() {
        return resultTopic;
    }

    public void setResultTopic(String resultTopic) {
        this.resultTopic = resultTopic;
    }

    public Boolean getSkipAspect() {
        return skipAspect;
    }

    public void setSkipAspect(Boolean skipAspect) {
        this.skipAspect = skipAspect;
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

    public ExecutorType getExecutorType() {
        return executorType;
    }

    public void setExecutorType(ExecutorType executorType) {
        this.executorType = executorType;
    }

//    public Boolean getAsyn() {
//        return asyn;
//    }
//
//    public void setAsyn(Boolean asyn) {
//        this.asyn = asyn;
//    }

    public JSONObject getCondition() {
        return condition;
    }

    public void setCondition(JSONObject condition) {
        this.condition = condition;
    }
}

package com.kongtrolink.framework.scloud.entity;

import java.util.Date;

/**
 * 工单记录表，记录每条工单流转工程
 */
public class WorkRecord {

    private String id ;                 //id
    private String workId;              //工单id
    private FacadeView worker;        //处理人
    private String operateType;         //操作类型
    private String operateDescribe;     //操作描述与APP端处理措施
    private Date operateTime;           //操作时间
    private float handleTime;            //处理时间
    private String operateFTU;          //操作终端
    private FacadeView receiver;         //操作的接受者

    //回单字段
    private String reason;              //故障原因
    private String handleWay;           //处理方式
    private String handleResult;        //处理结果
    private String verifyWay;           //验证方式

    public FacadeView getReceiver() {
        return receiver;
    }

    public void setReceiver(FacadeView receiver) {
        this.receiver = receiver;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getOperateDescribe() {
        return operateDescribe;
    }

    public void setOperateDescribe(String operateDescribe) {
        this.operateDescribe = operateDescribe;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public float getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(float handleTime) {
        this.handleTime = handleTime;
    }

    public String getOperateFTU() {
        return operateFTU;
    }

    public void setOperateFTU(String operateFTU) {
        this.operateFTU = operateFTU;
    }

    public FacadeView getWorker() {
        return worker;
    }

    public void setWorker(FacadeView worker) {
        this.worker = worker;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getHandleWay() {
        return handleWay;
    }

    public void setHandleWay(String handleWay) {
        this.handleWay = handleWay;
    }

    public String getHandleResult() {
        return handleResult;
    }

    public void setHandleResult(String handleResult) {
        this.handleResult = handleResult;
    }

    public String getVerifyWay() {
        return verifyWay;
    }

    public void setVerifyWay(String verifyWay) {
        this.verifyWay = verifyWay;
    }
}

package com.kongtrolink.framework.scloud.entity;

import java.io.Serializable;

/**
 * 工程管理-测试单操作记录 数据实体类
 * Created by Eric on 2020/4/13.
 */
public class ProjectOrderLogEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -3423145590264829382L;
    private String orderId; //测试单Id
    private FacadeView operator;    //操作人
    private String action;  //操作动作
    private String suggestion;  //审批意见
    private String remark;  //备注
    private Long time;  //操作时间

    public ProjectOrderLogEntity(){
    }

    public ProjectOrderLogEntity(String orderId, FacadeView operator, String action, String suggestion, String remark, Long time) {
        this.orderId = orderId;
        this.operator = operator;
        this.action = action;
        this.suggestion = suggestion;
        this.remark = remark;
        this.time = time;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public FacadeView getOperator() {
        return operator;
    }

    public void setOperator(FacadeView operator) {
        this.operator = operator;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}

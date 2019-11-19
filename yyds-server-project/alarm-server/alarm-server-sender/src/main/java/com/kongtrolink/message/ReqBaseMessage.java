package com.kongtrolink.message;

import com.kongtrolink.utils.CommonUtil;

/**
 * @auther: liudd
 * @date: 2019/10/26 10:51
 * 功能描述:
 */
public class ReqBaseMessage {
    protected String smsUser;
    protected String smsKey;
    protected Integer templateId;
    protected String signature;
    protected String timestamp;


    public ReqBaseMessage(String smsUser, String smsKey) {
        this.smsUser = smsUser;
        this.smsKey = smsKey;
        timestamp = CommonUtil.getTimestamp();
    }

    public String getSmsUser() {
        return smsUser;
    }

    public void setSmsUser(String smsUser) {
        this.smsUser = smsUser;
    }

    public String getSmsKey() {
        return smsKey;
    }

    public void setSmsKey(String smsKey) {
        this.smsKey = smsKey;
    }

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

package com.kongtrolink.message;

import com.kongtrolink.email.ConfUtil;
import com.kongtrolink.utils.CommonUtil;

/**
 * @auther: liudd
 * @date: 2019/10/26 10:51
 * 功能描述:发送短信实体
 */
public class ReqBaseMessage {
    private String smsUser;
    private String smsKey;
    private Integer templateId;
    private String signature;
    private String timestamp;


    public ReqBaseMessage() {
        ConfUtil confUtil = ConfUtil.getInstance();
        smsUser = confUtil.getSmsUser();
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

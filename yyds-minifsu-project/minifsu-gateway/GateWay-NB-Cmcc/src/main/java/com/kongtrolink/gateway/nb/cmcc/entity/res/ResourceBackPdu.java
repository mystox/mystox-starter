package com.kongtrolink.gateway.nb.cmcc.entity.res;

import java.io.Serializable;

/**
 * xx
 * by Mag on 2018/12/5.
 */
public class ResourceBackPdu implements Serializable {

    private static final long serialVersionUID = -2123077819492465757L;
    private String msgid;
    private int page;
    private int sum;
    private ResourceBackJson json;

    public void initPdu(ResourcePdu pduInfo){
        this.msgid = pduInfo.getMsgid();
        this.page = pduInfo.getPage();
        this.sum = pduInfo.getSum();
    }
    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public ResourceBackJson getJson() {
        return json;
    }

    public void setJson(ResourceBackJson json) {
        this.json = json;
    }
}
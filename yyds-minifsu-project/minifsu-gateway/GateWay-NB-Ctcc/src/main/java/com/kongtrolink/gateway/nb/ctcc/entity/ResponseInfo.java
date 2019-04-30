package com.kongtrolink.gateway.nb.ctcc.entity;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * xx
 * by Mag on 2019/1/18.
 */
public class ResponseInfo implements Serializable {
    private static final long serialVersionUID = -6049884206493013320L;
    private String msgid;
    private int page;
    private int sum;
    private Object json;

    public void initInfo(PackageData packageData){
        if(packageData!=null){
            this.msgid = packageData.getMsgid();
            this.page =   packageData.getPage();
            this.sum = packageData.getSum();
            JSONObject object = JSONObject.parseObject(packageData.getJson());
            this.json = object;
        }
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

    public Object getJson() {
        return json;
    }

    public void setJson(Object json) {
        this.json = json;
    }
}

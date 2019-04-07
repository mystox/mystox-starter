package com.kongtrolink.framework.execute.module.model;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by mystoxlol on 2019/3/26, 10:21.
 * company: kongtrolink
 * description:
 * update record:
 */
public class TerminalMsg
{
    private String msgId;
    private Integer pkgSum;
    private Integer pkgNo;

    private JSONObject payload;


    public String getMsgId()
    {
        return msgId;
    }

    public void setMsgId(String msgId)
    {
        this.msgId = msgId;
    }

    public Integer getPkgSum() {
        return pkgSum;
    }

    public void setPkgSum(Integer pkgSum) {
        this.pkgSum = pkgSum;
    }

    public Integer getPkgNo() {
        return pkgNo;
    }

    public void setPkgNo(Integer pkgNo) {
        this.pkgNo = pkgNo;
    }

    public JSONObject getPayload()
    {
        return payload;
    }

    public void setPayload(JSONObject payload)
    {
        this.payload = payload;
    }


    public static void main(String[] args)
    {
        String json = JSONObject.toJSONString(new TerminalMsg());
        System.out.println(json);
    }

}

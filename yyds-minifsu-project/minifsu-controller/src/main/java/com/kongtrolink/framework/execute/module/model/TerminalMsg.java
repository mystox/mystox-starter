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
    private int pkgSum;
    private long ts;
    private JSONObject payload;


    public String getMsgId()
    {
        return msgId;
    }

    public void setMsgId(String msgId)
    {
        this.msgId = msgId;
    }

    public int getPkgSum()
    {
        return pkgSum;
    }

    public void setPkgSum(int pkgSum)
    {
        this.pkgSum = pkgSum;
    }

    public long getTs()
    {
        return ts;
    }

    public void setTs(long ts)
    {
        this.ts = ts;
    }

    public JSONObject getPayload()
    {
        return payload;
    }

    public void setPayload(JSONObject payload)
    {
        this.payload = payload;
    }


    @Override
    public String toString()
    {
        return "TerminalMsg{" +
                "msgId='" + msgId + '\'' +
                ", pkgSum=" + pkgSum +
                ", ts=" + ts +
                ", payload=" + payload +
                '}';
    }
}

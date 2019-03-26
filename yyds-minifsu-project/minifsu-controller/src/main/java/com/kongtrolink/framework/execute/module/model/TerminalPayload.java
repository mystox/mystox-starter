package com.kongtrolink.framework.execute.module.model;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * Created by mystoxlol on 2019/3/26, 10:21.
 * company: kongtrolink
 * description:
 * update record:
 */
public class TerminalPayload implements Serializable
{
    private String msgId;
    private String pktType;
    private String uuid;
    private String gip;
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

    public String getPktType()
    {
        return pktType;
    }

    public void setPktType(String pktType)
    {
        this.pktType = pktType;
    }

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    public String getGip()
    {
        return gip;
    }

    public void setGip(String gip)
    {
        this.gip = gip;
    }

    @Override
    public String toString()
    {
        return "TerminalPayload{" +
                "msgId='" + msgId + '\'' +
                ", pktType='" + pktType + '\'' +
                ", uuid='" + uuid + '\'' +
                ", gip='" + gip + '\'' +
                ", pkgSum=" + pkgSum +
                ", ts=" + ts +
                ", payload=" + payload +
                '}';
    }
}

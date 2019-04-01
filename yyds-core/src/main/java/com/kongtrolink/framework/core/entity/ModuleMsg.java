package com.kongtrolink.framework.core.entity;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by mystoxlol on 2019/3/27, 16:35.
 * company: kongtrolink
 * description: 服务间通讯实体
 * update record:
 */
public class ModuleMsg
{
    private String msgId;
    private String pktType; //消息通讯类型 类型参考PktType.class
    private String uuid;
    private String SN;
    private JSONObject payload;

    public String getSN()
    {
        return SN;
    }

    public void setSN(String SN)
    {
        this.SN = SN;
    }

    public String getMsgId()
    {
        return msgId;
    }

    public void setMsgId(String msgId)
    {
        this.msgId = msgId;
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
        return "ModuleMsg{" +
                "msgId='" + msgId + '\'' +
                ", pktType='" + pktType + '\'' +
                ", uuid='" + uuid + '\'' +
                ", SN='" + SN + '\'' +
                ", payload=" + payload +
                '}';
    }

    public static void main(String[] args)
    {
        ModuleMsg moduleMsg = new ModuleMsg();
        moduleMsg.setSN("sfadfsf");
        System.out.println(JSONObject.toJSONString(moduleMsg));
        ModuleMsg moduleMsg1 = JSONObject.parseObject("{\"sN\":\"sfadfsf\"}",ModuleMsg.class);
        System.out.println(moduleMsg1);
    }
}

package com.kongtrolink.framework.core.entity;

import java.sql.Date;

/**
 * Created by mystoxlol on 2019/3/28, 14:14.
 * company: kongtrolink
 * description:
 * update record:
 */
public class Log
{
    private String id;
    private Date time;
    private Integer errorCode;
    private String SN;
    private String msgType;
    private String msgId;
    private String serviceName;
    private String hostName;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public Date getTime()
    {
        return time;
    }

    public void setTime(Date time)
    {
        this.time = time;
    }

    public Integer getErrorCode()
    {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode)
    {
        this.errorCode = errorCode;
    }

    public String getSN()
    {
        return SN;
    }

    public void setSN(String SN)
    {
        this.SN = SN;
    }

    public String getMsgType()
    {
        return msgType;
    }

    public void setMsgType(String msgType)
    {
        this.msgType = msgType;
    }

    public String getMsgId()
    {
        return msgId;
    }

    public void setMsgId(String msgId)
    {
        this.msgId = msgId;
    }

    public String getServiceName()
    {
        return serviceName;
    }

    public void setServiceName(String serviceName)
    {
        this.serviceName = serviceName;
    }

    public String getHostName()
    {
        return hostName;
    }

    public void setHostName(String hostName)
    {
        this.hostName = hostName;
    }
}

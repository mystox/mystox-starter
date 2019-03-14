package com.kongtrolink.framework.node.controller.entity;

import java.util.Date;

/**
 * Created by mystoxlol on 2019/2/19, 14:28.
 * company: kongtrolink
 * description:
 * update record:
 */
public class ControllerNodeData
{
    private String ip;
    private int port;
    private String name;
    private Date startTime;
    private Date updateTime;
    private Date bTime;



    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Date getbTime()
    {
        return bTime;
    }

    public void setbTime(Date bTime)
    {
        this.bTime = bTime;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }
}

package com.kongtrolink.framework.core.entity;

import java.util.Date;

/**
 * Created by mystoxlol on 2019/2/19, 14:29.
 * company: kongtrolink
 * description:工作节点信息实体
 * update record:
 */
public class WorkerNodeData
{


    private String ip;
    private int port;
    private String name;
    private Date startTime;
    private Date updateTIme;
    private int cpuPercent;
    private int cpuCore;
    private int cpuFrequency;
    private int memPercent;
    private int memTotal;
    private int memFrequency;

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

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getUpdateTIme()
    {
        return updateTIme;
    }

    public void setUpdateTIme(Date updateTIme)
    {
        this.updateTIme = updateTIme;
    }

    public int getCpuPercent()
    {
        return cpuPercent;
    }

    public void setCpuPercent(int cpuPercent)
    {
        this.cpuPercent = cpuPercent;
    }

    public int getCpuCore()
    {
        return cpuCore;
    }

    public void setCpuCore(int cpuCore)
    {
        this.cpuCore = cpuCore;
    }

    public int getCpuFrequency()
    {
        return cpuFrequency;
    }

    public void setCpuFrequency(int cpuFrequency)
    {
        this.cpuFrequency = cpuFrequency;
    }

    public int getMemPercent()
    {
        return memPercent;
    }

    public void setMemPercent(int memPercent)
    {
        this.memPercent = memPercent;
    }

    public int getMemTotal()
    {
        return memTotal;
    }

    public void setMemTotal(int memTotal)
    {
        this.memTotal = memTotal;
    }

    public int getMemFrequency()
    {
        return memFrequency;
    }

    public void setMemFrequency(int memFrequency)
    {
        this.memFrequency = memFrequency;
    }
}

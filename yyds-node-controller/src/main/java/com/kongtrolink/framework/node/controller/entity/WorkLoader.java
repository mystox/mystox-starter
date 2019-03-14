package com.kongtrolink.framework.node.controller.entity;

/**
 * Created by mystoxlol on 2019/2/21, 8:39.
 * company: kongtrolink
 * description: 负载实体
 * update record:
 */
public class WorkLoader
{
    private String workId;
    private int loadValue;
    private int state; // 0 invalid无效 1 valid 有效
    private String ip; //业务节点
    private int port; //业务节点rpc服务端口

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

    public String getWorkId()
    {
        return workId;
    }

    public void setWorkId(String workId)
    {
        this.workId = workId;
    }

    public int getLoadValue()
    {
        return loadValue;
    }

    public void setLoadValue(int loadValue)
    {
        this.loadValue = loadValue;
    }

    public int getState()
    {
        return state;
    }

    public void setState(int state)
    {
        this.state = state;
    }
}

package com.kongtrolink.framework.node.controller.entity;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by mystoxlol on 2019/2/20, 12:29.
 * company: kongtrolink
 * description: 路由表实体
 * update record:
 */
public class Route
{
    private String workId; //业务节点workId
    private String ip; //业务节点
    private int port; //业务节点rpc服务端口
    private Date accessTime; //最近访问时间
    private volatile AtomicInteger accessCount; //访问次数
    private boolean routeType; //路由类型 静态和非静态
    private int state; //路由状态 0 invalid 1 valid

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

    public Date getAccessTime()
    {
        return accessTime;
    }

    public void setAccessTime(Date accessTime)
    {
        this.accessTime = accessTime;
    }


    public AtomicInteger getAccessCount()
    {
        return accessCount;
    }

    public void setAccessCount(AtomicInteger accessCount)
    {
        this.accessCount = accessCount;
    }

    public boolean isRouteType()
    {
        return routeType;
    }

    public void setRouteType(boolean routeType)
    {
        this.routeType = routeType;
    }

    public int getState()
    {
        return state;
    }

    public void setState(int state)
    {
        this.state = state;
    }

    @Override
    public String toString()
    {
        return "Route{" +
                "workId='" + workId + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", accessTime=" + accessTime +
                ", accessCount=" + accessCount +
                ", routeType=" + routeType +
                ", state=" + state +
                '}';
    }
}

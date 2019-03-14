package com.kongtrolink.framework.node.controller.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mystoxlol on 2019/2/19, 14:29.
 * company: kongtrolink
 * description:
 * update record:
 */
public class InfoData implements Serializable
{
    private Date startTime; //集群启动时间
    private Date updateTime;
    private int workNumOnline;


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

    public int getWorkNumOnline()
    {
        return workNumOnline;
    }

    public void setWorkNumOnline(int workNumOnline)
    {
        this.workNumOnline = workNumOnline;
    }

    @Override
    public String toString()
    {
        return "InfoData{" +
                "startTime=" + startTime +
                ", updateTime=" + updateTime +
                ", workNumOnline=" + workNumOnline +
                '}';
    }

}

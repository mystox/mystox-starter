package com.kongtrolink.framework.core.entity;

import java.io.Serializable;

/**
 * Created by mystoxlol on 2018/7/10, 8:39.
 * company: kongtrolink
 * description:
 * update record:
 */
public class ServiceSubVo implements Serializable
{
    private String id;
    private String serviceCode;

    private String serviceName;

    private String serviceVersion;

    private String serviceUri;


    private String routeMark;

    private String pageRoute;

    public String getPageRoute()
    {
        return pageRoute;
    }

    public void setPageRoute(String pageRoute)
    {
        this.pageRoute = pageRoute;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getServiceCode()
    {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode)
    {
        this.serviceCode = serviceCode;
    }

    public String getServiceName()
    {
        return serviceName;
    }

    public void setServiceName(String serviceName)
    {
        this.serviceName = serviceName;
    }

    public String getServiceVersion()
    {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion)
    {
        this.serviceVersion = serviceVersion;
    }

    public String getServiceUri()
    {
        return serviceUri;
    }

    public void setServiceUri(String serviceUri)
    {
        this.serviceUri = serviceUri;
    }

    public String getRouteMark()
    {
        return routeMark;
    }

    public void setRouteMark(String routeMark)
    {
        this.routeMark = routeMark;
    }
}

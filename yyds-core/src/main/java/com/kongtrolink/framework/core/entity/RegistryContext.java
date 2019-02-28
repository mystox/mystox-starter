package com.kongtrolink.framework.core.entity;

/**
 * Created by mystoxlol on 2019/2/19, 22:26.
 * company: kongtrolink
 * description:
 * update record:
 */
public class RegistryContext
{
    private String path;
    private Object data;


    public RegistryContext(String path, Object data)
    {
        this.path = path;
        this.data = data;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public Object getData()
    {
        return data;
    }

    public void setData(Object data)
    {
        this.data = data;
    }
}

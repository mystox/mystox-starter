package com.kongtrolink.framework.model.signal;

import java.io.Serializable;

/**
 * Created by mystoxlol on 2018/12/8, 9:35.
 * company: kongtrolink
 * description:
 * update record:
 */
public class SignalCode implements Serializable
{
    private String name;
    private String unit;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getUnit()
    {
        return unit;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }
}

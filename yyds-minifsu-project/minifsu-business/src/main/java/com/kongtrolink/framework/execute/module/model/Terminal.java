package com.kongtrolink.framework.execute.module.model;

/**
 * Created by mystoxlol on 2019/3/28, 9:38.
 * company: kongtrolink
 * description:
 * update record:
 */
public class Terminal
{
    private String id;
    private String SN;
    private String type;
    private String BID;

    public String getSN()
    {
        return SN;
    }

    public void setSN(String SN)
    {
        this.SN = SN;
    }

    public String getBID()
    {
        return BID;
    }

    public void setBID(String BID)
    {
        this.BID = BID;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}

package com.kongtrolink.framework.execute.module.model;

/**
 * Created by mystoxlol on 2019/3/28, 9:57.
 * company: kongtrolink
 * description:
 * update record:
 */
public class Order
{
    private String id;
    private String BID;
    private String BIP;
    private String WIP;

    public String getWIP() {
        return WIP;
    }

    public void setWIP(String WIP) {
        this.WIP = WIP;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getBID()
    {
        return BID;
    }

    public void setBID(String BID)
    {
        this.BID = BID;
    }

    public String getBIP()
    {
        return BIP;
    }

    public void setBIP(String BIP)
    {
        this.BIP = BIP;
    }
}

package com.kongtrolink.gateway.nb.cmcc.entity.res;


import com.kongtrolink.gateway.nb.cmcc.entity.iot.ResourceAck;
import com.kongtrolink.gateway.nb.cmcc.entity.iot.ResourceItem;

import java.io.Serializable;
import java.util.List;

/**
 * xx
 * by Mag on 2019/2/19.
 */
public class ResourceBackJson implements Serializable {

    private static final long serialVersionUID = -1992555284648879446L;

    private String fsuid;
    private long TimeStamp;
    private int total_count;
    private List<ResourceItem> item;

    public void initBack(ResourceAck ack, ResourceJson json){
        this.total_count = ack.getTotal_count();
        this.item = ack.getItem();
        this.fsuid = json.getFsuid();
        this.TimeStamp = json.getTimeStamp();
    }

    public String getFsuid() {
        return fsuid;
    }

    public void setFsuid(String fsuid) {
        this.fsuid = fsuid;
    }

    public long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        TimeStamp = timeStamp;
    }

    public int getTotal_count() {
        return total_count;
    }

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public List<ResourceItem> getItem() {
        return item;
    }

    public void setItem(List<ResourceItem> item) {
        this.item = item;
    }
}

package com.kongtrolink.gateway.nb.cmcc.entity.iot;

import java.io.Serializable;
import java.util.List;

/**
 * 资源信息
 * by Mag on 2019/2/15.
 */
public class ResourceAck implements Serializable {

    private static final long serialVersionUID = -1793902566176583622L;

    private int total_count;
    private List<ResourceItem> item;

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

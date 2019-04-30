package com.kongtrolink.gateway.nb.cmcc.entity.iot;

import java.io.Serializable;
import java.util.List;

/**
 * 获取资源列表- item 字段
 * by Mag on 2019/2/15.
 */
public class ResourceItem implements Serializable {
    private static final long serialVersionUID = -7076528439129235634L;
    private int obj_id;
    private List<ResourceInstances> instances;

    public int getObj_id() {
        return obj_id;
    }

    public void setObj_id(int obj_id) {
        this.obj_id = obj_id;
    }

    public List<ResourceInstances> getInstances() {
        return instances;
    }

    public void setInstances(List<ResourceInstances> instances) {
        this.instances = instances;
    }
}

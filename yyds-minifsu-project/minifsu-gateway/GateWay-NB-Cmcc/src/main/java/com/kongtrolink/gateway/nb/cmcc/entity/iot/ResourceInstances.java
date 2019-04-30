package com.kongtrolink.gateway.nb.cmcc.entity.iot;

import java.io.Serializable;

/**
 * 获取资源列表- item 字段 - Instances字段
 * by Mag on 2019/2/15.
 */
public class ResourceInstances implements Serializable{
    private static final long serialVersionUID = -1747516699901086417L;
    private int inst_id;
    private Integer[] resources;

    public int getInst_id() {
        return inst_id;
    }

    public void setInst_id(int inst_id) {
        this.inst_id = inst_id;
    }

    public Integer[] getResources() {
        return resources;
    }

    public void setResources(Integer[] resources) {
        this.resources = resources;
    }
}

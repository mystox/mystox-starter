package com.kongtrolink.framework.scloud.entity;

import java.io.Serializable;

/**
 * 工程管理测试单 数据实体类
 * Created by Eric on 2020/4/13.
 */
public class ProjectOrderEntity implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 8908778391904991970L;
    private String _id;

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }
}

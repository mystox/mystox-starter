package com.kongtrolink.framework.scloud.entity;

import java.io.Serializable;

/**
 * 工程管理-测试单操作记录 数据实体类
 * Created by Eric on 2020/4/13.
 */
public class ProjectOrderLogEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -3423145590264829382L;
    private String orderId; //测试单Id

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}

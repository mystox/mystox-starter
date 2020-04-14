package com.kongtrolink.framework.scloud.query;

/**
 * 工程管理测试单 查询类
 * Created by Eric on 2020/4/13.
 */
public class ProjectOrderQuery extends Paging {
    /**
     *
     */
    private static final long serialVersionUID = -4109961828140714709L;

    private String orderId; //测试单Id

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}

package com.kongtrolink.gateway.nb.cmcc.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 分包信息
 * by Mag on 2018/12/5.
 */
public class PackageInfo implements Serializable {

    private int order; // 包的顺序
    private int total;//包的总数
    private String data;//分包内容
    private Date ctime;//接收包的时间

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}

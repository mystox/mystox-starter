package com.kongtrolink.framework.scloud.entity.model.home;

import java.io.Serializable;

/**
 * 告警工单统计
 * @author Mag
 **/
public class HomeWorkModel implements Serializable {
    private static final long serialVersionUID = -3502931961491280584L;

    private String state;//状态
    private int count;//数量

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

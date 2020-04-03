package com.kongtrolink.framework.scloud.entity.model.home;

import java.io.Serializable;

/**
 * 首页 站点交维态数量统计
 * Created by Mg on 2018/5/11.
 */
public class HomeFsuNumber implements Serializable {

    private static final long serialVersionUID = -2078676889300386894L;

    private int intersectionState; //交维态数量

    private int  total; //总数

    public int getIntersectionState() {
        return intersectionState;
    }

    public void setIntersectionState(int intersectionState) {
        this.intersectionState = intersectionState;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}

package com.kongtrolink.framework.scloud.entity.model.home;

import java.io.Serializable;

/**
 * 首页 站点交维态数量统计
 * Created by Mg on 2018/5/11.
 */
public class HomeFsuOnlineModel implements Serializable {

    private static final long serialVersionUID = -2078676889300386894L;

    private String state; //在线状态

    private int  count;//数量

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

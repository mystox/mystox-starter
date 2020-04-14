package com.kongtrolink.framework.scloud.entity.model.home;

import java.io.Serializable;

/**
 * 告警工单统计
 * @author Mag
 **/
public class HomeWorkDto implements Serializable {
    private static final long serialVersionUID = -3502931961491280584L;

    private int noReceive;//未接工单
    private int onRoad;//在途
    private int overTime;//超时
    private int history;//历史
    private int total;//总数

    public int getNoReceive() {
        return noReceive;
    }

    public void setNoReceive(int noReceive) {
        this.noReceive = noReceive;
    }

    public int getOnRoad() {
        return onRoad;
    }

    public void setOnRoad(int onRoad) {
        this.onRoad = onRoad;
    }

    public int getOverTime() {
        return overTime;
    }

    public void setOverTime(int overTime) {
        this.overTime = overTime;
    }

    public int getHistory() {
        return history;
    }

    public void setHistory(int history) {
        this.history = history;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}

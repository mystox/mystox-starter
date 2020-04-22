package com.kongtrolink.framework.scloud.entity.multRoom;

import java.io.Serializable;

/**
 * 历史告警 统计
 * by Mag on 6/20/2018.
 */
public class RoomHistoryAlarm implements Serializable{

    private static final long serialVersionUID = 8008406200919461249L;

    private int level1;// 1 级历史告警数
    private int level2;// 2 级历史告警数
    private int level3;// 3 级历史告警数
    private int level4;// 4 级历史告警数
    private int total;//  历史告警 总数

    public int getLevel1() {
        return level1;
    }

    public void setLevel1(int level1) {
        this.level1 = level1;
    }

    public int getLevel2() {
        return level2;
    }

    public void setLevel2(int level2) {
        this.level2 = level2;
    }

    public int getLevel3() {
        return level3;
    }

    public void setLevel3(int level3) {
        this.level3 = level3;
    }

    public int getLevel4() {
        return level4;
    }

    public void setLevel4(int level4) {
        this.level4 = level4;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}

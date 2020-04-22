package com.kongtrolink.framework.scloud.entity.multRoom;

import java.io.Serializable;

/**
 * 首页 汇总 专用
 * Created by Mg on 2018/5/11.
 */
public class RoomAlarmCount implements Serializable{


    private static final long serialVersionUID = -4458985256065228072L;

    private int count1; //告警top1数量
    private int count2; //告警top2数量
    private int count3; //告警top3数量
    private String time; //时间

    public int getCount1() {
        return count1;
    }

    public void setCount1(int count1) {
        this.count1 = count1;
    }

    public int getCount2() {
        return count2;
    }

    public void setCount2(int count2) {
        this.count2 = count2;
    }

    public int getCount3() {
        return count3;
    }

    public void setCount3(int count3) {
        this.count3 = count3;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

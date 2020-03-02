package com.kongtrolink.framework.scloud.entity.model;

/**
 * 历史数据展示对象
 * @author Mag
 **/
public class HistoryDataModel {

    private long time; //时间
    private String value; //值

    public HistoryDataModel() {
    }

    public HistoryDataModel(long time, String value) {
        this.time = time;
        this.value = value;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

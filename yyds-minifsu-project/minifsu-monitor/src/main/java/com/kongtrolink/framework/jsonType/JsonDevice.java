package com.kongtrolink.framework.jsonType;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/3/29 13:28
 * @Description:
 */
public class JsonDevice {

    //JSON转换使用字段
    private String dev;     //变化上报时dev字段

    private List<JsonSignal> data;

    public String getDev() {
        return dev;
    }

    public void setDev(String dev) {
        this.dev = dev;
    }

    public List<JsonSignal> getData() {
        return data;
    }

    public void setData(List<JsonSignal> data) {
        this.data = data;
    }
}

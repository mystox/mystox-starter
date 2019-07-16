package com.kongtrolink.framework.jsonType;

import java.util.HashMap;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/3/29 13:28
 * @Description:
 */
public class JsonDevice {

    //JSON转换使用字段
    private String dev;     //变化上报时dev字段
    private HashMap<String, Long> info ; //上报的实时数据

    public String getDev() {
        return dev;
    }

    public void setDev(String dev) {
        this.dev = dev;
    }

    public HashMap<String, Long> getInfo() {
        return info;
    }

    public void setInfo(HashMap<String, Long> info) {
        this.info = info;
    }
}

package com.kongtrolink.framework.mqtt;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/10/22 16:22
 * @Description:
 */
public class CIResponseEntity {

    private int count;
    private List<JSONObject> infos;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<JSONObject> getInfos() {
        return infos;
    }

    public void setInfos(List<JSONObject> infos) {
        this.infos = infos;
    }
}

package com.kongtrolink.framework.scloud.mqtt.entity;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * 获取资产信息 响应数据实体类
 * Created by Eric on 2020/2/10.
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

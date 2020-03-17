package com.kongtrolink.framework.reports.entity.query;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * 获取资产信息 响应数据实体类
 * Created by Eric on 2020/2/10.
 */
public class CIResponseEntity {

    private int result; //请求结果。0-失败，1-成功
    private int count;
    private List<JSONObject> infos;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

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

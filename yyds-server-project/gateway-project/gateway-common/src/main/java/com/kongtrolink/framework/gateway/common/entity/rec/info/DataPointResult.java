package com.kongtrolink.framework.gateway.common.entity.rec.info;

/**
 * 数据点详情
 * Created by Mag on 2019/10/14.
 */
public class DataPointResult {

    private String id;//	string	否	数据点ID
    private int result;//	number	否	数据点设置是否成功

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}

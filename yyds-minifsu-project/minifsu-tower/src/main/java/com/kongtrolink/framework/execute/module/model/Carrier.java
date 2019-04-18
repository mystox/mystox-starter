package com.kongtrolink.framework.execute.module.model;

/**
 * @author fengw
 * 运营商对照信息
 * 新建文件 2019-4-17 18:32:23
 */
public class Carrier {
    //内部服务运营商类型
    private String type;
    //铁塔运营商类型
    private String cntbType;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCntbType() {
        return cntbType;
    }

    public void setCntbType(String cntbType) {
        this.cntbType = cntbType;
    }
}

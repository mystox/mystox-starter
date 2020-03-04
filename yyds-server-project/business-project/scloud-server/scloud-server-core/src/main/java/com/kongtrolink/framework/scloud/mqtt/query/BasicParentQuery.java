package com.kongtrolink.framework.scloud.mqtt.query;

/**
 * 查询条件 父资产信息
 *  SCloud->中台资管
 * Created by Eric on 2020/3/4.
 */
public class BasicParentQuery {

    private BasicCommonQuery name;  //父资产类型
    private BasicCommonQuery sn;    //父资产SN

    public BasicCommonQuery getName() {
        return name;
    }

    public void setName(BasicCommonQuery name) {
        this.name = name;
    }

    public BasicCommonQuery getSn() {
        return sn;
    }

    public void setSn(BasicCommonQuery sn) {
        this.sn = sn;
    }
}

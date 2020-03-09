package com.kongtrolink.framework.scloud.mqtt.entity;

import com.kongtrolink.framework.scloud.constant.CommonConstant;

/**
 * 获取到的父资产 信息
 *  中台资管->SCloud
 * Created by Eric on 2020/3/4.
 */
public class BasicParentEntity {

    private String type;    //父资产类型
    private String sn;  //父资产SN
    private String relationshipType = CommonConstant.RELATIONSHIP_TYPE_INSTALL; //连接类型，Install为安装于

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }
}

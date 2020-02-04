package com.kongtrolink.framework.gateway.tower.core.entity.impl;

import java.io.Serializable;

public class EnterpriseEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 4199934412488082011L;

    private String uniqueCode;      // 企业唯一码

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }
}

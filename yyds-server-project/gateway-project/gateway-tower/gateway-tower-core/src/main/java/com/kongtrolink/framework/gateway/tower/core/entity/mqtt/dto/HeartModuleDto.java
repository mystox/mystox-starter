package com.kongtrolink.framework.gateway.tower.core.entity.mqtt.dto;


import com.kongtrolink.framework.gateway.tower.core.entity.RedisFsuInfo;

import java.io.Serializable;

public class HeartModuleDto implements Serializable {
    private static final long serialVersionUID = 7753748773951091367L;

    private String uniqueCode;
    private RedisFsuInfo redisFsuInfo;

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public RedisFsuInfo getRedisFsuInfo() {
        return redisFsuInfo;
    }

    public void setRedisFsuInfo(RedisFsuInfo redisFsuInfo) {
        this.redisFsuInfo = redisFsuInfo;
    }
}

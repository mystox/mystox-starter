package com.kongtrolink.framework.gateway.tower.core.entity.mqtt.dto;


import com.kongtrolink.framework.gateway.tower.core.entity.RedisFsuInfo;

import java.io.Serializable;

public class HistoryModuleDto implements Serializable {

    private static final long serialVersionUID = -5371122675563624739L;
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

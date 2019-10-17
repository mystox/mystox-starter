package com.kongtrolink.framework.gateway.common.entity.rec.info;

/**
 * 告警点信息
 * Created by Mag on 2019/10/14.
 */
public class AlarmPointSimple {

    private String id;//	String	否	告警点ID
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

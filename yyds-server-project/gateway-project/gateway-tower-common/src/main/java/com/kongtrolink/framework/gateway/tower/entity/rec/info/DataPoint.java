package com.kongtrolink.framework.gateway.tower.entity.rec.info;

/**
 * 数据点详情
 * Created by Mag on 2019/10/14.
 */
public class DataPoint {

    private String id;//	string	否	数据点ID
    private int type;//	number	否	数据点类型
    private String name;//	string	否	数据点名称
    private Double value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}

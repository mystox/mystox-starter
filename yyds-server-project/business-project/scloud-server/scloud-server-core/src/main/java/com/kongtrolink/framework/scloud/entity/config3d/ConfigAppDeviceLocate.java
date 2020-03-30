/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kongtrolink.framework.scloud.entity.config3d;

/**
 * copy form scloud
 * @author Mag
 */
public class ConfigAppDeviceLocate {
    
    private String id;  // 设备ID
    private String name;    // 设备名
    private String type;    // 设备类型名
    private String typeCode;    // 类型编码
    private int group;  // 联动操作分组
    private int room;   // 所属房间（区域）ID
    private ConfigAppCoord coord;   // 设备坐标

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public int getRoom() {
        return room;
    }

    public void setRoom(int room) {
        this.room = room;
    }

    public ConfigAppCoord getCoord() {
        return coord;
    }

    public void setCoord(ConfigAppCoord coord) {
        this.coord = coord;
    }

    @Override
    public String toString() {
        return "ConfigAppDeviceLocate{" + "id=" + id + ", name=" + name + ", type=" + type + ", typeCode=" + typeCode + ", group=" + group + ", room=" + room + ", coord=" + coord + '}';
    }

}

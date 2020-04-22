/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kongtrolink.framework.scloud.entity.config3d;

import java.util.List;

/**
 * copy form scloud
 * @author Mag
 */
public class ConfigAppRoom {
    
    private int id;     // 房间（区域）ID
    private List<List<ConfigAppCoord>> paths;   // 第一层为闭环个数，第二层为闭环连接线各端点

    public ConfigAppRoom() {
    }

    public ConfigAppRoom(int id, List<List<ConfigAppCoord>> paths) {
        this.id = id;
        this.paths = paths;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<List<ConfigAppCoord>> getPaths() {
        return paths;
    }

    public void setPaths(List<List<ConfigAppCoord>> paths) {
        this.paths = paths;
    }

    @Override
    public String toString() {
        return "ConfigAppRoom{" + "id=" + id + ", paths=" + paths + '}';
    }
    
}

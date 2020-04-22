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
public class ConfigAppCoord {
    
    private float x;
    private float y;

    public ConfigAppCoord() {
    }

    public ConfigAppCoord(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "ConfigAppCoord{" + "x=" + x + ", y=" + y + '}';
    }
    
}

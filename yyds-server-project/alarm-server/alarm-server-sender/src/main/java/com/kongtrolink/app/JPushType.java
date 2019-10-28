/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kongtrolink.app;

/**
 *
 * @author Mosaico
 */
public enum JPushType {
    
    ALARM_REPORT(101),  // 告警上报
    ALARM_RECOVER(102);// 告警消除
    
    public static final String TYPE = "type";
    
    private JPushType(int num) {
        this.num = num;
    }
    
    private int num;

    public int getNum() {
        return num;
    }
    
}

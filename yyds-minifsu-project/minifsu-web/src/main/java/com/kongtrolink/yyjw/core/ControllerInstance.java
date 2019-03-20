package com.kongtrolink.yyjw.core;

import com.kongtrolink.yyjw.model.signal.SignalCode;

import java.util.Map;

/**
 * \* @Author: mystox
 * \* Date: 2018/12/4 10:33
 * \* Description:
 * \
 */
public class ControllerInstance {

    private Map<String,String> tierMap;
    private Map<String,String> dStationMap;
    private Map<String,String> roomStationMap;
    private Map<String, SignalCode> signalMap;


    public static ControllerInstance getInstance() {
        return SingletonInstance.instance;
    }
    private static class SingletonInstance
    {
        private final static ControllerInstance instance = new ControllerInstance();
    }

    public Map<String, SignalCode> getSignalMap()
    {
        return signalMap;
    }

    public void setSignalMap(Map<String, SignalCode> signalMap)
    {
        this.signalMap = signalMap;
    }

    public Map<String, String> getTierMap() {
        return tierMap;
    }

    public void setTierMap(Map<String, String> tierMap) {
        this.tierMap = tierMap;
    }

    public Map<String, String> getdStationMap()
    {
        return dStationMap;
    }

    public void setdStationMap(Map<String, String> dStationMap)
    {
        this.dStationMap = dStationMap;
    }

    public Map<String, String> getRoomStationMap()
    {
        return roomStationMap;
    }

    public void setRoomStationMap(Map<String, String> roomStationMap)
    {
        this.roomStationMap = roomStationMap;
    }
}
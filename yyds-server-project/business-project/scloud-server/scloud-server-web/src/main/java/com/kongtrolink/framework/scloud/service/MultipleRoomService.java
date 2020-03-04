package com.kongtrolink.framework.scloud.service;


import com.kongtrolink.framework.scloud.entity.multRoom.RoomSignalTypeConfig;

/**
 * 综合机房
 * by Mag on 6/20/2018.
 */
public interface MultipleRoomService {
    /**
     * 保存自定义信号点
     */
    boolean addShowSignalConfig(String uniqueCode, RoomSignalTypeConfig config);
    /**
     * 根据设备查询信号点配置 不包含默认值
     */
    RoomSignalTypeConfig queryRoomSignalTypeConfig(String uniqueCode, int deviceId);
    /**
     * 根据设备查询信号点配置包含默认值 前天界面用
     */
    RoomSignalTypeConfig queryRoomSignalTypeConfigShow(String uniqueCode, int deviceId);

}

package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.scloud.dao.MultipleRoomDao;
import com.kongtrolink.framework.scloud.entity.multRoom.RoomSignalTypeConfig;
import com.kongtrolink.framework.scloud.service.MultipleRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mag
 **/
@Service
public class MultipleRoomServiceImpl implements MultipleRoomService {
    @Autowired
    MultipleRoomDao multipleRoomDao;
    /**
     * 保存自定义信号点
     *
     * @param uniqueCode
     * @param config
     */
    @Override
    public boolean addShowSignalConfig(String uniqueCode, RoomSignalTypeConfig config) {
        try{
            //先删除原先设备的配置
            multipleRoomDao.delShowSignalConfig(uniqueCode,config.getDeviceId());
            //保存最新的设备配置
            multipleRoomDao.addShowSignalConfig(uniqueCode, config);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 根据设备查询信号点配置 不包含默认值
     *
     * @param uniqueCode
     * @param deviceId
     */
    @Override
    public RoomSignalTypeConfig queryRoomSignalTypeConfig(String uniqueCode, int deviceId) {
        return multipleRoomDao.queryRoomSignalTypeConfig(uniqueCode, deviceId);
    }

    /**
     * 根据设备查询信号点配置包含默认值 前天界面用
     *
     * @param uniqueCode
     * @param deviceId
     */
    @Override
    public RoomSignalTypeConfig queryRoomSignalTypeConfigShow(String uniqueCode, int deviceId) {
        return multipleRoomDao.queryRoomSignalTypeConfig(uniqueCode, deviceId);
    }
}

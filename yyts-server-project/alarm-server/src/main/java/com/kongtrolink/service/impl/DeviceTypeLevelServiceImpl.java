package com.kongtrolink.service.impl;

import com.kongtrolink.dao.DeviceTypeLevelDao;
import com.kongtrolink.enttiy.DeviceTypeLevel;
import com.kongtrolink.query.DeviceTypeLevelQuery;
import com.kongtrolink.service.DeviceTypeLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/20 16:43
 * @Description:
 */
@Service
public class DeviceTypeLevelServiceImpl implements DeviceTypeLevelService {
    @Autowired
    DeviceTypeLevelDao typeLevelDao;

    @Override
    public void add(DeviceTypeLevel deviceTypeLevel) {
        //liuddtodo 等级重复等问题
        typeLevelDao.add(deviceTypeLevel);
    }

    @Override
    public boolean delete(String deviceTypeLevelId) {
        return typeLevelDao.delete(deviceTypeLevelId);
    }

    @Override
    public boolean update(DeviceTypeLevel deviceTypeLevel) {
        //liuddtodo 需要判定等级重复问题
        return typeLevelDao.update(deviceTypeLevel);
    }

    @Override
    public List<DeviceTypeLevel> list(DeviceTypeLevelQuery levelQuery) {
        return typeLevelDao.list(levelQuery);
    }

    @Override
    public int count(DeviceTypeLevelQuery levelQuery) {
        return typeLevelDao.count(levelQuery);
    }

    @Override
    public DeviceTypeLevel getOne(DeviceTypeLevelQuery levelQuery) {
        return typeLevelDao.getOne(levelQuery);
    }

    @Override
    public boolean isReprat(DeviceTypeLevel typeLevel) {
        DeviceTypeLevelQuery levelQuery = new DeviceTypeLevelQuery();
        levelQuery.setUniqueCode(typeLevel.getUniqueCode());
        levelQuery.setService(typeLevel.getService());
        levelQuery.setDeviceType(typeLevel.getDeviceType());
        levelQuery.setLevel(typeLevel.getLevel());
        DeviceTypeLevel one = getOne(levelQuery);
        if(null == one){
            return false;
        }
        if(one.getId().equals(typeLevel.getId())){
            return false;
        }
        return true;
    }
}

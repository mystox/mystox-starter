package com.kongtrolink.framework.service.impl;

import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.dao.DeviceTypeLevelDao;
import com.kongtrolink.framework.enttiy.AlarmLevel;
import com.kongtrolink.framework.enttiy.DeviceTypeLevel;
import com.kongtrolink.framework.enttiy.EnterpriseLevel;
import com.kongtrolink.framework.query.AlarmLevelQuery;
import com.kongtrolink.framework.query.DeviceTypeLevelQuery;
import com.kongtrolink.framework.query.EnterpriseLevelQuery;
import com.kongtrolink.framework.service.AlarmLevelService;
import com.kongtrolink.framework.service.DeviceTypeLevelService;
import com.kongtrolink.framework.service.EnterpriseLevelService;
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
    @Autowired
    AlarmLevelService alarmLevelService;
    @Autowired
    EnterpriseLevelService enterpriseLevelService;

    @Override
    public boolean add(DeviceTypeLevel deviceTypeLevel) {
        typeLevelDao.add(deviceTypeLevel);
        if(!StringUtil.isNUll(deviceTypeLevel.getId())){
            //添加成功，则生成对应的自定义告警等级
            addAlarmLevelByDeviceLevel(deviceTypeLevel);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(String deviceTypeLevelId) {
        DeviceTypeLevel deviceTypeLevel = get(deviceTypeLevelId);
        boolean delete = typeLevelDao.delete(deviceTypeLevelId);
        if(delete){
            deleteAlarmLevels(deviceTypeLevel);
        }
        return delete;
    }

    @Override
    public boolean update(DeviceTypeLevel deviceTypeLevel) {
        DeviceTypeLevel sourceDeviceLevel = get(deviceTypeLevel.getId());
        boolean update = typeLevelDao.update(deviceTypeLevel);
        if(update){
            //删除之前设备型号等级对应的告警等级
            deleteAlarmLevels(sourceDeviceLevel);
            //修改成功后，生成新的告警自定义等级
            addAlarmLevelByDeviceLevel(deviceTypeLevel);
        }
        return update;
    }

    /**
     * @param deviceTypeLevelId
     * @auther: liudd
     * @date: 2019/9/26 13:47
     * 功能描述:根据id获取
     */
    @Override
    public DeviceTypeLevel get(String deviceTypeLevelId) {
        return typeLevelDao.get(deviceTypeLevelId);
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
    public boolean isRepeat(DeviceTypeLevel typeLevel) {
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

    /**
     * @auther: liudd
     * @date: 2019/9/26 13:18
     * 功能描述:根据设备型号告警等级，删除对应的自定义告警等级
     */
    @Override
    public int deleteAlarmLevels(DeviceTypeLevel deviceTypeLevel) {
        if(null != deviceTypeLevel){
            //删除该设备型号原来对应的告警等级
            AlarmLevelQuery alarmLevelQuery = AlarmLevelQuery.deviceTypeLevel2AlarmLevelQuery(deviceTypeLevel);
            return alarmLevelService.deleteList(alarmLevelQuery);
        }
        return 0;
    }

    /**
     * @param deviceTypeLevel
     * @auther: liudd
     * @date: 2019/9/26 13:33
     * 功能描述:根据设备型号告警，生成新告警自定义等级
     */
    @Override
    public boolean addAlarmLevelByDeviceLevel(DeviceTypeLevel deviceTypeLevel) {
        EnterpriseLevelQuery enterpriseLevelQuery = EnterpriseLevelQuery.deviceLevel2EnterpriseLevel(deviceTypeLevel);
        EnterpriseLevel maxLevelSinceLevel = enterpriseLevelService.getMaxLevelSinceLevel(enterpriseLevelQuery);
        if(null != maxLevelSinceLevel){
            AlarmLevel alarmLevel = alarmLevelService.createAlarmLevel(maxLevelSinceLevel, deviceTypeLevel);
            if(null != alarmLevel){
                return alarmLevelService.save(alarmLevel);
            }
        }
        return false;
    }
}

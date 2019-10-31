package com.kongtrolink.framework.service.impl;

import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.dao.DeviceTypeLevelDao;
import com.kongtrolink.framework.enttiy.AlarmLevel;
import com.kongtrolink.framework.enttiy.DeviceTypeLevel;
import com.kongtrolink.framework.enttiy.EnterpriseLevel;
import com.kongtrolink.framework.query.DeviceTypeLevelQuery;
import com.kongtrolink.framework.service.AlarmLevelService;
import com.kongtrolink.framework.service.DeviceTypeLevelService;
import com.kongtrolink.framework.service.EnterpriseLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
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
            //删除设备对应的等级关系
            int result = alarmLevelService.deleteList(deviceTypeLevel.getEnterpriseCode(), deviceTypeLevel.getServerCode(),
                    deviceTypeLevel.getDeviceType(), deviceTypeLevel.getDeviceModel());
            if(result >0 ){
                delete = true;
            }else {
                delete = false;
            }
        }
        return delete;
    }

    @Override
    public boolean update(DeviceTypeLevel deviceTypeLevel) {
        boolean update = typeLevelDao.update(deviceTypeLevel);
        if(update){
            //删除之前设备型号等级对应的告警等级
            alarmLevelService.deleteList(deviceTypeLevel.getEnterpriseCode(), deviceTypeLevel.getServerCode(),
                    deviceTypeLevel.getDeviceType(), deviceTypeLevel.getDeviceModel());
            //添加告警等级
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
        levelQuery.setEnterpriseCode(typeLevel.getEnterpriseCode());
        levelQuery.setServerCode(typeLevel.getServerCode());
        levelQuery.setDeviceType(typeLevel.getDeviceType());
        levelQuery.setDeviceModel(typeLevel.getDeviceModel());
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
     * @param deviceTypeLevel
     * @auther: liudd
     * @date: 2019/9/26 13:33
     * 功能描述:根据设备型号告警，生成新告警自定义等级
     */
    @Override
    public boolean addAlarmLevelByDeviceLevel(DeviceTypeLevel deviceTypeLevel) {
        List<EnterpriseLevel> lastUse = enterpriseLevelService.getLastUse(deviceTypeLevel.getEnterpriseCode(), deviceTypeLevel.getServerCode());
        for(Integer level : deviceTypeLevel.getLevels()){
            EnterpriseLevel match = getMatch(lastUse, level);
            AlarmLevel alarmLevel = new AlarmLevel(deviceTypeLevel.getEnterpriseCode(), deviceTypeLevel.getServerCode(),
                    deviceTypeLevel.getDeviceType(), deviceTypeLevel.getDeviceModel());
            alarmLevel.setEnterpriseName(deviceTypeLevel.getEnterpriseName());
            alarmLevel.setServerName(deviceTypeLevel.getServerName());
            alarmLevel.setSourceLevel(level);
            alarmLevel.setTargetLevel(match.getLevel());
            alarmLevel.setTargetLevelName(match.getLevelName());
            alarmLevel.setColor(match.getColor());
            alarmLevel.setUpdateTime(new Date());
            alarmLevelService.save(alarmLevel);
        }
        return true;
    }

    private EnterpriseLevel getMatch( List<EnterpriseLevel> enterpriseLevels, Integer level){
        EnterpriseLevel resultEnter = null;
        for(EnterpriseLevel enterpriseLevel : enterpriseLevels){
            if(enterpriseLevel.getLevel() > level){
                continue;
            }
            resultEnter = enterpriseLevel;
            break;
        }
        return resultEnter;
    }

    /**
     * @param enterpriseCode
     * @param serverCode
     * @auther: liudd
     * @date: 2019/10/16 16:13
     * 功能描述:根据企业信息获取所有设备等级
     */
    @Override
    public List<DeviceTypeLevel> listByEnterpriseInfo(String enterpriseCode, String serverCode) {
        return typeLevelDao.listByEnterpriseInfo(enterpriseCode, serverCode);
    }
}

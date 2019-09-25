package com.kongtrolink.framework.service.impl;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.dao.AlarmLevelDao;
import com.kongtrolink.framework.enttiy.AlarmLevel;
import com.kongtrolink.framework.enttiy.DeviceTypeLevel;
import com.kongtrolink.framework.enttiy.EnterpriseLevel;
import com.kongtrolink.framework.query.AlarmLevelQuery;
import com.kongtrolink.framework.query.EnterpriseLevelQuery;
import com.kongtrolink.framework.service.AlarmLevelService;
import com.kongtrolink.framework.service.EnterpriseLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/11 15:06
 * @Description:
 */
@Service
public class AlarmLevelServiceImpl implements AlarmLevelService {
    @Autowired
    AlarmLevelDao alarmLevelDao;
    @Autowired
    EnterpriseLevelService enterpriseLevelService;

    @Override
    public void save(AlarmLevel alarmLevel) {
        alarmLevel.setUpdateTime(new Date());
        alarmLevelDao.save(alarmLevel);
    }

    @Override
    public boolean delete(String alarmLevelId) {
        return alarmLevelDao.delete(alarmLevelId);
    }

    @Override
    public boolean update(AlarmLevel alarmLevel) {
        alarmLevel.setUpdateTime(alarmLevel.getUpdateTime());
        return alarmLevelDao.update(alarmLevel);
    }

    @Override
    public List<AlarmLevel> list(AlarmLevelQuery levelQuery) {
        return alarmLevelDao.list(levelQuery);
    }

    @Override
    public int count(AlarmLevelQuery levelQuery) {
        return alarmLevelDao.count(levelQuery);
    }

    @Override
    public AlarmLevel getOne(AlarmLevelQuery alarmLevelQuery) {
        return alarmLevelDao.getOne(alarmLevelQuery);
    }

    @Override
    public boolean isRepeat(AlarmLevel alarmLevel) {
        AlarmLevelQuery levelQuery = AlarmLevelQuery.entity2Query(alarmLevel);
        AlarmLevel one = getOne(levelQuery);
        if(null == one){
            return false;
        }
        if(one.getId().equals(alarmLevel.getId())){
            return false;
        }
        return true;
    }

    /**
     * @param levelQuery
     * @auther: liudd
     * @date: 2019/9/21 9:59
     * 功能描述：根据uniqueCode， service，deviceType， sourceLevel获取告警等级
     * 如果没有，则使用该系统默认告警点等级
     * 并将新告警等级和告警颜色等存入告警
     */
    @Override
    public AlarmLevel getAlarmLevel(AlarmLevelQuery levelQuery) {
        AlarmLevel one = getOne(levelQuery);
        if(null == one) {
            //没有找到匹配的等级自定义，使用系统默认等级
            EnterpriseLevelQuery enterpriseLevelQuery = new EnterpriseLevelQuery();
            enterpriseLevelQuery.setUniqueCode(levelQuery.getUniqueCode());
            enterpriseLevelQuery.setService(levelQuery.getService());
            enterpriseLevelQuery.setDefaultLevel(Contant.YES);
            EnterpriseLevel enterpriseLevel = enterpriseLevelService.getOne(enterpriseLevelQuery);
            if (null != enterpriseLevel) {
                one = new AlarmLevel();
                one.setSourceLevel(levelQuery.getSourceLevel());
                one.setTargetLevel(enterpriseLevel.getLevel());
                one.setColor(enterpriseLevel.getColor());
            }
        }
        return one;
    }

    @Override
    public AlarmLevel createAlarmLevel(EnterpriseLevel enterpriseLevel, DeviceTypeLevel deviceTypeLevel) {
        if(null == enterpriseLevel || null == deviceTypeLevel) {
            return null;
        }
        AlarmLevel alarmLevel = new AlarmLevel();
        alarmLevel.setUniqueCode(enterpriseLevel.getUniqueCode());
        alarmLevel.setService(enterpriseLevel.getService());
        alarmLevel.setDeviceType(deviceTypeLevel.getDeviceType());
        alarmLevel.setDeviceModel(deviceTypeLevel.getDeviceModel());
        alarmLevel.setSourceLevel(deviceTypeLevel.getLevel());
        alarmLevel.setTargetLevel(enterpriseLevel.getLevel());
        alarmLevel.setColor(enterpriseLevel.getColor());
        alarmLevel.setGenerate(Contant.SYSTEM);
        return alarmLevel;
    }
}

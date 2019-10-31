package com.kongtrolink.framework.service;

import com.kongtrolink.framework.dao.AlarmLevelDao;
import com.kongtrolink.framework.dao.EnterpriseLevelDao;
import com.kongtrolink.framework.enttiy.Alarm;
import com.kongtrolink.framework.enttiy.AlarmLevel;
import com.kongtrolink.framework.enttiy.EnterpriseLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: liudd
 * @Date: 2019/10/12 14:09
 * @Description:
 */
@Service
public class AlarmLevelService {

    @Autowired
    AlarmLevelDao levelDao;
    @Autowired
    EnterpriseLevelDao enterpriseLevelDao;

    public AlarmLevel getLevelByAlarm(Alarm alarm){
        String enterpriseCode = alarm.getEnterpriseCode();
        String serverCode = alarm.getServerCode();
        String deviceType = alarm.getDeviceType();
        String deviceModel = alarm.getDeviceModel();
        Integer level = alarm.getLevel();
        AlarmLevel alarmLevel = levelDao.matchLevel(enterpriseCode, serverCode, deviceType, deviceModel, level);
        if(null == alarmLevel) {
            EnterpriseLevel enterpriseLevel = enterpriseLevelDao.matchLevel(enterpriseCode, serverCode, level);
            if (null != enterpriseLevel) {
                alarmLevel = new AlarmLevel();
                alarmLevel.setTargetLevel(enterpriseLevel.getLevel());
                alarmLevel.setTargetLevelName(enterpriseLevel.getLevelName());
                alarmLevel.setColor(enterpriseLevel.getColor());
            }
        }
        return alarmLevel;
    }
}

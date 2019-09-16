package com.kongtrolink.service.impl;

import com.kongtrolink.base.StringUtil;
import com.kongtrolink.dao.AlarmLevelDao;
import com.kongtrolink.enttiy.AlarmLevel;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.query.AlarmLevelQuery;
import com.kongtrolink.service.AlarmLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    @Override
    public void save(AlarmLevel alarmLevel) {
        alarmLevelDao.save(alarmLevel);
    }

    @Override
    public boolean delete(String alarmLevelId) {
        return alarmLevelDao.delete(alarmLevelId);
    }

    @Override
    public boolean update(AlarmLevel alarmLevel) {
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
    public List<AlarmLevel> getBySourceLevel(String sourceLevel) {
        return alarmLevelDao.getBySourceLevel(sourceLevel);
    }

    @Override
    public String checkRepeatSource(AlarmLevel alarmLevel) {
        List<String> sourceLevelList = alarmLevel.getSourceLevelList();
        StringBuilder stringBuilder = new StringBuilder();
        for(String sourceLevel : sourceLevelList){
            List<AlarmLevel> bySourceLevel = getBySourceLevel(sourceLevel);
            for(AlarmLevel dbAlarmLevel : bySourceLevel){
                if(!dbAlarmLevel.getId().equals(alarmLevel.getId())){
                    stringBuilder.append(sourceLevel).append(",");
                }
            }
        }
        String repeatStr = stringBuilder.toString();
        if(!StringUtil.isNUll(repeatStr)){
            repeatStr = repeatStr.substring(0, repeatStr.lastIndexOf(","));
            return repeatStr;
        }
        return null;
    }

    /**
     * @param uniqueCode
     * @param sourceLevel
     * @auther: liudd
     * @date: 2019/9/16 16:53
     * 功能描述:根据告警原等级，获取告警自定义等级
     */
    @Override
    public String getTargetLevel(String uniqueCode, String sourceLevel) {
        List<AlarmLevel> targetAlarmLevelList = alarmLevelDao.getTargetLevel(uniqueCode, sourceLevel);
        if(targetAlarmLevelList.size() == 1){
            return targetAlarmLevelList.get(0).getTargetLevel();
        }
        if(targetAlarmLevelList.size() > 1){
            for(AlarmLevel alarmLevel : targetAlarmLevelList){
                if(!StringUtil.isNUll(alarmLevel.getUniqueCode())){
                    return alarmLevel.getTargetLevel();
                }
            }
        }
        return null;
    }
}

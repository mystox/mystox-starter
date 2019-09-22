package com.kongtrolink.service.impl;

import com.kongtrolink.base.Contant;
import com.kongtrolink.dao.AlarmCycleDao;
import com.kongtrolink.enttiy.AlarmCycle;
import com.kongtrolink.query.AlarmCycleQuery;
import com.kongtrolink.service.AlarmCycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: liudd
 * @Date: 2019/9/21 11:00
 * @Description:
 */
@Service
public class AlarmCycleServiceImpl implements AlarmCycleService {

    @Autowired
    AlarmCycleDao cycleDao;

    @Override
    public void save(AlarmCycle alarmCycle) {
        cycleDao.save(alarmCycle);
    }

    @Override
    public boolean delete(String alarmCycleCycleId) {
        return cycleDao.delete(alarmCycleCycleId);
    }

    @Override
    public boolean update(AlarmCycle alarmCycle) {
        return cycleDao.update(alarmCycle);
    }

    @Override
    public List<AlarmCycle> list(AlarmCycleQuery cycleQuery) {
        return cycleDao.list(cycleQuery);
    }

    @Override
    public int count(AlarmCycleQuery cycleQuery) {
        return cycleDao.count(cycleQuery);
    }

    @Override
    public AlarmCycle getOne(AlarmCycleQuery alarmCycleQuery) {
        return cycleDao.getOne(alarmCycleQuery);
    }

    @Override
    public Map<String, AlarmCycle> entity2CodeSerrviceMap(List<AlarmCycle> alarmCycleList) {
        Map<String, AlarmCycle> map = new HashMap<>();
        if(null == alarmCycleList || alarmCycleList.size() == 0){
            return map;
        }
        for(AlarmCycle alarmCycle : alarmCycleList){
            map.put(alarmCycle.getUniqueCode() + Contant.UNDERLINE + alarmCycle.getService(), alarmCycle);
        }
        return map;
    }
}

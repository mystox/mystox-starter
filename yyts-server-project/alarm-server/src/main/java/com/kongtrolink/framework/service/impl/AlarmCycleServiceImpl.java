package com.kongtrolink.framework.service.impl;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.dao.AlarmCycleDao;
import com.kongtrolink.framework.enttiy.AlarmCycle;
import com.kongtrolink.framework.query.AlarmCycleQuery;
import com.kongtrolink.framework.service.AlarmCycleService;
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

    @Override
    public boolean updateState(AlarmCycleQuery cycleQuery) {
        //如果是禁用，直接禁用；如果是启用，需要先禁用当前启用的规则
        String state = cycleQuery.getState();
        if(Contant.FORBIT.equals(state)){
            cycleDao.updateState(cycleQuery);
        }
        String sourceId = cycleQuery.getId();
        cycleQuery.setId(null);
        cycleQuery.setState(Contant.FORBIT);
        cycleDao.updateState(cycleQuery);
        //启用新规则
        cycleQuery.setId(sourceId);
        cycleQuery.setState(Contant.USEING);
        boolean result = cycleDao.updateState(cycleQuery);
        return result;
    }
}

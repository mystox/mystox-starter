package com.kongtrolink.service;

import com.kongtrolink.enttiy.AlarmCycle;
import com.kongtrolink.query.AlarmCycleQuery;

import java.util.List;
import java.util.Map;

/**
 * @Auther: liudd
 * @Date: 2019/9/21 11:00
 * @Description:
 */
public interface AlarmCycleService {

    void save(AlarmCycle alarmCycle);

    boolean delete(String alarmCycleCycleId);

    boolean update(AlarmCycle alarmCycle);

    List<AlarmCycle> list(AlarmCycleQuery cycleQuery);

    int count(AlarmCycleQuery cycleQuery);

    AlarmCycle getOne(AlarmCycleQuery alarmCycleQuery);

    Map<String, AlarmCycle> entity2CodeSerrviceMap(List<AlarmCycle> alarmCycleList);
}

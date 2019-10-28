package com.kongtrolink.framework.service;

import com.kongtrolink.framework.enttiy.AlarmCycle;
import com.kongtrolink.framework.query.AlarmCycleQuery;

import java.util.List;
import java.util.Map;

/**
 * @Auther: liudd
 * @Date: 2019/9/21 11:00
 * @Description:
 */
public interface AlarmCycleService {

    boolean save(AlarmCycle alarmCycle);

    boolean delete(String alarmCycleId);

    boolean update(AlarmCycle alarmCycle);

    AlarmCycle get(String alarmCycleId);

    List<AlarmCycle> list(AlarmCycleQuery cycleQuery);

    int count(AlarmCycleQuery cycleQuery);

    AlarmCycle getOne(AlarmCycleQuery alarmCycleQuery);

    Map<String, AlarmCycle> entity2CodeSerrviceMap(List<AlarmCycle> alarmCycleList);

    boolean updateState(AlarmCycleQuery cycleQuery);

    AlarmCycle getLastUpdateOne(AlarmCycleQuery alarmCycleQuery);
}

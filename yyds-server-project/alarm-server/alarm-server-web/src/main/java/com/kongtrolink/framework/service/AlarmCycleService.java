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

    void initAlarmCycle();

    /**
     * @auther: liudd
     * @date: 2019/12/27 11:09
     * 功能描述:处理企业默认告警周期
     */
    void handleUniqueDefault(String enterpriseServer, String serverCode);

    /**
     * @auther: liudd
     * @date: 2019/12/27 14:55
     * 功能描述:根据名称获取
     */
    AlarmCycle getByName(String enterpriseServer, String serverCode, String name);

    /**
     * @auther: liudd
     * @date: 2019/12/28 14:58
     * 功能描述:获取最后在使用的告警周期规则
     */
    AlarmCycle getLastUse(String enterpriseServer, String serverCode);
}

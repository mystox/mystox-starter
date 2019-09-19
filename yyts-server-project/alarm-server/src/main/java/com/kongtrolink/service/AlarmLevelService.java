package com.kongtrolink.service;

import com.kongtrolink.enttiy.AlarmLevel;
import com.kongtrolink.query.AlarmLevelQuery;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/11 15:06
 * @Description:
 */
public interface AlarmLevelService {

    void save(AlarmLevel alarmLevel);

    boolean delete(String alarmLevelId);

    boolean update(AlarmLevel alarmLevel);

    List<AlarmLevel> list(AlarmLevelQuery levelQuery);

    int count(AlarmLevelQuery levelQuery);

    List<AlarmLevel> getBySourceLevel(String sourceLevel);

    String checkRepeatSource(AlarmLevel alarmLevel);

    /**
     * @auther: liudd
     * @date: 2019/9/16 16:53
     * 功能描述:根据告警原等级，获取告警自定义等级
     */
    String getTargetLevel(String uniqueCode, String sourceLevel);
}

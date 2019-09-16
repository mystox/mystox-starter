package com.kongtrolink.service;

import com.kongtrolink.enttiy.Alarm;
import com.kongtrolink.query.AlarmQuery;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/11 14:42
 * @Description:
 */
public interface AlarmService {

    /**
     * @auther: liudd
     * @date: 2019/9/11 14:48
     * 功能描述:保存告警
     */
    void save(Alarm alarm);

    /**
     * @auther: liudd
     * @date: 2019/9/11 14:48
     * 功能描述:列表
     */
    List<Alarm> list(AlarmQuery alarmQuery);

    /**
     * @auther: liudd
     * @date: 2019/9/11 14:48
     * 功能描述:统计
     */
    int count(AlarmQuery alarmQuery);

    /**
     * @auther: liudd
     * @date: 2019/9/11 14:48
     * 功能描述:初始化查询条件
     */
    void initQuery(AlarmQuery alarmQuery);

    void saveRemove(Alarm alarm);

    List<Alarm> listRemove(AlarmQuery alarmQuery);

    int countRemove(AlarmQuery alarmQuery);

}

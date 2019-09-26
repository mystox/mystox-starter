package com.kongtrolink.framework.service;

import com.kongtrolink.framework.enttiy.Alarm;
import com.kongtrolink.framework.query.AlarmQuery;
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
    void save(Alarm alarm, String table);

    boolean delete(String alarmId, String table);

    boolean update(Alarm alarm, String table);

    /**
     * @auther: liudd
     * @date: 2019/9/11 14:48
     * 功能描述:列表
     */
    List<Alarm> list(AlarmQuery alarmQuery, String table);

    /**
     * @auther: liudd
     * @date: 2019/9/11 14:48
     * 功能描述:统计
     */
    int count(AlarmQuery alarmQuery, String table);

    /**
     * @auther: liudd
     * @date: 2019/9/24 9:08
     * 功能描述:批量删除
     */
    int deleteList(AlarmQuery alarmQuery, String table);

    void addList(List<Alarm> alarmList, String table);

    /**
     * @auther: liudd
     * @date: 2019/9/24 11:10
     * 功能描述:修改告警属性，包括附属属性
     */
    boolean updateProperties(Alarm alarm, String table);

    /**
     * @auther: liudd
     * @date: 2019/9/26 10:15
     * 功能描述:获取一个告警
     */
    Alarm getOne(AlarmQuery alarmQuery, String table);

    /**
     * @auther: liudd
     * @date: 2019/9/26 10:29
     * 功能描述:告警消除
     */
    boolean resolve(Alarm alarm, String table);
}

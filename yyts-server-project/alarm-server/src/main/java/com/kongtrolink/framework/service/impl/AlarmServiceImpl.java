package com.kongtrolink.framework.service.impl;

import com.kongtrolink.framework.dao.AlarmDao;
import com.kongtrolink.framework.enttiy.Alarm;
import com.kongtrolink.framework.query.AlarmQuery;
import com.kongtrolink.framework.service.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/11 14:42
 * @Description:
 */
@Service
public class AlarmServiceImpl implements AlarmService{

    @Autowired
    AlarmDao alarmDao;


    /**
     * @param alarm
     * @param table
     * @auther: liudd
     * @date: 2019/9/11 14:48
     * 功能描述:保存告警
     */
    @Override
    public void save(Alarm alarm, String table) {
        alarm.settReport(new Date());
        alarmDao.save(alarm, table);
    }

    @Override
    public boolean delete(String alarmId, String table) {
        return alarmDao.delete(alarmId, table);
    }

    @Override
    public boolean update(Alarm alarm, String table) {
        return alarmDao.update(alarm, table);
    }

    /**
     * @param alarmQuery
     * @param table
     * @auther: liudd
     * @date: 2019/9/11 14:48
     * 功能描述:列表
     */
    @Override
    public List<Alarm> list(AlarmQuery alarmQuery, String table) {
        return alarmDao.list(alarmQuery, table);
    }

    /**
     * @param alarmQuery
     * @param table
     * @auther: liudd
     * @date: 2019/9/11 14:48
     * 功能描述:统计
     */
    @Override
    public int count(AlarmQuery alarmQuery, String table) {
        return alarmDao.count(alarmQuery, table);
    }

    /**
     * @param alarmQuery
     * @param table
     * @auther: liudd
     * @date: 2019/9/24 9:08
     * 功能描述:批量删除
     */
    @Override
    public int deleteList(AlarmQuery alarmQuery, String table) {
        return alarmDao.deleteList(alarmQuery, table);
    }

    @Override
    public void addList(List<Alarm> alarmList, String table) {
        alarmDao.addList(alarmList, table);
    }

    /**
     * @param alarm
     * @param table
     * @auther: liudd
     * @date: 2019/9/24 11:10
     * 功能描述:修改告警属性，包括附属属性
     */
    @Override
    public boolean updateProperties(Alarm alarm, String table) {
        return alarmDao.updateProperties(alarm, table);
    }

    /**
     * @param alarmQuery
     * @param table
     * @auther: liudd
     * @date: 2019/9/26 10:15
     * 功能描述:获取一个告警
     */
    @Override
    public Alarm getOne(AlarmQuery alarmQuery, String table) {
        return alarmDao.getOne(alarmQuery, table);
    }

    /**
     * @param alarm
     * @param table
     * @auther: liudd
     * @date: 2019/9/26 10:29
     * 功能描述:告警消除
     */
    @Override
    public boolean resolve(Alarm alarm, String table) {
        return alarmDao.resolve(alarm, table);
    }
}

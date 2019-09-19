package com.kongtrolink.service.impl;

import com.kongtrolink.dao.AlarmDao;
import com.kongtrolink.enttiy.Alarm;
import com.kongtrolink.query.AlarmQuery;
import com.kongtrolink.service.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     * @auther: liudd
     * @date: 2019/9/11 14:48
     * 功能描述:保存告警
     */
    @Override
    public void save(Alarm alarm) {

    }

    /**
     * @param alarmQuery
     * @auther: liudd
     * @date: 2019/9/11 14:48
     * 功能描述:列表
     */
    @Override
    public List<Alarm> list(AlarmQuery alarmQuery) {
        return null;
    }

    /**
     * @param alarmQuery
     * @auther: liudd
     * @date: 2019/9/11 14:48
     * 功能描述:统计
     */
    @Override
    public int count(AlarmQuery alarmQuery) {
        return 0;
    }

    /**
     * @param alarmQuery
     * @auther: liudd
     * @date: 2019/9/11 14:48
     * 功能描述:初始化查询条件
     */
    @Override
    public void initQuery(AlarmQuery alarmQuery) {

    }

    @Override
    public void saveRemove(Alarm alarm) {

    }

    @Override
    public List<Alarm> listRemove(AlarmQuery alarmQuery) {
        return null;
    }

    @Override
    public int countRemove(AlarmQuery alarmQuery) {
        return 0;
    }
}

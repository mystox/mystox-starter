package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.scloud.dao.AlarmFocusDao;
import com.kongtrolink.framework.scloud.entity.AlarmFocus;
import com.kongtrolink.framework.scloud.query.AlarmFocusQuery;
import com.kongtrolink.framework.scloud.service.AlarmFocusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/2/28 13:16
 * @Description:
 */
@Service
public class AlarmFocusServiceImpl implements AlarmFocusService {

    @Autowired
    AlarmFocusDao alarmFocusDao;

    @Override
    public boolean add(String uniqueCode, AlarmFocus alarmFocus) {
        return alarmFocusDao.add(uniqueCode, alarmFocus);
    }

    @Override
    public boolean delete(String uniqueCode, String id) {
        return alarmFocusDao.delete(uniqueCode, id);
    }

    @Override
    public List<AlarmFocus> list(String uniqueCode, AlarmFocusQuery alarmFocusQuery) {
        return alarmFocusDao.list(uniqueCode, alarmFocusQuery);
    }

    @Override
    public int count(String uniqueCode, AlarmFocusQuery alarmFocusQuery) {
        return alarmFocusDao.count(uniqueCode, alarmFocusQuery);
    }
}
package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.scloud.dao.RedefineAlarmDao;
import com.kongtrolink.framework.scloud.entity.RedefineAlarm;
import com.kongtrolink.framework.scloud.query.RedefineAlarmQuery;
import com.kongtrolink.framework.scloud.service.RedefineAlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/13 14:54
 * @Description:
 */
@Service
public class RedefineAlarmServiceImpl implements RedefineAlarmService {

    @Autowired
    RedefineAlarmDao redefineAlarmDao;

    @Override
    public boolean add(String uniqueCode, RedefineAlarm redefineAlarm) {
        return redefineAlarmDao.add(uniqueCode, redefineAlarm);
    }

    @Override
    public List<RedefineAlarm> list(String uniqueCode, RedefineAlarmQuery redefineAlarmQuery) {
        return redefineAlarmDao.list(uniqueCode, redefineAlarmQuery);
    }

    @Override
    public int count(String uniqueCode, RedefineAlarmQuery redefineAlarmQuery) {
        return redefineAlarmDao.count(uniqueCode, redefineAlarmQuery);
    }
}

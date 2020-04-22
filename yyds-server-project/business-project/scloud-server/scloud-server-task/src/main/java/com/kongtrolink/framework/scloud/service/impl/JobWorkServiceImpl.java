package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.scloud.dao.JobWorkDao;
import com.kongtrolink.framework.scloud.entity.*;
import com.kongtrolink.framework.scloud.service.JobWorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/8 16:55
 * @Description:
 */
@Service
public class JobWorkServiceImpl implements JobWorkService {

    @Autowired
    JobWorkDao jobWorkDao;

    @Override
    public List<WorkAlarmConfig> getAllWorkAlarmConfig(String uniqueCode, Date curDate) {
        return jobWorkDao.getAllWorkAlarmConfig(uniqueCode, curDate);
    }

    @Override
    public WorkConfig getWorkConfigById(String uniqueCode, String workConfigId) {
        return jobWorkDao.getWorkConfigById(uniqueCode, workConfigId);
    }

    /**
     * @param uniqueCode
     * @param deviceCode
     * @auther: liudd
     * @date: 2020/4/8 17:12
     * 功能描述:获取该设备未回单的告警工单
     */
    @Override
    public Work getNoOverWorkByDeviceCode(String uniqueCode, String deviceCode) {
        return jobWorkDao.getNoOverWorkByDeviceCode(uniqueCode, deviceCode);
    }

    @Override
    public void addWork(String uniqueCode, Work work) {
        jobWorkDao.addWork(uniqueCode, work);
    }

    @Override
    public void updateWork(String uniqueCode, Work work) {
        jobWorkDao.updateWork(uniqueCode, work);
    }

    @Override
    public void addWorkRecord(String uniqueCode, WorkRecord workRecord) {
        jobWorkDao.addWorkRecord(uniqueCode, workRecord);
    }

    @Override
    public void deleteWorkAlarmConfigById(String uniqueCode, String workAlarmConfigId) {
        jobWorkDao.deleteWorkAlarmConfigById(uniqueCode, workAlarmConfigId);
    }

    /**
     * @param uniqueCode
     * @param alarmBusiness
     * @auther: liudd
     * @date: 2020/4/11 15:03
     * 功能描述:修改告警的工单编码
     */
    @Override
    public boolean updateAlarmWorkCode(String uniqueCode, AlarmBusiness alarmBusiness) {
        return jobWorkDao.updateAlarmWorkCode(uniqueCode, alarmBusiness);
    }
}

package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.scloud.dao.JobWorkDao;
import com.kongtrolink.framework.scloud.entity.Work;
import com.kongtrolink.framework.scloud.entity.WorkAlarmConfig;
import com.kongtrolink.framework.scloud.entity.WorkConfig;
import com.kongtrolink.framework.scloud.entity.WorkRecord;
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
    public List<WorkAlarmConfig> getAllWorkAlarmConfig(Date curDate) {
        return jobWorkDao.getAllWorkAlarmConfig(curDate);
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
    public void deleteWorkAlarmConfigById(String workAlarmConfigId) {
        jobWorkDao.deleteWorkAlarmConfigById(workAlarmConfigId);
    }
}

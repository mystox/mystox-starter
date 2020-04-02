package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.scloud.dao.WorkAlarmConfigDao;
import com.kongtrolink.framework.scloud.entity.WorkAlarmConfig;
import com.kongtrolink.framework.scloud.service.WorkAlarmConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 告警工单配置对应表service实现类
 */
@Service
public class WorkAlarmConfigServiceImpl implements WorkAlarmConfigService{

    @Autowired
    WorkAlarmConfigDao workAlarmConfigDao;

    /**
     * 添加：
     * 如果该告警所在设备没有未回单的工单，则添加。
     * 否则不添加，在该工单中添加告警信息。并且将该工单id加入该告警数据中
     */
    @Override
    public void add(WorkAlarmConfig workAlarmConfig) {
        workAlarmConfigDao.add(workAlarmConfig);
    }

    /**
     * 根据企业编码和告警id获取记录
     * @param uniqueCode
     * @param alarmId
     * @return
     */
    public WorkAlarmConfig findByAlarmId(String uniqueCode, String alarmId){
        return workAlarmConfigDao.findByAlarmId(uniqueCode, alarmId);
    }

    /**
     * 根据告警id删除数据，
     * @param uniqueCode
     * @param alarmId
     * @return
     */
    public void deleteByAlarmId(String uniqueCode, String alarmId){
        workAlarmConfigDao.deleteByAlarmId(uniqueCode, alarmId);
    }
}

package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.entity.Work;
import com.kongtrolink.framework.scloud.entity.WorkAlarmConfig;
import com.kongtrolink.framework.scloud.entity.WorkConfig;
import com.kongtrolink.framework.scloud.entity.WorkRecord;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/8 16:55
 * @Description:
 */
public interface JobWorkService {

    List<WorkAlarmConfig> getAllWorkAlarmConfig(Date curDate);

    WorkConfig getWorkConfigById(String uniqueCode, String workConfigId);

    /**
     * @auther: liudd
     * @date: 2020/4/8 17:12
     * 功能描述:获取该设备未回单的告警工单
     */
    Work getNoOverWorkByDeviceCode(String uniqueCode, String deviceCode);

    void addWork(String uniqueCode, Work work);

    void updateWork(String uniqueCode, Work work);

    void addWorkRecord(String uniqueCode, WorkRecord workRecord);

    void deleteWorkAlarmConfigById(String workAlarmConfigId);
}

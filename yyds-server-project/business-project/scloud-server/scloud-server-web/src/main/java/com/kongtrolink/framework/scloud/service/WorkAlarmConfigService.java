package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.entity.WorkAlarmConfig;

/**
 * 告警工单配置对应表
 */
public interface WorkAlarmConfigService {

    /**
     * 添加
     */
    void add(WorkAlarmConfig workAlarmConfig);

    /**
     * 根据企业编码和告警id获取记录
     * @param uniqueCode
     * @param alarmId
     * @return
     */
    WorkAlarmConfig findByAlarmId(String uniqueCode, String alarmId);

    /**
     * 根据告警id删除数据，
     * @param uniqueCode
     * @param alarmId
     * @return
     */
    void deleteByAlarmId(String uniqueCode, String alarmId);
}

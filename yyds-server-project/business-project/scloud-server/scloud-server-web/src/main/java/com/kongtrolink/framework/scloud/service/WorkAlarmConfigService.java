package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.entity.Alarm;
import com.kongtrolink.framework.scloud.entity.WorkAlarmConfig;

import java.util.List;

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
     * @return
     */
    WorkAlarmConfig findByAlarmKey(String uniqueCode, String alarmKey);

    /**
     * 根据告警id删除数据，
     * @param uniqueCode
     * @return
     */
    void deleteByAlarmKey(String uniqueCode, String alarmKey);

    void matchAutoConfig(List<Alarm> alarmList);
}

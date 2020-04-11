package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.entity.*;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/8 16:55
 * @Description:
 */
public interface JobWorkService {

    List<WorkAlarmConfig> getAllWorkAlarmConfig(String uniqueCode, Date curDate);

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

    /**
     * @auther: liudd
     * @date: 2020/4/8 19:05
     * 功能描述:添加告警业务信息.如果告警消除时，该告警还未派单，则删除告警工单配置信息。杜绝派单时告警已消除情况
     */
    void addAlarmBusiness(String uniqueCode, AlarmBusiness alarmBusiness);
}

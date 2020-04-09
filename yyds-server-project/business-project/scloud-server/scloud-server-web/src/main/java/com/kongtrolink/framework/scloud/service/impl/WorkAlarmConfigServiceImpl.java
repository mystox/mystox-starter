package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.scloud.constant.WorkConstants;
import com.kongtrolink.framework.scloud.dao.WorkAlarmConfigDao;
import com.kongtrolink.framework.scloud.entity.Alarm;
import com.kongtrolink.framework.scloud.entity.WorkAlarmConfig;
import com.kongtrolink.framework.scloud.entity.WorkConfig;
import com.kongtrolink.framework.scloud.service.WorkAlarmConfigService;
import com.kongtrolink.framework.scloud.service.WorkConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 告警工单配置对应表service实现类
 */
@Service
public class WorkAlarmConfigServiceImpl implements WorkAlarmConfigService{

    @Autowired
    WorkAlarmConfigDao workAlarmConfigDao;
    @Autowired
    WorkConfigService workConfigService;

    @Override
    public void matchAutoConfig(List<Alarm> alarmList) {
        for(Alarm alarm : alarmList) {
            WorkConfig workConfig = workConfigService.matchAutoConfig(alarm.getEnterpriseCode(), alarm, alarm.getSiteType(), WorkConstants.SEND_TYPE_AUTO);
            if (null != workConfig) {//20190404有些企业可能没有默认规则
                WorkAlarmConfig alarmWorkConfig = createAlarmWorkConfig(alarm.getEnterpriseCode(), workConfig, alarm);
                add(alarmWorkConfig);
            }
        }
    }

    /**
     * 生成AlarmWorkConfig
     * @param uniqueCode
     * @param workConfig
     * @param alarm
     * @return
     */
    private WorkAlarmConfig createAlarmWorkConfig(String uniqueCode, WorkConfig workConfig, Alarm alarm){
        WorkAlarmConfig alarmWorkConfig = new WorkAlarmConfig();
        alarmWorkConfig.setUniqueCode(uniqueCode);
        alarmWorkConfig.setWorkConfigId(workConfig.getId());
        alarmWorkConfig.setSiteCode(alarm.getSiteCode());
        alarmWorkConfig.setSiteName(alarm.getSiteName());
        alarmWorkConfig.setDeviceCode(alarm.getDeviceId());
        alarmWorkConfig.setDeviceName(alarm.getDeviceName());
        alarmWorkConfig.setDeviceType(alarm.getDeviceType());
        alarmWorkConfig.setAlarmName(alarm.getName());
        alarmWorkConfig.setAlarmLevel(alarm.getLevel());
        alarmWorkConfig.setTreport(alarm.getTreport());
        alarmWorkConfig.setAlarmState(alarm.getState());
        Date tReport = alarm.getTreport();
        alarmWorkConfig.setAlarmKey(alarm.initKey());
        //告警后启用时限毫秒值
        int alarmBeginTime = workConfig.getReportAfter() * 60 * 1000;
        Date sendWorkTime = new Date(tReport.getTime() + alarmBeginTime);
        alarmWorkConfig.setSendTime(sendWorkTime);
        alarmWorkConfig.setSendType(workConfig.getSendType());
        return alarmWorkConfig;
    }

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
     * @return
     */
    public WorkAlarmConfig findByAlarmKey(String uniqueCode, String alarmKey){
        return workAlarmConfigDao.findByAlarmKey(uniqueCode, alarmKey);
    }

    /**
     * 根据告警id删除数据，
     * @param uniqueCode
     * @return
     */
    public void deleteByAlarmKey(String uniqueCode, String alarmKey){
        workAlarmConfigDao.deleteByAlarmKey(uniqueCode, alarmKey);
    }
}

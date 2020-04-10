package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.scloud.dao.WorkConfigDao;
import com.kongtrolink.framework.scloud.entity.Alarm;
import com.kongtrolink.framework.scloud.entity.AlarmBusiness;
import com.kongtrolink.framework.scloud.entity.WorkConfig;
import com.kongtrolink.framework.scloud.query.WorkConfigQuery;
import com.kongtrolink.framework.scloud.service.WorkConfigService;
import com.kongtrolink.framework.scloud.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/1 10:29
 * @Description:
 */
@Service
public class WorkConfigServiceImpl implements WorkConfigService {

    @Autowired
    WorkConfigDao workConfigDao;

    @Override
    public void add(String uniqueCode, WorkConfig workConfig) {
        workConfigDao.add(uniqueCode, workConfig);
    }

    @Override
    public boolean delete(String uniqueCode, String workConfigId) {
        return workConfigDao.delete(uniqueCode, workConfigId);
    }

    @Override
    public boolean update(String uniqueCode, WorkConfig workConfig) {
        if(delete(uniqueCode, workConfig.getId())){
            add(uniqueCode, workConfig);
            return true;
        }
        return false;
    }

    @Override
    public List<WorkConfig> list(String uniqueCode, WorkConfigQuery workConfigQuery) {
        return workConfigDao.list(uniqueCode, workConfigQuery);
    }

    @Override
    public int count(String uniqueCode, WorkConfigQuery workConfigQuery) {
        return workConfigDao.count(uniqueCode, workConfigQuery);
    }

    @Override
    public WorkConfig getById(String uniqueCode, String workConfigId) {
        return workConfigDao.getById(uniqueCode, workConfigId);
    }

    /**
     * 获取告警最匹配的工单配置
     * @param uniqueCode
     * @return
     */
    @Override
    public WorkConfig matchAutoConfig(String uniqueCode, AlarmBusiness alarmBusiness, String sendWorkType) {
        Date tReport = alarmBusiness.getTreport();
        String format = StringUtil.sdf.format(tReport);
        //HH:mm:ss
        String date = format.substring(11);
        int intReportTime= StringUtil.dateToInt(date);
        WorkConfigQuery workConfigQuery = new WorkConfigQuery();
        workConfigQuery.setSiteType(alarmBusiness.getSiteType());
        workConfigQuery.setIntTreport(intReportTime);
        workConfigQuery.setSiteCode(alarmBusiness.getSiteCode());
        workConfigQuery.setAlarmLevel(alarmBusiness.getLevel());
        workConfigQuery.setAlarmName(alarmBusiness.getName());
        workConfigQuery.setSendType(sendWorkType);
        WorkConfig workConfig = workConfigDao.matchAutoConfig(uniqueCode, workConfigQuery);
        if(workConfig == null){
            workConfig = getDefaultConfig(uniqueCode);
        }
        return workConfig;
    }

    /**
     * @auther: liudd
     * @date: 2020/4/2 10:40
     * 功能描述:手动派单匹配工单配置
     */
    @Override
    public WorkConfig matchManualConfig(String uniqueCode, String siteCode) {
        WorkConfig workConfig = workConfigDao.matchManualConfig(uniqueCode, siteCode);
        if(null == workConfig){
            workConfig = getDefaultConfig(uniqueCode);
        }
        return workConfig;
    }

    /**
     * @param uniqueCode
     * @auther: liudd
     * @date: 2020/4/2 10:47
     * 功能描述:获取默认告警工单配置，如果么有，则创建并返回
     */
    @Override
    public WorkConfig getDefaultConfig(String uniqueCode) {
        WorkConfig defaultConfig = workConfigDao.getDefaultConfig(uniqueCode);
//        if(null == defaultConfig){
//            defaultConfig = createDefaultConfig(uniqueCode);
//        }
        return defaultConfig;
    }

    /**
     * @param uniqueCode
     * @auther: liudd
     * @date: 2020/4/2 10:52
     * 功能描述:创建默认告警工单配置
     */
    @Override
    public synchronized WorkConfig createDefaultConfig(String uniqueCode) {
        WorkConfig defaultConfig = workConfigDao.getDefaultConfig(uniqueCode);
        if(null == defaultConfig) {
            defaultConfig = new WorkConfig();
            defaultConfig.setCsjdsc(20);
            defaultConfig.setCsjdtxjg(10);
            defaultConfig.setCshdsc(20);
            defaultConfig.setCshdtxjg(10);
            add(uniqueCode, defaultConfig);
        }
        return defaultConfig;
    }
}

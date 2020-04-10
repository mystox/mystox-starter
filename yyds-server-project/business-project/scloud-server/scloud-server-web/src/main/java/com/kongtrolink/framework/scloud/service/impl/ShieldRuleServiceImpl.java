package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.core.entity.Const;
import com.kongtrolink.framework.scloud.constant.WorkConstants;
import com.kongtrolink.framework.scloud.dao.ShieldRuleDao;
import com.kongtrolink.framework.scloud.entity.Alarm;
import com.kongtrolink.framework.scloud.entity.AlarmBusiness;
import com.kongtrolink.framework.scloud.entity.ShieldAlarm;
import com.kongtrolink.framework.scloud.entity.ShieldRule;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.entity.model.SiteModel;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.query.ShieldRuleQuery;
import com.kongtrolink.framework.scloud.query.SiteQuery;
import com.kongtrolink.framework.scloud.service.DeviceService;
import com.kongtrolink.framework.scloud.service.ShieldAlarmService;
import com.kongtrolink.framework.scloud.service.ShieldRuleService;
import com.kongtrolink.framework.scloud.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Auther: liudd
 * @Date: 2020/3/4 14:50
 * @Description:
 */
@Service
public class ShieldRuleServiceImpl implements ShieldRuleService {

    @Autowired
    ShieldRuleDao shieldRuleDao;
    @Autowired
    ShieldAlarmService shieldAlarmService;
    @Autowired
    DeviceService deviceService;
    @Autowired
    SiteService siteService;

    @Override
    public boolean add(String uniqueCode, ShieldRule shieldRule) {
        return shieldRuleDao.add(uniqueCode, shieldRule);
    }

    @Override
    public int delete(String uniqueCode, String shieldRuleId) {
        return shieldRuleDao.delete(uniqueCode, shieldRuleId);
    }

    @Override
    public boolean updateState(String uniqueCode, String shieldId, Boolean state) {
        return shieldRuleDao.updateState(uniqueCode, shieldId, state);
    }

    @Override
    public List<ShieldRule> list(String uniqueCode, ShieldRuleQuery shieldRuleQuery) {
        return shieldRuleDao.list(uniqueCode, shieldRuleQuery);
    }

    @Override
    public int count(String uniqueCode, ShieldRuleQuery ruleQuery) {
        return shieldRuleDao.count(uniqueCode, ruleQuery);
    }

    @Override
    public ShieldRule get(String uniqueCode, String ruleId) {
        return shieldRuleDao.get(uniqueCode, ruleId);
    }

    /**
     * @auther: liudd
     * @date: 2020/3/5 8:48
     * 功能描述:填充信息
     */
    @Override
    public void initInfo(String uniqueCode, ShieldRule shieldRule) throws Exception{
        //获取设备列表
        List<String> deviceCodeList = shieldRule.getDeviceCodeList();
        DeviceQuery deviceQuery = new DeviceQuery();
        deviceQuery.setServerCode(shieldRule.getServerCode());
        deviceQuery.setPageSize(Integer.MAX_VALUE);
        deviceQuery.setDeviceCodes(deviceCodeList);
        List<DeviceModel> deviceList = deviceService.findDeviceList(uniqueCode, deviceQuery);
        for(DeviceModel deviceModel : deviceList){
            String tierName = deviceModel.getTierName();
            String siteName = deviceModel.getSiteName();
            String name = deviceModel.getName();
            String deviceInfo = tierName + WorkConstants.COLON + siteName + WorkConstants.COLON + name;
            shieldRule.getDeviceInfoList().add(deviceInfo);
        }
    }

    /**
     * @auther: liudd
     * @date: 2020/3/4 16:08
     * 功能描述:匹配告警屏蔽规则
     */
    @Override
    public void matchRule(String uniqueCode, List<AlarmBusiness> alarmBusinessList) {
        List<ShieldRule> rules = shieldRuleDao.getEnables(uniqueCode);
//        List<ShieldAlarm> shieldAlarmList = new ArrayList<>();
        for (AlarmBusiness alarm : alarmBusinessList) {
            for (ShieldRule rule : rules) {
                String deviceId = alarm.getDeviceCode();
                if (rule.getDeviceCodeList().contains(deviceId) && rule.getAlarmlevel().contains(alarm.getLevel())) {
//                    ShieldAlarm shieldAlarm = new ShieldAlarm();
//                    shieldAlarm.setRuleId(rule.getId());
//                    shieldAlarm.setAlarmId(alarm.getId());
//                    shieldAlarm.setAlarmLevel(alarm.getLevel());
//                    shieldAlarm.setTreport(alarm.getTreport());
//                    shieldAlarm.setSiteName(alarm.getSiteName());
//                    shieldAlarm.setSiteAddress(alarm.getSiteAddress());
//                    shieldAlarm.setDeviceCode(alarm.getDeviceCode());
//                    shieldAlarm.setDeviceName(alarm.getDeviceName());
//                    shieldAlarm.setSignalId(alarm.getCntbId());
//                    shieldAlarm.setSignalName(alarm.getSignalName());
//                    shieldAlarmList.add(shieldAlarm);
                    alarm.setShield(true);
                    alarm.setShieldRuleId(rule.getId());
                }
            }
        }
//        shieldAlarmService.add(uniqueCode, shieldAlarmList);
    }
}

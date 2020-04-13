package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.scloud.dao.RedefineRuleDao;
import com.kongtrolink.framework.scloud.entity.AlarmBusiness;
import com.kongtrolink.framework.scloud.entity.RedefineAlarm;
import com.kongtrolink.framework.scloud.entity.RedefineRule;
import com.kongtrolink.framework.scloud.query.RedefineRuleQuery;
import com.kongtrolink.framework.scloud.service.RedefineAlarmService;
import com.kongtrolink.framework.scloud.service.RedefineRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/13 14:05
 * @Description:
 */
@Service
public class RedefineRuleServiceImpl implements RedefineRuleService {

    @Autowired
    RedefineRuleDao redefineRuleDao;
    @Autowired
    RedefineAlarmService redefineAlarmService;

    @Override
    public boolean add(String uniqueCode, RedefineRule redefineRule) {
        return redefineRuleDao.add(uniqueCode, redefineRule);
    }

    @Override
    public boolean delete(String uniqueCode, String id) {
        return redefineRuleDao.delete(uniqueCode, id);
    }

    @Override
    public boolean update(String uniqueCode, RedefineRule redefineRule) {
        return redefineRuleDao.update(uniqueCode, redefineRule);
    }

    @Override
    public List<RedefineRule> list(String uniqueCode, RedefineRuleQuery ruleQuery) {
        return redefineRuleDao.list(uniqueCode, ruleQuery);
    }

    @Override
    public int count(String uniqueCode, RedefineRuleQuery ruleQuery) {
        return redefineRuleDao.count(uniqueCode, ruleQuery);
    }

    @Override
    public RedefineRule getByName(String uniqueCode, String name) {
        return redefineRuleDao.getByName(uniqueCode, name);
    }

    @Override
    public boolean updateState(String uniqueCode, RedefineRuleQuery redefineRuleQuery) {
        return redefineRuleDao.updateState(uniqueCode, redefineRuleQuery);
    }

    /**
     * @auther: liudd
     * @date: 2020/4/13 16:15
     * 功能描述:匹配告警重定义
     */
    @Override
    public void matchRule(String uniqueCode, AlarmBusiness alarmBusiness) {
        RedefineRule redefineRule = redefineRuleDao.matchRule(uniqueCode, alarmBusiness.getSiteCode(), alarmBusiness.getDeviceType(), alarmBusiness.getCntbId());
        if(null != redefineRule){
            RedefineAlarm redefineAlarm = RedefineAlarm.init(redefineRule, alarmBusiness);
            boolean add = redefineAlarmService.add(uniqueCode, redefineAlarm);
            if(add){
                alarmBusiness.setLevel(redefineRule.getAlarmLevel());
            }
        }
    }
}

package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.scloud.dao.ShieldRuleDao;
import com.kongtrolink.framework.scloud.entity.Alarm;
import com.kongtrolink.framework.scloud.entity.ShieldRule;
import com.kongtrolink.framework.scloud.query.ShieldRuleQuery;
import com.kongtrolink.framework.scloud.service.ShieldRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/3/4 14:50
 * @Description:
 */
@Service
public class ShieldRuleServiceImpl implements ShieldRuleService {

    @Autowired
    ShieldRuleDao shieldRuleDao;

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

    /**
     * @param alarmList
     * @auther: liudd
     * @date: 2020/3/4 16:08
     * 功能描述:匹配告警屏蔽规则
     */
    @Override
    public void matchRule(List<Alarm> alarmList) {

    }
}

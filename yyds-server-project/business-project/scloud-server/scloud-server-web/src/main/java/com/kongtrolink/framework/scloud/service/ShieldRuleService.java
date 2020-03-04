package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.entity.Alarm;
import com.kongtrolink.framework.scloud.entity.ShieldRule;
import com.kongtrolink.framework.scloud.query.ShieldRuleQuery;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/3/4 14:50
 * @Description:
 */
public interface ShieldRuleService {

    boolean add(String uniqueCode, ShieldRule shieldRule);

    int delete(String uniqueCode, String shieldRuleId);

    boolean updateState(String uniqueCode, String shieldId, Boolean state);

    List<ShieldRule> list(String uniqueCode, ShieldRuleQuery shieldRuleQuery);

    int count(String uniqueCode, ShieldRuleQuery ruleQuery);

    /**
     * @auther: liudd
     * @date: 2020/3/4 16:08
     * 功能描述:匹配告警屏蔽规则
     */
    void matchRule(List<Alarm> alarmList);
}

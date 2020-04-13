package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.entity.AlarmBusiness;
import com.kongtrolink.framework.scloud.entity.RedefineRule;
import com.kongtrolink.framework.scloud.query.RedefineRuleQuery;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/13 14:05
 * @Description:
 */
public interface RedefineRuleService {

    boolean add(String uniqueCode, RedefineRule redefineRule);

    boolean delete(String uniqueCode, String id);

    boolean update(String uniqueCode, RedefineRule redefineRule);

    List<RedefineRule> list(String uniqueCode, RedefineRuleQuery ruleQuery);

    int count(String uniqueCode, RedefineRuleQuery ruleQuery);

    RedefineRule getByName(String uniqueCode, String name);

    boolean updateState(String uniqueCode, RedefineRuleQuery redefineRuleQuery);

    void matchRule(String uniqueCode, AlarmBusiness alarmBusiness);
}

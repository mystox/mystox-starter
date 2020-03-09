package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.entity.FilterRule;
import com.kongtrolink.framework.scloud.query.FilterRuleQuery;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/3/5 15:02
 * @Description:
 */
public interface FilterRuleService {

    boolean add(String uniqueCode, FilterRule filterRule);

    boolean delete(String uniqueCode, String filterRuleId);

    boolean update(String uniqueCode, FilterRule filterRule);

    List<FilterRule> list(String uniqueCode, FilterRuleQuery ruleQuery);

    int count(String uniqeuCode, FilterRuleQuery ruleQuery);

    boolean updateState(String uniqueCode, FilterRuleQuery ruleQuery);

    /**
     * @auther: liudd
     * @date: 2020/3/5 15:49
     * 功能描述:获取用户正在启用的过滤规则
     */
    FilterRule getUserInUse(String uniqueCode, String creatorId);



}

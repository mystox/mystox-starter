package com.kongtrolink.framework.service;

import com.kongtrolink.framework.enttiy.InformRule;
import com.kongtrolink.framework.query.InformRuleQuery;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/10/11 09:42
 * @Description:
 */
public interface InformRuleService {

    boolean save(InformRule deliver);

    boolean delete(String deliverId);

    boolean update(InformRule deliver);

    InformRule get(String ruleId);

    List<InformRule> list(InformRuleQuery deliverQuery);

    int count(InformRuleQuery deliverQuery);

    InformRule getOne(InformRuleQuery deliverQuery);

    InformRule getByName(String name);

    boolean updateStatus(String ruleId, String status);

    List<InformRule> getByTemplateIdAndType(String templateId, String type);

    /**
     * @auther: liudd
     * @date: 2019/10/29 9:59
     * 功能描述:火球系统默认告警投递规则
     */
    InformRule getSystemRule();

    /**
     * @auther: liudd
     * @date: 2019/10/29 9:59
     * 功能描述:初始化默认告警投递规则
     */
    void initInformRule();
}

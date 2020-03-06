package com.kongtrolink.framework.scloud.query;

/**
 * @Auther: liudd
 * @Date: 2020/3/5 09:26
 * @Description:
 */
public class ShieldAlarmQuery extends Paging {

    private String ruleId;
    private String ruleName;

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
}

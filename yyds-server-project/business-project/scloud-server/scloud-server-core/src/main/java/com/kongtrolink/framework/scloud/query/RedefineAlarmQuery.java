package com.kongtrolink.framework.scloud.query;

/**
 * @Auther: liudd
 * @Date: 2020/4/13 15:01
 * @Description:
 */
public class RedefineAlarmQuery extends Paging{

    private String id;
    private String ruleId;
    private String ruleName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

package com.kongtrolink.framework.scloud.query;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/3/5 09:26
 * @Description:
 */
public class ShieldAlarmQuery extends Paging {

    private String ruleId;
    private String ruleName;
    private String type;
    private List<String> siteCodeList;

    public List<String> getSiteCodeList() {
        return siteCodeList;
    }

    public void setSiteCodeList(List<String> siteCodeList) {
        this.siteCodeList = siteCodeList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

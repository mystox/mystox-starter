package com.kongtrolink.framework.query;

/**
 * @Auther: liudd
 * @Date: 2019/10/11 15:45
 * @Description:
 */
public class InformRuleUserQuery {

    private String _id;
    private String ruleId;
    private String ruleName;
    private String userId;
    private String username;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

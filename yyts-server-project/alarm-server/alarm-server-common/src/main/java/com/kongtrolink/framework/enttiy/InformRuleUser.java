package com.kongtrolink.framework.enttiy;

import com.kongtrolink.framework.base.FacadeView;

import java.util.Date;

/**
 * @Auther: liudd
 * @Date: 2019/10/11 15:43
 * @Description: 用户，通知规则对应表。由于本地没有用户信息，为了方便授权通知规则，使用中间表
 */
public class InformRuleUser {

    private String _id;
    private FacadeView informRule;
    private FacadeView user;
    private Date updateTime;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public FacadeView getInformRule() {
        return informRule;
    }

    public void setInformRule(FacadeView informRule) {
        this.informRule = informRule;
    }

    public FacadeView getUser() {
        return user;
    }

    public void setUser(FacadeView user) {
        this.user = user;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}

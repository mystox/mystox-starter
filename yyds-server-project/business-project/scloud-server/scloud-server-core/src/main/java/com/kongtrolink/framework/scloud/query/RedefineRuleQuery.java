package com.kongtrolink.framework.scloud.query;

import java.util.Date;

/**
 * @Auther: liudd
 * @Date: 2020/4/13 14:03
 * @Description:
 */
public class RedefineRuleQuery extends Paging {

    private String id;
    private String name;
    private Boolean enabled;
    private Date updateTime;

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

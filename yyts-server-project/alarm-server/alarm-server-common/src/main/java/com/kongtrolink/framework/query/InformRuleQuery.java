package com.kongtrolink.framework.query;

import com.kongtrolink.framework.base.Paging;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/10/11 09:46
 * @Description:
 */
public class InformRuleQuery extends Paging {
    private String _id;
    private String enterpirseName;
    private String serviceName;
    private String name;
    private String status;
    private String creatorName;
    private List<String> userIds;
    private List<String> usernames;

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public List<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getEnterpirseName() {
        return enterpirseName;
    }

    public void setEnterpirseName(String enterpirseName) {
        this.enterpirseName = enterpirseName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

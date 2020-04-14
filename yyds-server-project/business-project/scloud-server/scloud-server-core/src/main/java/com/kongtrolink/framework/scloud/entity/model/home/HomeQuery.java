package com.kongtrolink.framework.scloud.entity.model.home;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 首页 查询参数
 * Created by Mg on 2018/5/11.
 */
public class HomeQuery implements Serializable{

    private static final long serialVersionUID = 2364848418917007425L;

    private boolean isCurrentRoot = true;   //是否为全局管理员，默认为true（跟用户管辖站点权限相关）
    private String serverCode;
    private String tierCode; // 区域code
    private Date startTime;//开始时间
    private Date endTime; //结束时间

    public String getTierCode() {
        return tierCode;
    }

    public void setTierCode(String tierCode) {
        this.tierCode = tierCode;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    public boolean isCurrentRoot() {
        return isCurrentRoot;
    }

    public void setCurrentRoot(boolean currentRoot) {
        isCurrentRoot = currentRoot;
    }
}

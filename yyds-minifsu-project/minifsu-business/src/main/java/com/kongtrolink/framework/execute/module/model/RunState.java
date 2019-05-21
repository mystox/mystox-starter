package com.kongtrolink.framework.execute.module.model;

import java.util.Date;

/**
 * Created by mystoxlol on 2019/4/22, 10:24.
 * company: kongtrolink
 * description:
 * update record:
 */
public class RunState {
    private String id;
    private String sn;
    private String cpuUse;
    private String memUse;
    private Long sysTime; //终端系统时间
    private Integer csq; //信号强度
    private Date createTime;
    private Integer flashStatus;
    private Integer eleCom;

    public Integer getFlashStatus() {
        return flashStatus;
    }

    public void setFlashStatus(Integer flashStatus) {
        this.flashStatus = flashStatus;
    }

    public Integer getEleCom() {
        return eleCom;
    }

    public void setEleCom(Integer eleCom) {
        this.eleCom = eleCom;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCpuUse() {
        return cpuUse;
    }

    public void setCpuUse(String cpuUse) {
        this.cpuUse = cpuUse;
    }

    public String getMemUse() {
        return memUse;
    }

    public void setMemUse(String memUse) {
        this.memUse = memUse;
    }

    public Long getSysTime() {
        return sysTime;
    }

    public void setSysTime(Long sysTime) {
        this.sysTime = sysTime;
    }

    public Integer getCsq() {
        return csq;
    }

    public void setCsq(Integer csq) {
        this.csq = csq;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

}

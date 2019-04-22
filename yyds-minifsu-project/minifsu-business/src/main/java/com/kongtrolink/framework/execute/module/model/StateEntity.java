package com.kongtrolink.framework.execute.module.model;

/**
 * Created by mystoxlol on 2019/4/22, 10:39.
 * company: kongtrolink
 * description:
 * update record:
 */
public class StateEntity {
    private String cpuUse;
    private String memUse;
    private Long sysTime; //终端系统时间
    private Integer csq; //信号强度

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
}

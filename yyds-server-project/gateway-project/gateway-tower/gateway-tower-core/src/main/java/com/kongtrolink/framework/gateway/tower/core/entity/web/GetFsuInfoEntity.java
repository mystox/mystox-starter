package com.kongtrolink.framework.gateway.tower.core.entity.web;


import com.kongtrolink.framework.gateway.tower.core.entity.msg.GetFsuInfo;

import java.util.Date;

/**
 * xx
 * by Mag on 2019/4/17.
 */
public class GetFsuInfoEntity {

    private String id;
    private String fsuId;
    private String fsuCode;
    private long  ctime;

    public GetFsuInfoEntity() {
    }

    public GetFsuInfoEntity(GetFsuInfo info) {
        this.fsuId = info.getFsuId();
        this.fsuCode = info.getFsuCode();
        this.ctime = new Date().getTime();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFsuId() {
        return fsuId;
    }

    public void setFsuId(String fsuId) {
        this.fsuId = fsuId;
    }

    public String getFsuCode() {
        return fsuCode;
    }

    public void setFsuCode(String fsuCode) {
        this.fsuCode = fsuCode;
    }

    public long getCtime() {
        return ctime;
    }

    public void setCtime(long ctime) {
        this.ctime = ctime;
    }
}

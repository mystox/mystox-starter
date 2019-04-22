package com.kongtrolink.framework.execute.module.model;

import java.util.Map;

/**
 * Created by mystoxlol on 2019/4/22, 10:24.
 * company: kongtrolink
 * description:
 * update record:
 */
public class RunState {
    private String id;
    private String sn;
    private Map<String, String> status;

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

    public Map<String, String> getStatus() {
        return status;
    }

    public void setStatus(Map<String, String> status) {
        this.status = status;
    }
}

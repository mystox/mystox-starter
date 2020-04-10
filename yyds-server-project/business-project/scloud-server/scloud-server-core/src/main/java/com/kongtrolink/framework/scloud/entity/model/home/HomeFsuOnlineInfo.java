package com.kongtrolink.framework.scloud.entity.model.home;


import java.io.Serializable;

/**
 * 首页 站点fsu在线情况
 * Created by Mg on 2018/5/11.
 */
public class HomeFsuOnlineInfo implements Serializable {

    private static final long serialVersionUID = -2078676889300386894L;

    private String siteCode;

    private int id; //站点code

    private String code; //交维态数量

    private String state ;	//注册状态（FSU类型、摄像机类型）：在线、离线

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }
}

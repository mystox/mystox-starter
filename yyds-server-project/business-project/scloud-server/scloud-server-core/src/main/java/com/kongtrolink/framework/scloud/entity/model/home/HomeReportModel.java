package com.kongtrolink.framework.scloud.entity.model.home;

import java.io.Serializable;

/**
 * 汇总的值
 * Created by Mg on 2018/5/11.
 */
public class HomeReportModel implements Serializable {


    private static final long serialVersionUID = -9050888413503436411L;
    private String siteCode;
    private String level; //group值
    private String checkState; //确认状态
    private int  count;//数量

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getCheckState() {
        return checkState;
    }

    public void setCheckState(String checkState) {
        this.checkState = checkState;
    }
}

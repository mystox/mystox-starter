package com.kongtrolink.framework.scloud.query;

import java.util.List;

/**
 * 站点查询类
 * Created by Eric on 2020/2/21.
 */
public class SiteQuery extends Paging{
    /**
     *
     */
    private static final long serialVersionUID = -6757930718134122244L;
    private List<Integer> siteIdList;   //站点id集合
    private List<String> tierCodes;    //区域Code
    private String siteName;    //站点名称
    private String siteCode;    //站点编码
    private String siteType;    //站点类型。A-D类机房
    private String address; //站点地址
    private String towerType;   //铁塔类型
    private String respName;    //资产管理员名称
    private Long startTime; //开始时间
    private Long endTime;   //结束时间
    private List<String> siteCodes; //站点编码集合

    public List<Integer> getSiteIdList() {
        return siteIdList;
    }

    public void setSiteIdList(List<Integer> siteIdList) {
        this.siteIdList = siteIdList;
    }

    public List<String> getTierCodes() {
        return tierCodes;
    }

    public void setTierCodes(List<String> tierCodes) {
        this.tierCodes = tierCodes;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteType() {
        return siteType;
    }

    public void setSiteType(String siteType) {
        this.siteType = siteType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTowerType() {
        return towerType;
    }

    public void setTowerType(String towerType) {
        this.towerType = towerType;
    }

    public String getRespName() {
        return respName;
    }

    public void setRespName(String respName) {
        this.respName = respName;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public List<String> getSiteCodes() {
        return siteCodes;
    }

    public void setSiteCodes(List<String> siteCodes) {
        this.siteCodes = siteCodes;
    }
}

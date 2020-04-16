package com.kongtrolink.framework.scloud.entity;

import java.io.Serializable;

/**
 * 工程管理测试单 数据实体类
 * Created by Eric on 2020/4/13.
 */
public class ProjectOrderEntity implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 8908778391904991970L;
    private String _id;
    private String siteCode;    //站点编码
    private String fsuCode; //FSU设备编码
    private String desc;    //入网说明
    private String applicantName;   //入网信息联系人
    private String applicantPhone;  //入网信息联系人电话
    private Long applyTime; //申请时间
    private String state;   //测试单状态("待提交"、"待审核"、"已通过"、"已作废")

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getFsuCode() {
        return fsuCode;
    }

    public void setFsuCode(String fsuCode) {
        this.fsuCode = fsuCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantPhone() {
        return applicantPhone;
    }

    public void setApplicantPhone(String applicantPhone) {
        this.applicantPhone = applicantPhone;
    }

    public Long getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Long applyTime) {
        this.applyTime = applyTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}

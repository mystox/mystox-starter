package com.kongtrolink.framework.scloud.query;

import java.util.List;

/**
 * 工程管理测试单 查询类
 * Created by Eric on 2020/4/13.
 */
public class ProjectOrderQuery extends Paging {
    /**
     *
     */
    private static final long serialVersionUID = -4109961828140714709L;

    private String orderId; //测试单Id
    private List<String> siteCodes; //站点编码
    private List<String> fsuCodes;  //FSU设备编码
    private String state;   //测试单状态("待提交"、"待审核"、"已通过"、"已作废")
    private String manufacturer;    //厂家
    private String fsuState;  //FSU注册状态
    private Long startTime; //开始时间
    private Long endTime;   //结束时间

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<String> getSiteCodes() {
        return siteCodes;
    }

    public void setSiteCodes(List<String> siteCodes) {
        this.siteCodes = siteCodes;
    }

    public List<String> getFsuCodes() {
        return fsuCodes;
    }

    public void setFsuCodes(List<String> fsuCodes) {
        this.fsuCodes = fsuCodes;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFsuState() {
        return fsuState;
    }

    public void setFsuState(String fsuState) {
        this.fsuState = fsuState;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
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
}

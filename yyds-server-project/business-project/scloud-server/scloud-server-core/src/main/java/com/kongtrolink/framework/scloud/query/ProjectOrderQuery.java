package com.kongtrolink.framework.scloud.query;

import java.util.List;
import java.util.Map;

/**
 * 工程管理测试单 查询类
 * Created by Eric on 2020/4/13.
 */
public class ProjectOrderQuery extends Paging {
    /**
     *
     */
    private static final long serialVersionUID = -4109961828140714709L;

    private String serverCode;
    private String orderId; //测试单Id
    private List<String> siteCodes; //站点编码
    private List<String> deviceCodes;  //设备编码
    private String state;   //测试单状态("待提交"、"待审核"、"已通过"、"已作废")
    private String manufacturer;    //厂家
    private String fsuState;  //FSU注册状态
    private Long startTime; //开始时间
    private Long endTime;   //结束时间

    private String fsuCode;
    private Map<String, String> deviceMap;  //(工程管理-生成测试项时使用)FSU下关联的设备，key:deviceCode, value:deviceName

    private String suggestion;  //审批意见
    private String remark;  //备注
    private String gatewayServerCode;   //获取测试项实时数据必传字段

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

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

    public List<String> getDeviceCodes() {
        return deviceCodes;
    }

    public void setDeviceCodes(List<String> deviceCodes) {
        this.deviceCodes = deviceCodes;
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

    public String getFsuCode() {
        return fsuCode;
    }

    public void setFsuCode(String fsuCode) {
        this.fsuCode = fsuCode;
    }

    public Map<String, String> getDeviceMap() {
        return deviceMap;
    }

    public void setDeviceMap(Map<String, String> deviceMap) {
        this.deviceMap = deviceMap;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getGatewayServerCode() {
        return gatewayServerCode;
    }

    public void setGatewayServerCode(String gatewayServerCode) {
        this.gatewayServerCode = gatewayServerCode;
    }
}

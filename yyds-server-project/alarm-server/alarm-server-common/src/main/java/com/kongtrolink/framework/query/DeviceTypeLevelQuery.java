package com.kongtrolink.framework.query;

import com.kongtrolink.framework.base.Paging;

/**
 * @Auther: liudd
 * @Date: 2019/9/20 16:40
 * @Description:
 */
public class DeviceTypeLevelQuery extends Paging {

    private String id;
    private String enterpriseCode;
    private String serverCode;
    private String deviceType;      //设备类型
    private String deviceModel;     //设备型号
    private Integer level;
    private String operatorName;        //操作用户姓名

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public DeviceTypeLevelQuery() {
    }

    public DeviceTypeLevelQuery(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}

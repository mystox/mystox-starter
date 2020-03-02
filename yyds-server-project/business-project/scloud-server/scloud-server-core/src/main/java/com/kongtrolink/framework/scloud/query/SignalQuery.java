 /**
  * *****************************************************
  * Copyright (C) Kongtrolink techology Co.ltd - All Rights Reserved
  *
  * This file is part of Kongtrolink techology Co.Ltd property.
  *
  * Unauthorized copying of this file, via any medium is strictly prohibited
  * Proprietary and confidential
  ******************************************************
  */
package com.kongtrolink.framework.scloud.query;


/**
 * 数据监控- 实时数据-查询参数
 * @author Mag
 */
public class SignalQuery extends Paging {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 515408966365570432L;
	private int deviceId;
	private String deviceType;//设备类型 必传!
    private String type;
    private String name;
    private String uniqueCode;
    private String cntbId;
    private Boolean displayMark;
    private String deviceCode;
    private String fsuCode;
    private String value;//下发的值或者阈值
    private String gatewayServerCode;//必传

    public String getCntbId() {
        return cntbId;
    }

    public void setCntbId(String cntbId) {
        this.cntbId = cntbId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getDisplayMark() {
        return displayMark;
    }

    public void setDisplayMark(Boolean displayMark) {
        this.displayMark = displayMark;
    }

    public SignalQuery() {
    }
    
    public SignalQuery(int deviceId, String type, String name) {
        this.deviceId = deviceId;
        this.type = type;
        this.name = name;
    }
    
    public int getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getFsuCode() {
        return fsuCode;
    }

    public void setFsuCode(String fsuCode) {
        this.fsuCode = fsuCode;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getGatewayServerCode() {
        return gatewayServerCode;
    }

    public void setGatewayServerCode(String gatewayServerCode) {
        this.gatewayServerCode = gatewayServerCode;
    }
}

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

import java.util.List;

/**
 * 设备查询类
 * @author Mag
 */
public class DeviceQuery extends Paging {

	/**
	 * 
	 */
	private static final long serialVersionUID = 201367096037913874L;
	private String serverCode;
	private List<String> tierCodes;	//区域Code（选择了整个区域的情况）
	private String siteName;	//站点名称
	private String siteCode;	//站点编码
	private List<String> siteCodes;	//站点编码（集合）
	private List<Integer> siteIds;	//站点Id（集合）
	private String deviceCode;	//设备编码
	private String deviceType;	//设备类型
	private String deviceTypeCode;	//设备类型编码
	private String deviceName;	//设备名称
	private String signalType;	//信号类型（AI,AO,DI,DO）
	private String model;	//设备型号
	private String manufacturer;	//设备厂家
	private Long startTime;	//开始时间
	private Long endTime;	//结束时间
	private String state;	//注册状态（FSU类型、摄像机类型）：在线、离线
	private String operationState;	//运行状态（FSU类型）：工程态、测试态、交维态
	private List<String> deviceCodes;	//设备编码（集合）
	private String signalCode;

	public String getServerCode() {
		return serverCode;
	}

	public void setServerCode(String serverCode) {
		this.serverCode = serverCode;
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

	public List<Integer> getSiteIds() {
		return siteIds;
	}

	public void setSiteIds(List<Integer> siteIds) {
		this.siteIds = siteIds;
	}

	public List<String> getSiteCodes() {
		return siteCodes;
	}

	public void setSiteCodes(List<String> siteCodes) {
		this.siteCodes = siteCodes;
	}

	public String getDeviceCode() {
		return deviceCode;
	}

	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceTypeCode() {
		return deviceTypeCode;
	}

	public void setDeviceTypeCode(String deviceTypeCode) {
		this.deviceTypeCode = deviceTypeCode;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getSignalType() {
		return signalType;
	}

	public void setSignalType(String signalType) {
		this.signalType = signalType;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
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

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getOperationState() {
		return operationState;
	}

	public void setOperationState(String operationState) {
		this.operationState = operationState;
	}

	public List<String> getDeviceCodes() {
		return deviceCodes;
	}

	public void setDeviceCodes(List<String> deviceCodes) {
		this.deviceCodes = deviceCodes;
	}

	public String getSignalCode() {
		return signalCode;
	}

	public void setSignalCode(String signalCode) {
		this.signalCode = signalCode;
	}
}

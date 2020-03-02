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
	private List<String> tierCodes;	//区域Code
	private String siteName;	//站点名称
	private List<Integer> siteIds;	//站点Id
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

	public List<Integer> getSiteIds() {
		return siteIds;
	}

	public void setSiteIds(List<Integer> siteIds) {
		this.siteIds = siteIds;
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
}

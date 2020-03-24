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
package com.kongtrolink.framework.scloud.entity.model;


import com.kongtrolink.framework.scloud.entity.DeviceEntity;

/**
 * 设备 前端显示模型
 * @author Mag
 */
public class DeviceModel extends DeviceEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3878090092310831714L;

	private String siteName;	//站点名称
	//todo 如果设备信息中填充了站点地址，关注告警list方法可以避免去资管获取站点信息
	private String siteAddress;	//站点地址
	private String name;	//设备名称
	private String model;	//设备型号
	private Integer fsuId; //fsuId
	private String fsuCode; //fsuCode
	private Integer countSignal;

	public String getSiteAddress() {
		return siteAddress;
	}

	public void setSiteAddress(String siteAddress) {
		this.siteAddress = siteAddress;
	}

	private String isModified; //修改设备时，是否修改了设备名称和设备型号,"0"-未修改，"1"-修改

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public Integer getCountSignal() {
		return countSignal;
	}

	public void setCountSignal(Integer countSignal) {
		this.countSignal = countSignal;
	}

	public String getFsuCode() {
		return fsuCode;
	}

	public void setFsuCode(String fsuCode) {
		this.fsuCode = fsuCode;
	}

	public Integer getFsuId() {
		return fsuId;
	}

	public void setFsuId(Integer fsuId) {
		this.fsuId = fsuId;
	}

	public String getIsModified() {
		return isModified;
	}

	public void setIsModified(String isModified) {
		this.isModified = isModified;
	}
}

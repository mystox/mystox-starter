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
package com.kongtrolink.framework.scloud.entity;

import java.io.Serializable;

/**
 *
 * @author Mosaico
 */
public class DeviceEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6887354115876021753L;
	private String _id;
	private String siteId;
	private String fsuId;
	private String name;
	private String code;
	private String shortCode;//14位编码 针对code裁剪
	private String type;
	private String typeCode;
	private String systemName;
	private String coordinate;
	private String manufactory;
	private String model;	//设备型号
	private String defaultName;	//设备默认名称，在添加设备时生成，根据设备类型名称+列表序列号。用户不可见，且不可更改
	private int defaultSignal;

	public String getDefaultName() {
		return defaultName;
	}

	public void setDefaultName(String defaultName) {
		this.defaultName = defaultName;
	}

	private boolean special = false;

	public String getId() {
		return _id;
	}

	public void setId(String _id) {
		this._id = _id;
	}

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public String getFsuId() {
		return fsuId;
	}

	public void setFsuId(String fsuId) {
		this.fsuId = fsuId;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}

	public String getManufactory() {
		return manufactory;
	}

	public void setManufactory(String manufactory) {
		this.manufactory = manufactory;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public boolean isSpecial() {
		return special;
	}

	public void setSpecial(boolean special) {
		this.special = special;
	}

	@Override
	public String toString() {
		return "Device{" + "_id=" + _id + ", siteId=" + siteId + ", fsuId=" + fsuId + ", name=" + name + ", code="
				+ code + ", type=" + type + ", typeCode=" + typeCode + ", systemName=" + systemName + ", coordinate="
				+ coordinate + ", manufactory=" + manufactory + ", model=" + model + ", special=" + special + '}';
	}

	public String getShortCode() {
		return shortCode;
	}

	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}

	public int getDefaultSignal() {
		return defaultSignal;
	}

	public void setDefaultSignal(int defaultSignal) {
		this.defaultSignal = defaultSignal;
	}
}

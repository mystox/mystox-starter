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


import com.kongtrolink.framework.scloud.base.GeneratedValue;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Mosaico
 */
public class Site implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3430851953570225429L;
	@Field(value = "id")
	@GeneratedValue
	private int id; //主键

	private String name;
	private String code;
	private TierInfo[] tier; // 层级信息
	private String tierCode;
	private String address;
	private String respName;
	private String respPhone;
	private String coordinate;
	private int stationType = 4; // C接口，默认为D类机房（1:A、2:B、3:C、4:D）
	//梅泰诺 需求字段
	private String fsuState; //fsu状态
	private String company; //归属公司
	private String towerHeight;//铁塔高度
	private String towerType;//铁塔类型(插接式单管塔、外法兰单管塔、一体化、仿生树、三管塔、角钢塔、楼顶装饰塔、增高架桅杆、拉线塔、灯杆塔)
	private String shareInfo;//共享信息(移动、联通、电信、移动电信、联通电信、移动联通、移动联通电信)
	private String approvalStatus;//审批状态:(在建 竣工 存量)
	private List<String> platform;//平台信息
	private Integer alarmNum;// 告警数量
	private Boolean online;//在线状态

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getTierCode() {
		return tierCode;
	}

	public void setTierCode(String tierCode) {
		this.tierCode = tierCode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}

	public TierInfo[] getTier() {
		return tier;
	}

	public void setTier(TierInfo[] tier) {
		this.tier = tier;
	}

	public String getRespName() {
		return respName;
	}

	public void setRespName(String respName) {
		this.respName = respName;
	}

	public String getRespPhone() {
		return respPhone;
	}

	public void setRespPhone(String respPhone) {
		this.respPhone = respPhone;
	}

	public int getStationType() {
		return stationType;
	}

	public void setStationType(int stationType) {
		this.stationType = stationType;
	}

	public String getTierString() {
		if (tier == null) {
			return null;
		}

		String strTier = "";
		for (int i = 0; i < tier.length; i++) {
			strTier += tier[i].getName();
			if (i == tier.length - 1) {
				break;
			}
			strTier += "-";
		}
		return strTier;
	}



	public List<String> getPlatform() {
		return platform;
	}

	public void setPlatform(List<String> platform) {
		this.platform = platform;
	}

	public String getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(String approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public String getShareInfo() {
		return shareInfo;
	}

	public void setShareInfo(String shareInfo) {
		this.shareInfo = shareInfo;
	}

	public String getTowerType() {
		return towerType;
	}

	public void setTowerType(String towerType) {
		this.towerType = towerType;
	}

	public String getTowerHeight() {
		return towerHeight;
	}

	public void setTowerHeight(String towerHeight) {
		this.towerHeight = towerHeight;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getFsuState() {
		return fsuState;
	}

	public void setFsuState(String fsuState) {
		this.fsuState = fsuState;
	}

	public Integer getAlarmNum() {
		return alarmNum;
	}

	public void setAlarmNum(Integer alarmNum) {
		this.alarmNum = alarmNum;
	}

	public Boolean getOnline() {
		return online;
	}

	public void setOnline(Boolean online) {
		this.online = online;
	}
}

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
import com.kongtrolink.framework.scloud.constant.CommonConstant;
import com.kongtrolink.framework.scloud.constant.FsuOperationState;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * 设备 数据实体类
 * @author Eric
 */
public class DeviceEntity implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 346633002678348402L;
	@Field(value = "id")
	@GeneratedValue
	private int id; //主键ID

	private String tierCode;	//区域编码
	private String siteCode;	//站点编码
	private int siteId;	//站点ID
	private String type;	//设备类型名称
	private String typeCode;	//设备类型编码
	private String code;	//设备编码
	private Long createTime;	//投入使用时间
	private String shelfLife;	//保质期
	private String isInsurance;	//是否出保（是/否）
	private String manufacturer;	//生产厂家
	private String brand;	//品牌
	private String deviceDesc;	//备注
	//网关上报
	private String ip;
	private String enterpriseCode;
	private String serverCode;
	private String gatewayServerCode;
	//下面为FSU动环主机设备特有属性
	private String state = CommonConstant.OFFLINE;	//注册状态（FSU类型、摄像机类型）：在线、离线
	private String operationState = FsuOperationState.PROJECT;	//运行状态（FSU类型）：工程态、测试态、交维态

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTierCode() {
		return tierCode;
	}

	public void setTierCode(String tierCode) {
		this.tierCode = tierCode;
	}

	public String getSiteCode() {
		return siteCode;
	}

	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public String getShelfLife() {
		return shelfLife;
	}

	public void setShelfLife(String shelfLife) {
		this.shelfLife = shelfLife;
	}

	public String getIsInsurance() {
		return isInsurance;
	}

	public void setIsInsurance(String isInsurance) {
		this.isInsurance = isInsurance;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getDeviceDesc() {
		return deviceDesc;
	}

	public void setDeviceDesc(String deviceDesc) {
		this.deviceDesc = deviceDesc;
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

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
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

	public String getGatewayServerCode() {
		return gatewayServerCode;
	}

	public void setGatewayServerCode(String gatewayServerCode) {
		this.gatewayServerCode = gatewayServerCode;
	}
}

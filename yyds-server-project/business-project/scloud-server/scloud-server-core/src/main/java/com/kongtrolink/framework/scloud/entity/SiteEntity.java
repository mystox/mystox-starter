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
 * 站点 数据实体类
 * @author Mosaico
 */
public class SiteEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3430851953570225429L;
	@Field(value = "id")
	@GeneratedValue
	private int id; //主键

	private String tierCode;	//区域编码
	private String tierName;	//区域名称(中间用"-"隔开)
	private String code;	//站点编码
	private String siteType;	//站点类型
	private String coordinate;	//站点经纬度
	private String address;	//站点地址
	private String respName;	//资产管理员名称
	private String respPhone;	//联系电话
	private String towerHeight;//铁塔高度
	private String towerType;//铁塔类型(插接式单管塔、外法兰单管塔、一体化、仿生树、三管塔、角钢塔、楼顶装饰塔、增高架桅杆、拉线塔、灯杆塔、爬支架式单管塔、四管塔、灯杆景观塔、外爬支架式地面景观塔、插接式地面景观塔、法兰式地面景观塔、双轮景观塔、路灯灯塔、落地增高架、楼面拉线塔、落地拉线塔、楼面增高架、楼面支撑杆、楼面角钢塔、楼面景观塔、楼面美化天线、楼面抱杆)
	private String shareInfo;//共享信息(暂无,移动,电信,联通,铁塔,移动电信,电信联通,移动联通,移动铁塔,联通铁塔,电信铁塔,移动电信铁塔,移动电信联通,电信联通铁塔,移动联通铁塔,移动电信联通铁塔)
	private String assetNature;	//产权性质（自建、社会资源、注入）
	private Long createTime;	//投入使用时间
	private String areaCovered;	//占地面积
	private Integer fileId;	//站点图纸文件Id

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSiteType() {
		return siteType;
	}

	public void setSiteType(String siteType) {
		this.siteType = siteType;
	}

	public String getTierCode() {
		return tierCode;
	}

	public void setTierCode(String tierCode) {
		this.tierCode = tierCode;
	}

	public String getTierName() {
		return tierName;
	}

	public void setTierName(String tierName) {
		this.tierName = tierName;
	}

	public String getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public String getTowerHeight() {
		return towerHeight;
	}

	public void setTowerHeight(String towerHeight) {
		this.towerHeight = towerHeight;
	}

	public String getTowerType() {
		return towerType;
	}

	public void setTowerType(String towerType) {
		this.towerType = towerType;
	}

	public String getShareInfo() {
		return shareInfo;
	}

	public void setShareInfo(String shareInfo) {
		this.shareInfo = shareInfo;
	}

	public String getAssetNature() {
		return assetNature;
	}

	public void setAssetNature(String assetNature) {
		this.assetNature = assetNature;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public String getAreaCovered() {
		return areaCovered;
	}

	public void setAreaCovered(String areaCovered) {
		this.areaCovered = areaCovered;
	}

	public Integer getFileId() {
		return fileId;
	}

	public void setFileId(Integer fileId) {
		this.fileId = fileId;
	}
}

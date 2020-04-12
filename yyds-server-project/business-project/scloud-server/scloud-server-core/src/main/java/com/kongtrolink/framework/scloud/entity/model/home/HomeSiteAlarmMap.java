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
package com.kongtrolink.framework.scloud.entity.model.home;


import com.kongtrolink.framework.scloud.entity.SiteEntity;

import java.util.List;
import java.util.Map;

/**
 * 站点地图 数据实体类
 * @author Mosaico
 */
public class HomeSiteAlarmMap extends SiteEntity {
	private static final long serialVersionUID = -5878975818445493876L;

	private int alarmNumber; //总的告警数量
	private Map<String,Integer> levelMap; //对应告警级别的数量
	private List<HomeFsuOnlineInfo> fsuOnlineInfo;//FSU在线情况

	public int getAlarmNumber() {
		return alarmNumber;
	}

	public void setAlarmNumber(int alarmNumber) {
		this.alarmNumber = alarmNumber;
	}

	public Map<String, Integer> getLevelMap() {
		return levelMap;
	}

	public void setLevelMap(Map<String, Integer> levelMap) {
		this.levelMap = levelMap;
	}

	public List<HomeFsuOnlineInfo> getFsuOnlineInfo() {
		return fsuOnlineInfo;
	}

	public void setFsuOnlineInfo(List<HomeFsuOnlineInfo> fsuOnlineInfo) {
		this.fsuOnlineInfo = fsuOnlineInfo;
	}
}

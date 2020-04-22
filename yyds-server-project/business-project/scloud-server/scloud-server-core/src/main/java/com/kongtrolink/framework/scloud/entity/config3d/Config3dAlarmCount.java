/** *****************************************************
 * Copyright (C) Kongtrolink techology Co.ltd - All Rights Reserved
 *
 * This file is part of Kongtrolink techology Co.Ltd property.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 ****************************************************** */
package com.kongtrolink.framework.scloud.entity.config3d;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * copy form scloud
 * @author Mag
 */
public class Config3dAlarmCount implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8758846240777481599L;

	class AlarmCount {
		String alarmLevel;
		int alarmNum;
	}

	private String deviceId;
	private List<AlarmCount> alarmCount;

	public Config3dAlarmCount() {
		this.alarmCount = new ArrayList<>();
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public void addAlarmCount(String level, int count) {
		AlarmCount newCount = new AlarmCount();
		newCount.alarmLevel = level;
		newCount.alarmNum = count;
		this.alarmCount.add(newCount);
	}

	public List<Integer> getAlarmCount() {
		List<Integer> counts = new ArrayList<>();
		for (AlarmCount count : alarmCount) {
			counts.add(count.alarmNum);
		}
		return counts;
	}

}

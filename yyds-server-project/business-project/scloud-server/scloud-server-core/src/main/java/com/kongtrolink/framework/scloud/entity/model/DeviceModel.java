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

import java.io.Serializable;

/**
 *
 * @author Mag
 */
public class DeviceModel extends DeviceEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3878090092310831714L;

	private int countSignal;


	public int getCountSignal() {
		return countSignal;
	}

	public void setCountSignal(int countSignal) {
		this.countSignal = countSignal;
	}
}

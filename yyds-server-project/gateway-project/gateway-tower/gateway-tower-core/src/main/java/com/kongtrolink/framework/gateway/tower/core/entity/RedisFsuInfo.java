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
package com.kongtrolink.framework.gateway.tower.core.entity;

import java.io.Serializable;

/**
 * 保持在redis中精简字段的数据
 */
public class RedisFsuInfo implements Serializable {

	private static final long serialVersionUID = -6361982081507590764L;

	private String id;
	private String shortCode;
	private String ip;
	private int port;
	private String gatewayServerCode;
	private String mqttId;//对面的mqtt信息

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getShortCode() {
		return shortCode;
	}

	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMqttId() {
		return mqttId;
	}

	public void setMqttId(String mqttId) {
		this.mqttId = mqttId;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getGatewayServerCode() {
		return gatewayServerCode;
	}

	public void setGatewayServerCode(String gatewayServerCode) {
		this.gatewayServerCode = gatewayServerCode;
	}
}

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
import java.util.List;

/**
 *
 * @author Mag
 */
public class WebPageInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 99349880817197110L;
	public String name;
	public String uri;
	public int priority = -1;
	//子菜单集合，用于前端生成树
	public List<WebPageInfo> nextMenuList ;
	//菜单编码
	private String code ;
	//父菜单编码
	private String superCode;

	public WebPageInfo() {
	}

	public WebPageInfo(String name, String uri, String code) {
		this.name = name;
		this.uri = uri;
		this.code = code;
	}

	public WebPageInfo(String name, String uri, String code, String superCode) {
		this.name = name;
		this.uri = uri;
		this.code = code;
		this.superCode = superCode;
	}

	public WebPageInfo(String name, String uri, int priority, String code) {
		this.name = name;
		this.uri = uri;
		this.priority = priority;
		this.code = code;
	}

	public WebPageInfo(String name, String uri, int priority, String code, String superCode) {
		this.name = name;
		this.uri = uri;
		this.priority = priority;
		this.code = code;
		this.superCode = superCode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSuperCode() {
		return superCode;
	}

	public void setSuperCode(String superCode) {
		this.superCode = superCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public List<WebPageInfo> getNextMenuList() {
		return nextMenuList;
	}

	public void setNextMenuList(List<WebPageInfo> nextMenuList) {
		this.nextMenuList = nextMenuList;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
}

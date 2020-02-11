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
 * 前端显示冗余
 * @author Mosaico
 */
public class FacadeView implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -5582784481982344034L;
	private String strId;
    private String name;
    private String phone;
    private String operationState;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public FacadeView() {
    }

    public FacadeView(String strId, String name) {
	this.strId = strId;
	this.name = name;
    }

    public String getStrId() {
	return strId;
    }

    public void setStrId(String strId) {
	this.strId = strId;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FacadeView(String strId, String name, String phone) {
        this.strId = strId;
        this.name = name;
        this.phone = phone;
    }

    public String getOperationState() {
        return operationState;
    }

    public void setOperationState(String operationState) {
        this.operationState = operationState;
    }
}

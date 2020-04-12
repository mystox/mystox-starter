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
package com.kongtrolink.framework.scloud.entity.multRoom;

import java.io.Serializable;

/**
 * 信号字典表（设备类型）
 *
 * @author Mosaico
 */
public class RoomDeviceTypeInfo implements Serializable{

    private static final long serialVersionUID = 2487474667652425180L;
    /**
	 * 
	 */
	private String code;
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

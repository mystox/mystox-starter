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
 * 层级信息
 * @author Mosaico
 */
public class TierInfo implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 2378290557390603273L;
	private String code;
    private String name;
    private String latitude;    //纬度
    private String longitude;   //经度
    
    public TierInfo() {
    }

    public TierInfo(String code, String name, String latitude, String longitude) {
        this.code = code;
        this.name = name;
        this.latitude=latitude;
        this.longitude=longitude;
    }

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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "TierInfo{" + "code=" + code + ", name=" + name + ", latitude=" + latitude + ", longitude=" + longitude + '}';
    }
    
}

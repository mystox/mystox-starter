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
import java.util.List;

/**
 * 信号字典表（设备类型）
 *
 * @author Mosaico
 */
public class RoomDeviceType implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 4208333923444037937L;
    private String uniqueCode;
	private String code;
    private String typeName;
    private List<RoomSignalType> signalTypeList;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<RoomSignalType> getSignalTypeList() {
        return signalTypeList;
    }

    public void setSignalTypeList(List<RoomSignalType> signalTypeList) {
        this.signalTypeList = signalTypeList;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    @Override
    public String toString() {
        return "DeviceType{" + "code=" + code + ", typeName=" + typeName + ", signalTypeList=" + signalTypeList + '}' + '\n';
    }

}

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

/**
 * copy form scloud
 * @author Mag
 */
public class Config3dInfo  implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 4110350528632751673L;
	private String type;
    private String deviceCode;
    private String label;
    private String usedFloor;
    private String custom;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUsedFloor() {
        return usedFloor;
    }

    public void setUsedFloor(String usedFloor) {
        this.usedFloor = usedFloor;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    @Override
    public String toString() {
        return "Config3dInfo{" + "type=" + type + ", deviceCode=" + deviceCode + ", label=" + label + ", usedFloor=" + usedFloor + ", custom=" + custom + '}';
    }
        
}

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
public class Config3dDeviceMap implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 7347241770424554236L;
	private String uuid;
    private String data;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
    
}

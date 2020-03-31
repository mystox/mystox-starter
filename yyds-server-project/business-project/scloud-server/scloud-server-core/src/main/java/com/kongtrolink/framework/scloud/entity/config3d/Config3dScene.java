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
public class Config3dScene  implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -6072993106286931298L;
	private int siteId;
    private Config3dObject object;

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public Config3dObject getObject() {
        return object;
    }

    public void setObject(Config3dObject object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "Config3dScene{" + "siteId=" + siteId + ", object=" + object + '}';
    }



}

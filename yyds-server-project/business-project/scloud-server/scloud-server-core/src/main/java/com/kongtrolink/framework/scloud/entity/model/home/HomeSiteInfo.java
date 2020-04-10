package com.kongtrolink.framework.scloud.entity.model.home;

import java.io.Serializable;

/**
 * @author Mag
 **/
public class HomeSiteInfo implements Serializable {
    private static final long serialVersionUID = -5917691537891582543L;

    private String siteCode;

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }
}

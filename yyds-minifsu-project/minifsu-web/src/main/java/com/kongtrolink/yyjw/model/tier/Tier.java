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
package com.kongtrolink.yyjw.model.tier;

import java.io.Serializable;

/**
 * 站点分级完整信息
 * @author Mosaico
 */
public class Tier implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -8071203782084924454L;
	private String tierCode;
    private String[] tierNames;
    private String latitude;//纬度
    private String longitude;//经度

    public Tier() {
    }

    public Tier(String tierCode, String[] tierNames,String longitude,String latitude) {
        this.tierCode = tierCode;
        this.tierNames = tierNames;
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public String getTierCode() {
        return tierCode;
    }

    public void setTierCode(String tierCode) {
        this.tierCode = tierCode;
    }

    public String[] getTierNames() {
        return tierNames;
    }

    public void setTierNames(String[] tierNames) {
        this.tierNames = tierNames;
    }
    
    public String getTierNameString() {
        if (tierNames == null) {
            return null;
        }

        String name = "";
        for (int i = 0; i < tierNames.length; i ++) {
            if (tierNames[i] == null || tierNames[i].equals("")) {
                continue;
            }
            name += tierNames[i];
            if (i == tierNames.length - 1) {
                break;
            }
            name += "-";
        }
        return name;
    }

    public String getTierNameByDepth(int depth){
        if (tierNames == null){
            return null;
        }

        String name = "";
        for (int i = 0; i < depth; i++){
            if (tierNames[i] == null || tierNames[i].equals("")){
                continue;
            }
            name += tierNames[i];
            if (i == depth - 1){
                break;
            }
            if (depth != 1) {
                name += "-";
            }
        }
        return name;
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
    
    public TierInfo[] getTierInfo() {
        while (tierCode.startsWith("00")) {
            tierCode = tierCode.substring(2);
        }
        TierInfo[] tierInfo = new TierInfo[tierNames.length];
        for (int i = 0; i < tierNames.length; i ++) {
            tierInfo[i] = new TierInfo();
            if( (2*i + 2) > tierCode.length()){
                tierCode = tierCode+ "000000";//以防报错
            }
            tierInfo[i].setCode(tierCode.substring(2*i, 2*i + 2));
            tierInfo[i].setName(tierNames[i]);
        }
        return tierInfo;
    }

    @Override
    public String toString() {
        return "Tier{" + "tierCode=" + tierCode + ", tierNames=" + tierNames + ", latitude=" + latitude + ", longitude=" + longitude + '}';
    }
    
}

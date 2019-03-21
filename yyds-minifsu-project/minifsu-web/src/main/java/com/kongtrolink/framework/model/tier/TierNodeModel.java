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
package com.kongtrolink.framework.model.tier;

import java.io.Serializable;

/**
 *
 * @author Mosaico
 */
public class TierNodeModel implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -4666423997142109652L;
	private Boolean end;    // 是否叶节点
    private TierInfo info;
    private Object nextTier;

    public TierNodeModel() {
    }

    public TierNodeModel(TierNode tierNode) {
        this.end = tierNode.isEnd();
        this.info = tierNode.getInfo();
    }

    public Boolean isEnd() {
        return end;
    }

    public void setEnd(Boolean end) {
        this.end = end;
    }

    public TierInfo getInfo() {
        return info;
    }

    public void setInfo(TierInfo info) {
        this.info = info;
    }

    public Object getNextTier() {
        return nextTier;
    }

    public void setNextTier(Object nextTier) {
        this.nextTier = nextTier;
    }

    public String getName() {
        return this.info.getName();
    }
    
    public String getCode() {
        return this.info.getCode();
    }
    
    @Override
    public String toString() {
        return "TierNodeModel{" + "end=" + end + ", info=" + info + ", nextTier=" + nextTier + '}';
    }
    
}

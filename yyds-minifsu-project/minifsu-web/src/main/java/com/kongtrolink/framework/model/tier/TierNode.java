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
import java.util.List;

/**
 * 分级节点
 * @author Mosaico
 */
public class TierNode implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 6467572192023937287L;
	private Boolean end;    // 是否叶节点
    private TierInfo info;
    private List<TierNode> nextTier;

    public TierNode() {
    }

    public TierNode(Boolean end, TierInfo info, List<TierNode> nextTier) {
        this.end = end;
        this.info = info;
        this.nextTier = nextTier;
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
    
    public List<TierNode> getNextTier() {
        return nextTier;
    }

    public void setNextTier(List<TierNode> nextTier) {
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
        return "TierNode{" + "end=" + end + ", info=" + info + ", nextTier=" + nextTier + '}';
    }
    
}

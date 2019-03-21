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
package com.kongtrolink.framework.util;


import com.kongtrolink.framework.model.tier.TierInfo;
import com.kongtrolink.framework.model.tier.TierNode;
import com.kongtrolink.framework.model.tier.TierNodeModel;

import java.util.*;

/**
 *
 * @author Mosaico
 */
public class TierTreeUtil {
    


    //在层级树中，只保留给定的层级
    public static void retainTierByCode(List<TierNode> tierNodeList, List<String> tierCodeList, String lastCode){
        for(int i = 0 ; i < tierNodeList.size() ; i++){
            TierNode tierNode = tierNodeList.get(i);
            String tierCode = lastCode + tierNode.getInfo().getCode();
            if(!tierNode.isEnd()){
                //如果当前节点不是叶子节点
                retainTierByCode(tierNode.getNextTier(), tierCodeList, tierCode);
                if(tierNode.getNextTier() == null || tierNode.getNextTier().size() == 0){
                    tierNodeList.remove(i);
                    i--;
                }
            }else{
                //如果当前节点是叶子节点
                tierCode = TierTreeUtil.formatTierCode(tierCode);
                boolean isRemove = true;
                for(int j = 0 ; j < tierCodeList.size() ; j++){
                    String siteTierCode = tierCodeList.get(j);
                    if(siteTierCode.equals(tierCode)){
                        tierCodeList.remove(j);
                        isRemove = false;
                        break;
                    }
                }
                if(isRemove){
                    tierNodeList.remove(i);
                    i--;
                }
            }
        }
    }

    /**
     * 获取站点分级编码。将数据库中按层级存储的层级序号拼成完整的站点分级编码，
     * 暂定为三级（6位）。
     *
     * @param tierNodes 站点层级。
     * @param code 层级节点编码。
     * @param tierCodes 分级节点编码。
     * @return 站点分级节点编码 List 。
     */
    public static List<String> getTierCodeList(List<TierNode> tierNodes, String code, List<String> tierCodes) {
        if (tierCodes == null) {
            tierCodes = new LinkedList<>();
        }
        for (int i = 0; i < tierNodes.size(); i ++) {
            TierNode tierNode = tierNodes.get(i);
            TierInfo tierInfo = tierNode.getInfo();
            String tierCode = code + tierInfo.getCode();
            if (!tierNode.isEnd()) {
                tierCodes = getTierCodeList(tierNode.getNextTier(), tierCode, tierCodes);
            }
            // tierCode 不满三级时在高位补零
            tierCode = TierTreeUtil.formatTierCode(tierCode);
            if (tierNode.isEnd()) {
                tierCodes.add(tierCode);
            }
        }
        return tierCodes;
    }



    /**
     * 将 tierCode 转化为标准的6位格式
     * @param tierCode
     * @return
     */
    public static String formatTierCode(String tierCode) {
        return String.format("%06d", Integer.parseInt(tierCode));
    }

    private static List<TierNodeModel> parse(List<TierNode> tierNodes) {
        List<TierNodeModel> models = new ArrayList();
        for (TierNode tierNode : tierNodes) {
            TierNodeModel tempModel = new TierNodeModel(tierNode);
            if (!tierNode.isEnd()) {
            tempModel.setNextTier(parse(tierNode.getNextTier()));
            }
            models.add(tempModel);
        }
        return models;
    }

    /**
     * code 与 中文对应关系 xxxxxx
     * 
     * @param tierNodes
     * @return
     */
    public static Map<String,String> getTireName(List<TierNode> tierNodes, String xcode, String xname, Map<String,String> xmap) {
    	if(xmap==null){
    		xmap = new HashMap<String,String>();
    	}
        for (TierNode tierNode : tierNodes) {
        	TierInfo info = tierNode.getInfo();
        	String code = xcode+info.getCode();
        	String name = info.getName();
        	xmap.put(code,name);
        	if (!tierNode.isEnd()) {
        		getTireName(tierNode.getNextTier(),code,name,xmap);
            }
        }
        return xmap;
    }
}

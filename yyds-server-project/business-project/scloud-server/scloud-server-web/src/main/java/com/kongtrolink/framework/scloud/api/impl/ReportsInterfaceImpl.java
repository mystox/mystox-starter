package com.kongtrolink.framework.scloud.api.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.scloud.api.ReportsInterface;
import com.kongtrolink.framework.scloud.entity.model.SiteModel;
import com.kongtrolink.framework.scloud.query.SiteQuery;
import com.kongtrolink.framework.scloud.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * \* @Author: mystox
 * \* Date: 2020/3/2 13:19
 * \* Description:
 * \
 */
@Service
public class ReportsInterfaceImpl implements ReportsInterface{


    @Autowired
    SiteService siteService;

    @Override
    public List<SiteModel> getSiteListByEnterpriseCode(String msg){
        try{
            JSONObject condition = JSONObject.parseObject(msg);
            String enterpriseCode = condition.getString("enterpriseCode");
            List<SiteModel> list = siteService.findSiteList(enterpriseCode, new SiteQuery());
            return list;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
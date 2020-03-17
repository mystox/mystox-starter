package com.kongtrolink.framework.scloud.api.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.scloud.api.ReportsInterface;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.entity.model.SiteModel;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.query.SiteQuery;
import com.kongtrolink.framework.scloud.service.DeviceService;
import com.kongtrolink.framework.scloud.service.SiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    Logger logger = LoggerFactory.getLogger(ReportsInterfaceImpl.class);

    @Autowired
    SiteService siteService;

    @Autowired
    DeviceService deviceService;
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

    @Override
    public List<DeviceModel> getFsuSCloud(String msg) {

        logger.info("根据站点id获取fsu列表");
        JSONObject condition = JSONObject.parseObject(msg);
        String siteId = condition.getString("stationId");
        String enterpriseCode = condition.getString("enterpriseCode");
        DeviceQuery deviceQuery = new DeviceQuery();
        deviceQuery.setDeviceTypeCode("038");
        deviceQuery.setSiteCode(siteId);
        try {
            List<DeviceModel> deviceList = deviceService.findDeviceList(enterpriseCode, deviceQuery);
            return deviceList;
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return null;
    }

}
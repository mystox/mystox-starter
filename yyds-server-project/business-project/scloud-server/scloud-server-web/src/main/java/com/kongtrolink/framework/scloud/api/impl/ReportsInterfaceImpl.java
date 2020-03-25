package com.kongtrolink.framework.scloud.api.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.scloud.api.ReportsInterface;
import com.kongtrolink.framework.scloud.constant.CommonConstant;
import com.kongtrolink.framework.scloud.dao.HistoryDataDao;
import com.kongtrolink.framework.scloud.entity.DeviceType;
import com.kongtrolink.framework.scloud.entity.SignalType;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.entity.model.SiteModel;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.query.HistoryDataQuery;
import com.kongtrolink.framework.scloud.query.SiteQuery;
import com.kongtrolink.framework.scloud.service.DeviceService;
import com.kongtrolink.framework.scloud.service.RealTimeDataService;
import com.kongtrolink.framework.scloud.service.SiteService;
import org.apache.commons.lang3.StringUtils;
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
public class ReportsInterfaceImpl implements ReportsInterface {

    Logger logger = LoggerFactory.getLogger(ReportsInterfaceImpl.class);

    @Autowired
    SiteService siteService;

    @Autowired
    DeviceService deviceService;

    @Autowired
    RealTimeDataService realTimeDataService;
    @Autowired
    HistoryDataDao historyDataDao;

    @Override

    public List<SiteModel> getSiteListByEnterpriseCode(String msg) {
        try {
            JSONObject condition = JSONObject.parseObject(msg);
            String enterpriseCode = condition.getString("enterpriseCode");
            List<SiteModel> list = siteService.findSiteList(enterpriseCode, new SiteQuery());
            return list;
        } catch (Exception e) {
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
        deviceQuery.setDeviceTypeCode("038"); //fsu
        deviceQuery.setSiteCode(siteId);
        try {
            List<DeviceModel> deviceList = deviceService.findDeviceList(enterpriseCode, deviceQuery);
            return deviceList;
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return null;
    }

    @Override
    public JSONObject stationElectricCountList(String msg) {
        logger.info("用电量统计报表获取");
        JSONObject condition = JSONObject.parseObject(msg);
        String siteId = condition.getString("stationId");
        String enterpriseCode = condition.getString("enterpriseCode");
        DeviceQuery deviceQuery = new DeviceQuery();
        deviceQuery.setDeviceTypeCode("029"); //分路计量
        deviceQuery.setSiteCode(siteId);
        try {
            List<DeviceModel> deviceList = deviceService.findDeviceList(enterpriseCode, deviceQuery);

            String deviceCode = "";
            String temp = "";
            if (deviceList != null && deviceList.size() > 0) {
                for (DeviceModel deviceModel : deviceList)
                    if (!CommonConstant.OFFLINE.equals(deviceModel)) {
                        deviceCode = deviceModel.getCode();
                    } else {
                        temp = deviceCode;
                        logger.warn("分路计量设备离线");
                    }
                if (StringUtils.isBlank(deviceCode)) {
                    logger.warn("get deviceCode offline");
                    deviceCode = temp;
                }
                //获取信号点
                DeviceType mobileSignalCodeRegex = realTimeDataService.getDeviceType(enterpriseCode, "029", null);
                List<SignalType> signalTypeList = mobileSignalCodeRegex.getSignalTypeList();
                String mobileSignalCntb = "";
                String unicomSignalCntb = "";
                String telecomSignalCntb = "";
                for (SignalType signalType : signalTypeList) {
                    String type = signalType.getType();
                    if ("102".equals(type)) mobileSignalCntb = signalType.getCntbId();
                    if ("104".equals(type)) unicomSignalCntb = signalType.getCntbId();
                    if ("106".equals(type)) telecomSignalCntb = signalType.getCntbId();
                }

                HistoryDataQuery historyDataQuery = new HistoryDataQuery();
                historyDataQuery.setDeviceCode(deviceCode);
                long startBeginTime = condition.getDate("startBeginTime").getTime();
                historyDataQuery.setStartTime(startBeginTime);
                long startEndTime = condition.getDate("startEndTime").getTime();
                historyDataQuery.setEndTime(startEndTime);
                historyDataQuery.setCntbId(mobileSignalCntb);
                JSONObject result = historyDataDao.statisticElectricHistoryData(enterpriseCode, deviceCode, mobileSignalCntb, unicomSignalCntb, telecomSignalCntb, startBeginTime, startEndTime);
                return result;
            }


        } catch (Exception e) {
            logger.error(e.toString());
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
        }

        return null;
    }


}
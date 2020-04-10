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
import com.kongtrolink.framework.scloud.util.WorkbookUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    @Value("${server.routeMark}")
    private String routeMark;
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

    @Override
    public JSONObject exportAlarmHistory(String msg) {
        logger.info("alarm export AlarmHistory receive...[{}]", msg);

        JSONObject msgJson = JSONObject.parseObject(msg, JSONObject.class);
        List<JSONObject> alarmHistory = new ArrayList<>();

        String[][][] resultData = alarmHistoryDataCreate(alarmHistory);

        String fileName = msgJson.getString("fileName");
        String excelUri = alarmHistoryExcelCreate(fileName, resultData);
        JSONObject result = new JSONObject();
        result.put("uri", excelUri);
        return result;
    }

    private String alarmHistoryExcelCreate(String fileName, String[][][] resultData) {
        long currentTime = System.currentTimeMillis();
        String path = "/reportsResources/report_alarmHistory";
        String filename = fileName + ".xls";
        WorkbookUtil.save("." + path, filename, WorkbookUtil.createWorkBook(new String[]{"告警列表"}, resultData));
        return "/" + routeMark + path + "/" + filename + ".xls";
    }

    private String[][][] alarmHistoryDataCreate(List<JSONObject> alarmHistory) {
        String[] tableHead = null;
        tableHead = new String[]{"告警名称", "告警等级", "告警值", "区域层级", "站点名称", "设备名称", "告警详情", "告警发生时间", "告警恢复时间", "确认时间", "确认人", "清除时间", "清除人"};
        String[] title = new String[tableHead.length];
        int colLength = tableHead.length; // 列
        int rowLength = alarmHistory.size() + 1; //行
        String[][] sheetData = new String[rowLength + 4][colLength];

        for (int i = 0; i < rowLength; i++) {
            String[] row = sheetData[i];
            if (i == 0) {
                sheetData[i] = title;
                continue;
            }
            if (i == 1) {
                sheetData[i] = tableHead;
                continue;
            }
            JSONObject jsonObject = alarmHistory.get(i - 1);
            row[0] = jsonObject.getString("alarmName");
            row[1] = jsonObject.getString("alarmLevel");
            row[2] = jsonObject.getString("value");
            row[3] = jsonObject.getString("alarmName");
            row[4] = jsonObject.getString("alarmName");
            row[5] = jsonObject.getString("alarmName");
            row[6] = jsonObject.getString("alarmName");
            row[7] = jsonObject.getString("alarmName");
            row[8] = jsonObject.getString("alarmName");
            row[9] = jsonObject.getString("alarmName");
            row[10] = jsonObject.getString("alarmName");
            row[11] = jsonObject.getString("alarmName");
            row[12] = jsonObject.getString("alarmName");

        }
        return new String[][][]{sheetData};


    }


}
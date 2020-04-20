package com.kongtrolink.framework.scloud.api.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.common.util.DateUtil;
import com.kongtrolink.framework.scloud.api.ReportsInterface;
import com.kongtrolink.framework.scloud.constant.CommonConstant;
import com.kongtrolink.framework.scloud.dao.HistoryDataDao;
import com.kongtrolink.framework.scloud.entity.AlarmBusiness;
import com.kongtrolink.framework.scloud.entity.DeviceType;
import com.kongtrolink.framework.scloud.entity.FacadeView;
import com.kongtrolink.framework.scloud.entity.SignalType;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.entity.model.SiteModel;
import com.kongtrolink.framework.scloud.query.*;
import com.kongtrolink.framework.scloud.service.*;
import com.kongtrolink.framework.scloud.util.WorkbookUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
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


    @Autowired
    AlarmService alarmService;

    @Autowired
    AlarmBusinessService businessService;

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
        String serverCode = condition.getString("serverCode");
        DeviceQuery deviceQuery = new DeviceQuery();
        deviceQuery.setDeviceTypeCode("038"); //fsu
        List<String> sites = new ArrayList<>();
        sites.add(siteId);
        deviceQuery.setSiteCodes(sites);
        deviceQuery.setServerCode(serverCode);
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
        msgJson.getString("startBeginTime");
        msgJson.getString("startEndTime");
        msgJson.getString("serverCode");
        msgJson.getString("enterpriseCode");
        AlarmBusinessQuery alarmQuery = msgJson.toJavaObject(AlarmBusinessQuery.class);
        List<AlarmBusiness> alarmHistory = businessService.findByTimePeriod(alarmQuery);
        String[][][] resultData = alarmHistoryDataCreate(alarmHistory);
        String fileName = msgJson.getString("fileName");
        String excelUri = alarmHistoryExcelCreate(fileName, resultData);
        JSONObject result = new JSONObject();
        result.put("uri", excelUri);
        return result;
    }

    @Override
    public List<SiteModel> getCurrentStationList(String msg) {
        JSONObject jsonObject = JSONObject.parseObject(msg, JSONObject.class);
        String uniqueCode = jsonObject.getString("uniqueCode");
        String userId = jsonObject.getString("userId");
        String serverCode = jsonObject.getString("serverCode");
        SiteQuery siteQuery = new SiteQuery();
        siteQuery.setUserId(userId);
        siteQuery.setServerCode(serverCode);
        List<SiteModel> siteModelList = siteService.findSiteList(uniqueCode, siteQuery);
        return siteModelList;
    }

    private String alarmHistoryExcelCreate(String fileName, String[][][] resultData) {
        String path = "/reportsResources/report_alarmHistory";
        String filename = fileName + ".xls";
        WorkbookUtil.save("." + path, filename, WorkbookUtil.createWorkBook(new String[]{"告警列表"}, resultData));
        return "/" + routeMark + path + "/" + filename + ".xls";
    }

    private String[][][] alarmHistoryDataCreate(List<AlarmBusiness> alarmHistorys) {
        String[] tableHead = null;
        tableHead = new String[]{"告警名称", "告警等级", "告警值", "区域层级", "站点名称", "设备名称", "告警详情", "告警发生时间", "告警恢复时间", "确认时间", "确认人", "清除时间", "清除人"};
        String[] title = new String[tableHead.length];
        int colLength = tableHead.length; // 列
        int rowLength = alarmHistorys.size() + 1; //行
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
            AlarmBusiness alarmHistory = alarmHistorys.get(i - 1);
            row[0] = alarmHistory.getName();
            row[1] = alarmHistory.getLevelName();
            row[2] = String.valueOf(alarmHistory.getValue());
            row[3] = alarmHistory.getTierName();
            row[4] = alarmHistory.getSiteName();
            row[5] = alarmHistory.getDeviceName();
            row[6] = alarmHistory.getName();
            Date treport = alarmHistory.getTreport();
            row[7] = DateUtil.getInstance().format(treport);
            Date trecover = alarmHistory.getTrecover();
            row[8] = trecover == null ? null : DateUtil.getInstance().format(trecover);
            Date checkTime = alarmHistory.getCheckTime();
            row[9] = DateUtil.getInstance().format(checkTime);
            FacadeView checker = alarmHistory.getChecker();
            row[10] = checker == null ? null : checker.getName();
            row[11] = trecover == null ? null : DateUtil.getInstance().format(trecover);
            FacadeView recoverMan = alarmHistory.getRecoverMan();
            row[12] = recoverMan == null ? null : recoverMan.getName();

        }
        return new String[][][]{sheetData};


    }


}
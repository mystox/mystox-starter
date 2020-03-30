package com.kongtrolink.framework.reports.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.utils.ReflectionUtils;
import com.kongtrolink.framework.entity.OperaCodePrefix;
import com.kongtrolink.framework.reports.api.SystemReportsService;
import com.kongtrolink.framework.reports.dao.*;
import com.kongtrolink.framework.reports.entity.*;
import com.kongtrolink.framework.reports.entity.SystemReports.ElectricCountTemp;
import com.kongtrolink.framework.reports.entity.SystemReports.StationBreakTemp;
import com.kongtrolink.framework.reports.entity.SystemReports.StationOffDetailsTemp;
import com.kongtrolink.framework.reports.entity.SystemReports.StationOffStatisticsTemp;
import com.kongtrolink.framework.reports.entity.query.DeviceEntity;
import com.kongtrolink.framework.reports.entity.query.FsuEntity;
import com.kongtrolink.framework.reports.entity.query.SiteEntity;
import com.kongtrolink.framework.reports.service.MqttCommonInterface;
import com.kongtrolink.framework.reports.stereotype.ReportExtend;
import com.kongtrolink.framework.reports.stereotype.ReportOperaCode;
import com.kongtrolink.framework.reports.utils.CommonCheck;
import com.kongtrolink.framework.reports.utils.DateUtil;
import com.kongtrolink.framework.reports.utils.WorkbookUtil;
import com.kongtrolink.framework.service.MqttOpera;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * \* @Author: mystox
 * \* Date: 2020/3/2 16:00
 * \* Description:
 * \
 */
@Service
public class SystemReportsServiceImpl implements SystemReportsService {
    Logger logger = LoggerFactory.getLogger(SystemReportsServiceImpl.class);
    @Autowired
    MqttOpera mqttOpera;

    @Autowired
    MqttCommonInterface mqttCommonInterface;
    @Autowired
    ReportTaskDao reportTaskDao;
    @Autowired
    StationOffStatisticsTempDao stationOffStatisticsTempDao;
    @Autowired
    StationOffDetailsTempDao stationOffDetailsTempDao;

    @Autowired
    StationBreakTempDao stationBreakTempDao;
    @Autowired
    ElectricCountTempDao electricCountTempDao;
    /////////////////////////////         站点停电统计         /////////////////////////////////////

    @Override
    @ReportOperaCode(code = OperaCodePrefix.REPORTS + "stationOffStatistic", rhythm = 20, dataType = {DataType.JSON}, extend = {
            @ReportExtend(field = "month", name = "月份", type = ReportExtend.FieldType.STRING), //时间类型是否需要
            @ReportExtend(field = "region", name = "区域层级", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, value = "/proxy_ap/region/getCurrentRegion"), //区域层级
            @ReportExtend(field = "stationList", name = "区域层级(站点级)", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, value = "/region/getStationList"), //站点列表
            @ReportExtend(field = "currentUser", name = "当前用户", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, value = "/proxy_ap/commonFunc/getUserInfo", hide = true), //当前用户信息
            @ReportExtend(field = "stationType", name = "站点类型", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "A级机房", "B级机房", "C级机房", "D级机房"}),
            @ReportExtend(field = "fsuManufactory", name = "fsu厂家", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "义益钛迪"}),
            @ReportExtend(field = "statisticLevel", name = "统计维度", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"省级", "市级", "县级", "站点级"}),
            @ReportExtend(field = "period", name = "统计周期", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"月报表", "季报表", "年报表"}),
            @ReportExtend(field = "timePeriod", name = "时间段", type = ReportExtend.FieldType.DATE, belong = ExecutorType.query),
    })
    public ReportData stationOffStatistics(String reportConfigStr) {
        ReportConfig reportConfig = JSONObject.parseObject(reportConfigStr, ReportConfig.class);
        ExecutorType executorType = reportConfig.getExecutorType();
        ReportTask reportTask = reportTaskDao.findByByUniqueCondition(
                reportConfig.getServerCode(), reportConfig.getEnterpriseCode(), reportConfig.getOperaCode(), reportConfig.getReportServerCode());
        if (reportTask == null)
            return new ReportData(DataType.ERROR, "report task is not exists [" + JSONObject.toJSONString(reportTask) + "]");
        if (ExecutorType.query.equals(executorType))
            return stationOffStatisticsQuery(reportConfig, reportTask);
        else return stationOffStatisticsExecutor(reportConfig, reportTask);
    }

    private ReportData stationOffStatisticsExecutor(ReportConfig reportConfig, ReportTask reportTask) {

        String serverCode = reportConfig.getServerCode();
        String enterpriseCode = reportConfig.getEnterpriseCode();
        List<StationOffStatisticsTemp> stationOffStatisticsTemps = new ArrayList<>();
        //获取时间信息
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);

        if (month == 1) {
            month = 12;
            year -= 1;
        } else month -= 1;


        JSONObject baseCondition = new JSONObject();
        baseCondition.put("serverCode", serverCode);
        baseCondition.put("enterpriseCode", enterpriseCode);
        // 获取企业在该云平台下所有站点
        List<SiteEntity> siteList = mqttCommonInterface.getSiteList(baseCondition);

        if (!CollectionUtils.isEmpty(siteList)) {
            int finalMonth = month;
            int finalYear = year;
            siteList.forEach(s -> {
                // 获取站点名称
                String address = s.getAddress();
                String addressName = mqttCommonInterface.getRegionName(address);
                List<String> addressArr = JSONArray.parseArray(addressName, String.class);
                String province = addressArr.get(0); //省
                String municipality = addressArr.get(1); //市
                String county = addressArr.get(2); //县|区
                // 获取站点下所有fsu及其相关告警数据
                String stationId = s.getSiteId();
                String stationName = s.getSiteName();
                String siteType = s.getSiteType();
                List<FsuEntity> fsuList = mqttCommonInterface.getFsuList(stationId, baseCondition);
                //判断交维态
                if (fsuList == null) {
                    logger.error("get fsu list is null:[{}]", JSONObject.toJSONString(s));
                    return;
                }
                FsuEntity fsuEntity = fsuList.get(0);
                String manufacturer = fsuEntity.getManufacturer();
                String operationState = CommonCheck.fsuOperaStateCheck(fsuList);
                List<String> fsuIds = ReflectionUtils.convertElementPropertyToList(fsuList, "fsuId");
                baseCondition.put("deviceTye", "开关电源");
                List<DeviceEntity> deviceList = mqttCommonInterface.getDeviceList(fsuIds, baseCondition);
                List<String> deviceIds = ReflectionUtils.convertElementPropertyToList(deviceList, "deviceId");
                deviceIds.addAll(fsuIds);
                JSONObject jsonObject = mqttCommonInterface.stationOffStatistic(deviceIds, finalYear, finalMonth, baseCondition);
                //交流电停电告警

                //填充站点告警统计信息
                StationOffStatisticsTemp stationOffStatisticsTemp = new StationOffStatisticsTemp();
                stationOffStatisticsTemp.setYear(finalYear);
                stationOffStatisticsTemp.setMonth(finalMonth);
                stationOffStatisticsTemp.setStationId(stationId);
                stationOffStatisticsTemp.setStationName(stationName);
                stationOffStatisticsTemp.setStationType(siteType);
                stationOffStatisticsTemp.setOperationState(operationState);
                stationOffStatisticsTemp.setProvince(province);
                stationOffStatisticsTemp.setMunicipality(municipality);
                stationOffStatisticsTemp.setCounty(county);
                stationOffStatisticsTemp.setFsuManufactory(manufacturer);
                Double durationSum = jsonObject.getDouble("durationSum");
                stationOffStatisticsTemp.setSumDuration(durationSum);
                Integer count = jsonObject.getInteger("count");
                stationOffStatisticsTemp.setTimes(count);
                stationOffStatisticsTemps.add(stationOffStatisticsTemp);
            });
        } else {
            logger.error("返回站点结果为空");
        }
        String reportTaskId = reportTask.getId();
        if (!CollectionUtils.isEmpty(stationOffStatisticsTemps)) {
            stationOffStatisticsTempDao.updateDelete(year, month, reportTaskId);
            stationOffStatisticsTempDao.save(stationOffStatisticsTemps, reportTaskId);
        }

        stationOffStatisticsTempDao.save(stationOffStatisticsTemps, reportTask.getId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("executorTime", year + "-" + month);
        jsonObject.put("executorResult", stationOffStatisticsTemps);
        return new ReportData(DataType.TEXT, jsonObject.toJSONString());
    }

    private ReportData stationOffStatisticsQuery(ReportConfig reportConfig, ReportTask reportTask) {

        JSONObject condition = reportConfig.getCondition();//获取查询条件
        String taskId = reportTask.getId();
        String statisticLevel = condition.getString("statisticLevel");
        JSONObject currentUser = condition.getJSONObject("currentUser");
        if (currentUser == null) currentUser = new JSONObject();
        String period = condition.getString("period");
        TimePeriod timePeriod = condition.getObject("timePeriod", TimePeriod.class);
        if (timePeriod == null) {
            timePeriod = new TimePeriod();
            timePeriod.setEndTime(new Date(System.currentTimeMillis()));
            timePeriod.setStartTime(DateUtil.getInstance().getFirstDayOfMonth());
        }

        List<JSONObject> stationOffStatisticsResult = stationOffStatisticsTempDao.getStationOffStatisticsByCondition(taskId, condition, timePeriod);


        String[][][] resultData = stationOffStatisticsDataCreate(stationOffStatisticsResult, statisticLevel);
        //统计告警量
        String resultType = reportConfig.getDataType();
        if (!DataType.FILE.equals(resultType)) {
            JsonData jsonData = new JsonData();
            jsonData.setName("站点停电统计表");
            List<String[]> dataArr = Arrays.asList(resultData[0]);
            dataArr = new ArrayList<>(dataArr);
            dataArr.remove(0);
            jsonData.setData(dataArr.toArray(new String[dataArr.size()][]));
            jsonData.setUnit("");
            jsonData.setxAxis(resultData[0][0]);
            ReportData reportData = new ReportData(DataType.TABLE, JSONObject.toJSONString(jsonData));
            return reportData;
        }
        int length = resultData[0].length;
        resultData[0][length - 4] = new String[]{""};
        resultData[0][length - 3] = new String[]{"统计周期:" + period};
        resultData[0][length - 2] = new String[]{"时间段:" + timePeriod.getStartTimeStr() + "-" + timePeriod.getEndTimeStr()};
        resultData[0][length - 1] = new String[]{"操作人员:" + currentUser.getString("name")};
        String excelUri = stationOffStatisticsExcelCreate("站点停电统计表-" + statisticLevel, resultData);
        ReportData reportData = new ReportData(DataType.FORM, excelUri);
        return reportData;


    }

    private String[][][] stationOffStatisticsDataCreate(List<JSONObject> stationOffStatisticsResult, String statisticLevel) {

        String[] tableHead = null;
        if (StringUtils.equalsAny(statisticLevel, "省级", "市级", "区县级")) {
            tableHead = new String[]{"区域层级", "站点总数", "停电时长（分钟）", "停电次数", "平均站点停电时长（分钟）"};
        } else
            tableHead = new String[]{"区域层级", "站点名称", "站点编号", "站点类型", "运行状态", "生产厂家", "停电时长（分钟）", "停电次数", "平均站点停电时长（分钟）"};

        int colLength = tableHead.length; // 列
        int rowLength = stationOffStatisticsResult.size() + 1; //行
        String[][] sheetData = new String[rowLength + 4][colLength];

        for (int i = 0; i < rowLength; i++) {
            String[] row = sheetData[i];
            if (i == 0) sheetData[i] = tableHead;
            JSONObject jsonObject = stationOffStatisticsResult.get(i - 1);
            row[0] = CommonCheck.aggregateTierName(jsonObject);
            int a = 0;
            if (colLength > 5) {
                row[1] = jsonObject.getString("stationName");
                row[2] = jsonObject.getString("stationId");
                row[3] = jsonObject.getString("stationType");
                row[4] = jsonObject.getString("operationState");
                row[5] = jsonObject.getString("fsuManufactory");
                a = 5;
            }
            Double sumDuration = jsonObject.getDouble("sumDuration");
            Integer times = jsonObject.getInteger("times");
            row[1 + a] = String.valueOf(sumDuration);
            row[2 + a] = String.valueOf(times);
            row[3 + a] = String.valueOf(sumDuration / times);

        }
        return new String[][][]{sheetData};
    }

    private String stationOffStatisticsExcelCreate(String s, String[][][] resultData) {
        long currentTime = System.currentTimeMillis();
        String path = "/reportsResources/report_stationOffStatistic";
        String filename = "站点停电统计表_" + currentTime + ".xls";
        WorkbookUtil.save("." + path, filename, WorkbookUtil.createWorkBook(new String[]{s}, resultData));
        return path + "/" + filename;
    }

    /////////////////////////////         站点停电明细         /////////////////////////////////////

    @Override
    @ReportOperaCode(code = OperaCodePrefix.REPORTS + "stationOffDetails", rhythm = 20, dataType = {DataType.JSON}, extend = {
            @ReportExtend(field = "month", name = "月份", type = ReportExtend.FieldType.STRING), //时间类型是否需要
            @ReportExtend(field = "region", name = "区域层级", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, value = "/proxy_ap/region/getCurrentRegion"), //区域层级
            @ReportExtend(field = "stationList", name = "区域层级(站点级)", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, value = "/region/getStationList", hide = true), //站点列表
            @ReportExtend(field = "currentUser", name = "当前用户", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, value = "/proxy_ap/commonFunc/getUserInfo", hide = true), //当前用户信息
            @ReportExtend(field = "stationType", name = "站点类型", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "A级机房", "B级机房", "C级机房", "D级机房"}),
            @ReportExtend(field = "statisticLevel", name = "统计维度", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"省级", "市级", "县级", "站点级"}),
            @ReportExtend(field = "period", name = "统计周期", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"月报表", "季报表", "年报表"}),
            @ReportExtend(field = "timePeriod", name = "时间段", type = ReportExtend.FieldType.DATE, belong = ExecutorType.query),
    })
    public ReportData stationOffDetails(String reportConfigStr) {
        ReportConfig reportConfig = JSONObject.parseObject(reportConfigStr, ReportConfig.class);
        ExecutorType executorType = reportConfig.getExecutorType();
        ReportTask reportTask = reportTaskDao.findByByUniqueCondition(
                reportConfig.getServerCode(), reportConfig.getEnterpriseCode(), reportConfig.getOperaCode(), reportConfig.getReportServerCode());
        if (reportTask == null)
            return new ReportData(DataType.ERROR, "report task is not exists [" + JSONObject.toJSONString(reportTask) + "]");
        if (ExecutorType.query.equals(executorType))
            return stationOffDetailsQuery(reportConfig, reportTask);
        else return stationOffDetailsExecutor(reportConfig, reportTask);
    }

    private ReportData stationOffDetailsExecutor(ReportConfig reportConfig, ReportTask reportTask) {
        String serverCode = reportConfig.getServerCode();
        String enterpriseCode = reportConfig.getEnterpriseCode();
        List<StationOffDetailsTemp> stationOffDetailsTemps = new ArrayList<StationOffDetailsTemp>();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);

        if (month == 1) {
            month = 12;
            year -= 1;
        } else month -= 1;

        JSONObject baseCondition = new JSONObject();
        baseCondition.put("serverCode", serverCode);
        baseCondition.put("enterpriseCode", enterpriseCode);
        // 获取企业在该云平台下所有站点
        List<SiteEntity> siteList = mqttCommonInterface.getSiteList(baseCondition);
        if (!CollectionUtils.isEmpty(siteList)) {
            int finalMonth = month;
            int finalYear = year;
            siteList.forEach(s -> {
                // 获取站点名称
                String address = s.getAddress();
                String addressName = mqttCommonInterface.getRegionName(address);
                List<String> addressArr = JSONArray.parseArray(addressName, String.class);
                String province = addressArr.get(0); //省
                String municipality = addressArr.get(1); //市
                String county = addressArr.get(2); //县|区
                // 获取站点下所有fsu及其相关告警数据
                String stationId = s.getSiteId();
                String stationName = s.getSiteName();
                String siteType = s.getSiteType();

                List<FsuEntity> fsuList = mqttCommonInterface.getFsuList(stationId, baseCondition);
                //判断交维态
                if (fsuList == null) {
                    logger.error("get fsu list is null:[{}]", JSONObject.toJSONString(s));
                    return;
                }
                FsuEntity fsuEntity = fsuList.get(0);
                String manufacturer = fsuEntity.getManufacturer();
                String operationState = CommonCheck.fsuOperaStateCheck(fsuList);
                List<String> fsuIds = ReflectionUtils.convertElementPropertyToList(fsuList, "fsuId");
                // 获取上月告警统计信息 ,包括多项告警统计信息 根据等级统计上个月内的所有历史告警数量和告警恢复数量
                logger.debug("statistic station off ByDeviceIds, site reportTaskId is [{}]", stationId);
                List<JSONObject> jsonObjects = mqttCommonInterface.getStationOffDetails(fsuIds, finalYear, finalMonth, baseCondition);

                jsonObjects.forEach(entity -> {
                    //填充站点告警统计信息
                    StationOffDetailsTemp stationOffDetailsTemp = new StationOffDetailsTemp();
                    stationOffDetailsTemp.setYear(finalYear);
                    stationOffDetailsTemp.setMonth(finalMonth);
                    stationOffDetailsTemp.setStationId(stationId);
                    stationOffDetailsTemp.setStationName(stationName);
                    stationOffDetailsTemp.setStationType(siteType);
                    stationOffDetailsTemp.setOperationState(operationState);
                    stationOffDetailsTemp.setProvince(province);
                    stationOffDetailsTemp.setMunicipality(municipality);
                    stationOffDetailsTemp.setCounty(county);
                    stationOffDetailsTemp.setFsuManufactory(manufacturer);
                    Date treport = entity.getDate("treport");
                    stationOffDetailsTemp.setStartTime(treport);
                    Date trecover = entity.getDate("trecover");
                    stationOffDetailsTemp.setEndTime(trecover);
                    Double duration = (double) (trecover.getTime() - treport.getTime()) / (1000 * 60);
                    stationOffDetailsTemp.setDuration(duration);//分钟
                    stationOffDetailsTemps.add(stationOffDetailsTemp);

                });
            });
        } else {
            logger.error("返回站点结果为空");
        }
        String reportTaskId = reportTask.getId();
        if (!CollectionUtils.isEmpty(stationOffDetailsTemps)) {
            stationOffDetailsTempDao.updateDelete(year, month, reportTaskId);
            stationOffDetailsTempDao.save(stationOffDetailsTemps, reportTaskId);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("executorTime", year + "-" + month);
        jsonObject.put("executorResult", stationOffDetailsTemps);
        return new ReportData(DataType.TEXT, jsonObject.toJSONString());
    }

    private ReportData stationOffDetailsQuery(ReportConfig reportConfig, ReportTask reportTask) {
        JSONObject condition = reportConfig.getCondition();//获取查询条件
        String taskId = reportTask.getId();
        String period = condition.getString("period");
        TimePeriod timePeriod = condition.getObject("timePeriod", TimePeriod.class);
        if (timePeriod == null) {
            timePeriod = new TimePeriod();
            timePeriod.setEndTime(new Date(System.currentTimeMillis()));
            timePeriod.setStartTime(DateUtil.getInstance().getFirstDayOfMonth());
        }
        //根据用户id的区域权限及其搜索条件获取站点列表筛选
        JSONObject currentUser = condition.getJSONObject("currentUser");
        if (currentUser == null) currentUser = new JSONObject();
        List<StationOffDetailsTemp> stationOffDetailsTemps = stationOffDetailsTempDao.getStationOffDetailsByCondition(taskId, condition, timePeriod);
        //统计离线明细，生成excel数据表
        String[][][] resultData = stationOffDetailsDataCreate(stationOffDetailsTemps);
        //统计告警量
        String resultType = reportConfig.getDataType();
        if (!DataType.FILE.equals(resultType)) {
            ReportData reportData = new ReportData(DataType.FORM, JSONObject.toJSONString(resultData));
            return reportData;
        }
        int length = resultData[0].length;
        resultData[0][length - 4] = new String[]{""};
        resultData[0][length - 3] = new String[]{"统计周期:" + period};
        resultData[0][length - 2] = new String[]{"时间段:" + timePeriod.getStartTimeStr() + "-" + timePeriod.getEndTimeStr()};
        resultData[0][length - 1] = new String[]{"操作人员:" + currentUser.getString("name")};
        String excelUri = stationOffDetailsExcelCreate("站点停电明细表", resultData);
        ReportData reportData = new ReportData(DataType.FORM, excelUri);
        return reportData;
    }

    private String[][][] stationOffDetailsDataCreate(List<StationOffDetailsTemp> stationOffDetailsResult) {
        String[] tableHead = null;
        tableHead = new String[]{"区域层级", "站点名称", "站点编号", "站点类型", "运行状态", "开始时间", "结束时间", "时长"};

        int colLength = tableHead.length; // 列
        int rowLength = stationOffDetailsResult.size() + 1; //行
        String[][] sheetData = new String[colLength][rowLength];

        for (int i = 0; i < rowLength; i++) {
            String[] row = sheetData[i];
            if (i == 0) {
                sheetData[i] = tableHead;
                continue;
            }
            StationOffDetailsTemp stationOffDetailsTemp = stationOffDetailsResult.get(i - 1);
            row[0] = CommonCheck.aggregateTierName((JSONObject) JSONObject.toJSON(stationOffDetailsTemp));
            int a = 0;
            row[1 + a] = stationOffDetailsTemp.getStationName();
            row[2 + a] = stationOffDetailsTemp.getStationId();
            row[3 + a] = stationOffDetailsTemp.getStationType();
            row[4 + a] = stationOffDetailsTemp.getOperationState();
            row[5 + a] = DateUtil.getInstance().format(stationOffDetailsTemp.getStartTime());
            row[6 + a] = DateUtil.getInstance().format(stationOffDetailsTemp.getEndTime());
            row[7 + a] = stationOffDetailsTemp.getDuration() + "";
        }
        return new String[][][]{sheetData};
    }

    private String stationOffDetailsExcelCreate(String sheetName, String[][][] resultData) {
        long currentTime = System.currentTimeMillis();
        String path = "/reportsResources/report_stationOffDetails";
        String filename = "站点停电明细表_" + currentTime + ".xls";
        HSSFWorkbook workBook = WorkbookUtil.createWorkBook(new String[]{sheetName}, resultData);
        WorkbookUtil.save("." + path, filename, workBook);
        return path + "/" + filename;
    }

    /////////////////////////////         市电停电断站统计表         /////////////////////////////////////
    @ReportOperaCode(code = OperaCodePrefix.REPORTS + "stationOffStatistic", rhythm = 20, dataType = {DataType.JSON}, extend = {
            @ReportExtend(field = "month", name = "月份", type = ReportExtend.FieldType.STRING), //时间类型是否需要
            @ReportExtend(field = "region", name = "区域层级", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, value = "/proxy_ap/region/getCurrentRegion"), //区域层级
            @ReportExtend(field = "stationList", name = "区域层级(站点级)", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, value = "/region/getStationList"), //站点列表
            @ReportExtend(field = "currentUser", name = "当前用户", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, value = "/proxy_ap/commonFunc/getUserInfo", hide = true), //当前用户信息
            @ReportExtend(field = "stationType", name = "站点类型", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "A级机房", "B级机房", "C级机房", "D级机房"}),
            @ReportExtend(field = "fsuManufactory", name = "fsu厂家", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "义益钛迪"}),
            @ReportExtend(field = "statisticLevel", name = "统计维度", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"省级", "市级", "县级", "站点级"}),
            @ReportExtend(field = "period", name = "统计周期", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"月报表", "季报表", "年报表"}),
            @ReportExtend(field = "timePeriod", name = "时间段", type = ReportExtend.FieldType.DATE, belong = ExecutorType.query),
    })
    @Override
    public ReportData stationBreak(String reportConfigStr) {
        ReportConfig reportConfig = JSONObject.parseObject(reportConfigStr, ReportConfig.class);
        ExecutorType executorType = reportConfig.getExecutorType();
        ReportTask reportTask = reportTaskDao.findByByUniqueCondition(
                reportConfig.getServerCode(), reportConfig.getEnterpriseCode(), reportConfig.getOperaCode(), reportConfig.getReportServerCode());
        if (reportTask == null)
            return new ReportData(DataType.ERROR, "report task is not exists [" + JSONObject.toJSONString(reportTask) + "]");
        if (ExecutorType.query.equals(executorType))
            return stationBreakQuery(reportConfig, reportTask);
        else return stationBreakExecutor(reportConfig, reportTask);

    }

    /**
     * @return com.kongtrolink.framework.reports.entity.ReportData
     * @Date 12:35 2020/3/5
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description 获取站点id和fsuId生成每月的统计数据
     **/
    private ReportData stationBreakExecutor(ReportConfig reportConfig, ReportTask reportTask) {
        String serverCode = reportConfig.getServerCode();
        String enterpriseCode = reportConfig.getEnterpriseCode();
        List<StationOffDetailsTemp> stationOffDetailsTemps = new ArrayList<StationOffDetailsTemp>();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);

        if (month == 1) {
            month = 12;
            year -= 1;
        } else month -= 1;

        List<StationBreakTemp> stationBreakTemps = new ArrayList<>();
        JSONObject baseCondition = new JSONObject();
        baseCondition.put("serverCode", serverCode);
        baseCondition.put("enterpriseCode", enterpriseCode);
        // 获取企业在该云平台下所有站点
        List<SiteEntity> siteList = mqttCommonInterface.getSiteList(baseCondition);
        if (!CollectionUtils.isEmpty(siteList)) {
            int finalMonth = month;
            int finalYear = year;
            siteList.forEach(s -> {
                // 获取站点名称
                String address = s.getAddress();
                String addressName = mqttCommonInterface.getRegionName(address);
                List<String> addressArr = JSONArray.parseArray(addressName, String.class);
                String province = addressArr.get(0); //省
                String municipality = addressArr.get(1); //市
                String county = addressArr.get(2); //县|区
                // 获取站点下所有fsu及其相关告警数据
                String stationId = s.getSiteId();
                String stationName = s.getSiteName();
                String siteType = s.getSiteType();

                List<FsuEntity> fsuList = mqttCommonInterface.getFsuList(stationId, baseCondition);
                //判断交维态
                if (fsuList == null) {
                    logger.error("get fsu list is null:[{}]", JSONObject.toJSONString(s));
                    return;
                }
                //获取该站的断站统计信息
                FsuEntity fsuEntity = fsuList.get(0);
                String manufacturer = fsuEntity.getManufacturer();
                String operationState = CommonCheck.fsuOperaStateCheck(fsuList);
                List<String> fsuIds = ReflectionUtils.convertElementPropertyToList(fsuList, "fsuId");
                // 获取上月告警统计信息 ,包括多项告警统计信息 根据等级统计上个月内的所有历史告警数量和告警恢复数量
                logger.debug("statistic station off ByDeviceIds, site reportTaskId is [{}]", stationId);
                JSONObject jsonObjects = mqttCommonInterface.getStationBreakStatistic(fsuIds, finalYear, finalMonth, baseCondition);

                StationBreakTemp stationBreakTemp = new StationBreakTemp();
                stationBreakTemp.setYear(finalYear);
                stationBreakTemp.setMonth(finalMonth);
                stationBreakTemp.setMunicipality(municipality);
                stationBreakTemp.setProvince(province);
                stationBreakTemp.setCounty(county);
                stationBreakTemp.setStationId(stationId);
                stationBreakTemp.setStationName(stationName);
                stationBreakTemp.setStationType(siteType);
                stationBreakTemp.setOperationState(operationState);
                stationBreakTemp.setFsuManufactory(manufacturer);
                stationBreakTemp.setAvgMonth(jsonObjects.getDouble("durationSum"));
                stationBreakTemps.add(stationBreakTemp);
            });
        } else {
            logger.error("返回站点结果为空");
        }

        String reportTaskId = reportTask.getId();
        if (!CollectionUtils.isEmpty(stationOffDetailsTemps)) {
            stationBreakTempDao.updateDelete(year, month, reportTaskId);
            stationBreakTempDao.save(stationBreakTemps, reportTaskId);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("executorTime", year + "-" + month);
        jsonObject.put("executorResult", stationOffDetailsTemps);
        return new ReportData(DataType.TEXT, jsonObject.toJSONString());
    }

    private ReportData stationBreakQuery(ReportConfig reportConfig, ReportTask reportTask) {
        JSONObject condition = reportConfig.getCondition();//获取查询条件
        String taskId = reportTask.getId();
        JSONObject currentUser = condition.getJSONObject("currentUser");
        if (currentUser == null) currentUser = new JSONObject();
        String period = condition.getString("period");
        TimePeriod timePeriod = condition.getObject("timePeriod", TimePeriod.class);
        if (timePeriod == null) {
            timePeriod = new TimePeriod();
            timePeriod.setEndTime(new Date(System.currentTimeMillis()));
            timePeriod.setStartTime(DateUtil.getInstance().getFirstDayOfMonth());
        }
        List<JSONObject> stationBreakResult = stationBreakTempDao.getStationBreakByCondition(taskId, condition, timePeriod);
        String[][][] resultData = stationBreakDataCreate(stationBreakResult);
        String resultType = reportConfig.getDataType();
        if (!DataType.FILE.equals(resultType)) {
            JsonData jsonData = new JsonData();
            jsonData.setName("站点停电明细表");
            List<String[]> dataArr = Arrays.asList(resultData[0]);
            dataArr = new ArrayList<>(dataArr);
            dataArr.remove(0);
            jsonData.setData(dataArr.toArray(new String[dataArr.size()][]));
            jsonData.setUnit("");
            jsonData.setxAxis(resultData[0][0]);
            ReportData reportData = new ReportData(DataType.TABLE, JSONObject.toJSONString(jsonData));
            return reportData;
        }
        int length = resultData[0].length;
        resultData[0][length - 4] = new String[]{""};
        resultData[0][length - 3] = new String[]{"统计周期:" + period};
        resultData[0][length - 2] = new String[]{"时间段:" + timePeriod.getStartTimeStr() + "-" + timePeriod.getEndTimeStr()};
        resultData[0][length - 1] = new String[]{"操作人员:" + currentUser.getString("name")};
        String excelUri = stationOffDetailsExcelCreate("站点停电明细表-", resultData);
        ReportData reportData = new ReportData(DataType.FORM, excelUri);
        return reportData;
    }

    private String[][][] stationBreakDataCreate(List<JSONObject> stationBreakResult) {
        String[] tableHead = null;
        tableHead = new String[]{"区域层级", "站点总数", "交维站点数", "月累计平均断站时长(分钟)"};

        int colLength = tableHead.length; // 列
        int rowLength = stationBreakResult.size() + 1; //行
        String[][] sheetData = new String[rowLength + 4][colLength];

        for (int i = 0; i < rowLength; i++) {
            String[] row = sheetData[i];
            if (i == 0) {
                sheetData[i] = tableHead;
                continue;
            }
            JSONObject jsonObject = stationBreakResult.get(i - 1);
            row[0] = CommonCheck.aggregateTierName(jsonObject);
            int a = 0;
            Integer siteCount = jsonObject.getInteger("siteCount");
            row[1 + a] = String.valueOf(siteCount);
            Integer maintenanceSCount = jsonObject.getInteger("maintenanceSCount");
            row[2 + a] = String.valueOf(maintenanceSCount);
            Double durationSum = jsonObject.getDouble("durationSum");
            row[3 + a] = String.valueOf(durationSum / maintenanceSCount);
        }
        return new String[][][]{sheetData};
    }

    @ReportOperaCode(code = OperaCodePrefix.REPORTS + "stationOffStatistic", rhythm = 20, dataType = {DataType.JSON}, extend = {
            @ReportExtend(field = "month", name = "月份", type = ReportExtend.FieldType.STRING), //时间类型是否需要
            @ReportExtend(field = "stationList", name = "区域层级(站点级)", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, value = "/region/getStationList"), //站点列表
            @ReportExtend(field = "currentUser", name = "当前用户", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, value = "/proxy_ap/commonFunc/getUserInfo", hide = true), //当前用户信息
            @ReportExtend(field = "stationType", name = "站点类型", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "A级机房", "B级机房", "C级机房", "D级机房"}),
            @ReportExtend(field = "fsuManufactory", name = "fsu厂家", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "义益钛迪"}),
            @ReportExtend(field = "statisticLevel", name = "统计维度", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"省级", "市级", "县级", "站点级"}),
            @ReportExtend(field = "period", name = "统计周期", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"月报表", "季报表", "年报表"}),
            @ReportExtend(field = "timePeriod", name = "时间段", type = ReportExtend.FieldType.DATE, belong = ExecutorType.query),
    })
    @Override
    public ReportData electricCount(String reportConfigStr) {
        ReportConfig reportConfig = JSONObject.parseObject(reportConfigStr, ReportConfig.class);
        ExecutorType executorType = reportConfig.getExecutorType();
        ReportTask reportTask = reportTaskDao.findByByUniqueCondition(
                reportConfig.getServerCode(), reportConfig.getEnterpriseCode(), reportConfig.getOperaCode(), reportConfig.getReportServerCode());
        if (reportTask == null)
            return new ReportData(DataType.ERROR, "report task is not exists [" + JSONObject.toJSONString(reportTask) + "]");
        if (ExecutorType.query.equals(executorType))
            return electricCountQuery(reportConfig, reportTask);
        else return electricCountExecutor(reportConfig, reportTask);
    }

    private ReportData electricCountExecutor(ReportConfig reportConfig, ReportTask reportTask) {
        String serverCode = reportConfig.getServerCode();
        String enterpriseCode = reportConfig.getEnterpriseCode();
        List<StationOffDetailsTemp> stationOffDetailsTemps = new ArrayList<StationOffDetailsTemp>();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);

        if (month == 1) {
            month = 12;
            year -= 1;
        } else month -= 1;

        List<ElectricCountTemp> electricCountTemps = new ArrayList<>();
        JSONObject baseCondition = new JSONObject();
        baseCondition.put("serverCode", serverCode);
        baseCondition.put("enterpriseCode", enterpriseCode);
        // 获取企业在该云平台下所有站点
        List<SiteEntity> siteList = mqttCommonInterface.getSiteList(baseCondition);
        if (!CollectionUtils.isEmpty(siteList)) {
            int finalMonth = month;
            int finalYear = year;
            siteList.forEach(s -> {
                // 获取站点名称
                String address = s.getAddress();
                String addressName = mqttCommonInterface.getRegionName(address);
                List<String> addressArr = JSONArray.parseArray(addressName, String.class);
                String province = addressArr.get(0); //省
                String municipality = addressArr.get(1); //市
                String county = addressArr.get(2); //县|区
                // 获取站点下所有fsu及其相关告警数据
                String stationId = s.getSiteId();
                String stationName = s.getSiteName();
                String siteType = s.getSiteType();

                List<FsuEntity> fsuList = mqttCommonInterface.getFsuList(stationId, baseCondition);
                //判断交维态
                if (fsuList == null) {
                    logger.error("get fsu list is null:[{}]", JSONObject.toJSONString(s));
                    return;
                }
                //获取该站的断站统计信息
                FsuEntity fsuEntity = fsuList.get(0);
                String manufacturer = fsuEntity.getManufacturer();
                String operationState = CommonCheck.fsuOperaStateCheck(fsuList);
                List<String> fsuIds = ReflectionUtils.convertElementPropertyToList(fsuList, "fsuId");
                // 获取上月告警统计信息 ,包括多项告警统计信息 根据等级统计上个月内的所有历史告警数量和告警恢复数量
                logger.debug("statistic station off ByDeviceIds, site reportTaskId is [{}]", stationId);
                JSONObject jsonObjects = mqttCommonInterface.getStationElectricCountList(fsuIds, finalYear, finalMonth, baseCondition);
                ElectricCountTemp electricCountTemp = jsonObjects.toJavaObject(ElectricCountTemp.class);
                electricCountTemp.setYear(finalYear);
                electricCountTemp.setMonth(finalMonth);
                electricCountTemp.setMunicipality(municipality);
                electricCountTemp.setProvince(province);
                electricCountTemp.setCounty(county);
                electricCountTemp.setStationName(stationName);
                electricCountTemp.setStationType(siteType);
                electricCountTemp.setOperationState(operationState);
                electricCountTemp.setFsuManufactory(manufacturer);
                electricCountTemp.setStartTime(DateUtil.getInstance().getFirstDayOfMonth(finalYear, finalMonth));
                electricCountTemp.setEndTime(DateUtil.getInstance().getLastDayOfMonth(finalYear, finalMonth));
                electricCountTemp.setsMobileElecCount(jsonObjects.getDouble("sMobileElecCount"));
                electricCountTemp.setsUnicomElecCount(jsonObjects.getDouble("sUnicomElecCount"));
                electricCountTemp.setsTelecomElecCount(jsonObjects.getDouble("sTelecomElecCount"));
                electricCountTemp.seteMobileElecCount(jsonObjects.getDouble("eMobileElecCount"));
                electricCountTemp.seteUnicomElecCount(jsonObjects.getDouble("eUnicomElecCount"));
                electricCountTemp.seteTelecomElecCount(jsonObjects.getDouble("eTelecomElecCount"));
                electricCountTemps.add(electricCountTemp);
            });
        } else {
            logger.error("返回站点结果为空");
        }

        String reportTaskId = reportTask.getId();
        if (!CollectionUtils.isEmpty(stationOffDetailsTemps)) {
            electricCountTempDao.updateDelete(year, month, reportTaskId);
            electricCountTempDao.save(electricCountTemps, reportTaskId);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("executorTime", year + "-" + month);
        jsonObject.put("executorResult", stationOffDetailsTemps);
        return new ReportData(DataType.TEXT, jsonObject.toJSONString());
    }

    private ReportData electricCountQuery(ReportConfig reportConfig, ReportTask reportTask) {

        JSONObject condition = reportConfig.getCondition();//获取查询条件
        String taskId = reportTask.getId();
        String statisticLevel = condition.getString("statisticLevel");
        JSONObject currentUser = condition.getJSONObject("currentUser");
        if (currentUser == null) currentUser = new JSONObject();
        String period = condition.getString("period");
        TimePeriod timePeriod = condition.getObject("timePeriod", TimePeriod.class);
        if (timePeriod == null) {
            timePeriod = new TimePeriod();
            timePeriod.setEndTime(new Date(System.currentTimeMillis()));
            timePeriod.setStartTime(DateUtil.getInstance().getFirstDayOfMonth());
        }

        List<JSONObject> electricCountResult = electricCountTempDao.getElectricCountByCondition(taskId, condition, timePeriod);
        String[][][] resultData = electricCountDataCreate(electricCountResult,timePeriod);
        //统计告警量
        String resultType = reportConfig.getDataType();
        if (!DataType.FILE.equals(resultType)) {
            JsonData jsonData = new JsonData();
            jsonData.setName("用电量统计报表");
            List<String[]> dataArr = Arrays.asList(resultData[0]);
            dataArr = new ArrayList<>(dataArr);
            dataArr.remove(0);
            jsonData.setData(dataArr.toArray(new String[dataArr.size()][]));
            jsonData.setUnit("");
            jsonData.setxAxis(resultData[0][0]);
            ReportData reportData = new ReportData(DataType.TABLE, JSONObject.toJSONString(jsonData));
            return reportData;
        }
        int length = resultData[0].length;
        resultData[0][length - 4] = new String[]{""};
        resultData[0][length - 3] = new String[]{"统计周期:" + period};
        resultData[0][length - 2] = new String[]{"时间段:" + timePeriod.getStartTimeStr() + "-" + timePeriod.getEndTimeStr()};
        resultData[0][length - 1] = new String[]{"操作人员:" + currentUser.getString("name")};
        String excelUri = electricCountExcelCreate("用电量统计报表", resultData);
        ReportData reportData = new ReportData(DataType.FORM, excelUri);
        return reportData;
    }

    private String electricCountExcelCreate(String sheetName, String[][][] resultData) {

        long currentTime = System.currentTimeMillis();
        String path = "/reportsResources/report_electricCount";
        String filename = "用电量统计报表_" + currentTime + ".xls";
        HSSFWorkbook workBook = WorkbookUtil.createWorkBook(new String[]{sheetName}, resultData);
        HSSFCellStyle cellStyle = workBook.createCellStyle();
        HSSFSheet sheet = workBook.getSheet(sheetName);
        cellStyle.setFillBackgroundColor((short) 13);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));
        HSSFCell cell1 = sheet.getRow(0).getCell(0);
        cell1.setCellStyle(cellStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 1, 1));
        HSSFCell cell2 = sheet.getRow(0).getCell(1);
        cell2.setCellStyle(cellStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 2, 2));
        HSSFCell cell3 = sheet.getRow(0).getCell(2);
        cell3.setCellStyle(cellStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 3, 3));
        HSSFCell cell4 = sheet.getRow(0).getCell(3);
        cell4.setCellStyle(cellStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 4, 4));
        HSSFCell cell5 = sheet.getRow(0).getCell(4);
        cell5.setCellStyle(cellStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 5, 7));
        HSSFCell cell6 = sheet.getRow(0).getCell(5);
        cell6.setCellStyle(cellStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 8, 10));
        HSSFCell cell7 = sheet.getRow(0).getCell(8);
        cell7.setCellStyle(cellStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 11, 13));
        HSSFCell cell8 = sheet.getRow(0).getCell(11);
        cell8.setCellStyle(cellStyle);


        WorkbookUtil.save("." + path, filename, workBook);
        return path + "/" + filename;

    }

    private String[][][] electricCountDataCreate(List<JSONObject> electricCountResult, TimePeriod timePeriod) {
        String[] tableHead1 = new String[]{"站点层级", "站点名称", "站点类型", "开始时间", "结束时间", "移动租户电量(KW·h)", "联通租户电量(KW·h)", "电信租户电量(KW·h)"};
        String[] tableHead2 = new String[]{"站点层级", "站点名称", "站点类型", "开始时间", "结束时间", "开始电量", "结束电量", "用电量", "开始电量", "结束电量", "用电量", "开始电量", "结束电量", "用电量"};

        int colLength = tableHead2.length; // 列
        int rowLength = electricCountResult.size() + 1; //行
        String[][] sheetData = new String[rowLength + 4][colLength];

        for (int i = 0; i < rowLength; i++) {
            String[] row = sheetData[i];
            if (i == 0) {
                sheetData[i] = tableHead1;
                continue;
            }
            if (i == 1) {
                sheetData[i] = tableHead2;
                continue;
            }
            JSONObject jsonObject = electricCountResult.get(i - 1);
            row[0] = CommonCheck.aggregateTierName(jsonObject);
            int a = 0;
            row[1 + a] = jsonObject.getString("stationName");
            row[2 + a] = jsonObject.getString("stationType");
            row[3 + a] = timePeriod.getStartTimeStr();
            row[4 + a] = timePeriod.getEndTimeStr();
            Double sMobileElecCount = jsonObject.getDouble("sMobileElecCount");
            Double eMobileElecCount = jsonObject.getDouble("eMobileElecCount");

            if (sMobileElecCount != null) row[5 + a] = String.valueOf(sMobileElecCount);
            if (eMobileElecCount != null) row[6 + a] = String.valueOf(eMobileElecCount);
            if (sMobileElecCount != null && eMobileElecCount != null)
                row[7 + a] = String.valueOf(eMobileElecCount - sMobileElecCount);


            Double sUnicomElecCount = jsonObject.getDouble("sUnicomElecCount");
            Double eUnicomElecCount = jsonObject.getDouble("eUnicomElecCount");
            if (sUnicomElecCount != null) row[8 + a] = String.valueOf(sUnicomElecCount);
            if (eUnicomElecCount != null) row[9 + a] = String.valueOf(eUnicomElecCount);
            if (sUnicomElecCount != null && eUnicomElecCount != null)
                row[10 + a] = String.valueOf(sUnicomElecCount - eUnicomElecCount);

            Double sTelecomElecCount = jsonObject.getDouble("sTelecomElecCount");
            Double eTelecomElecCount = jsonObject.getDouble("eTelecomElecCount");
            if (sTelecomElecCount != null) row[11 + a] = String.valueOf(eTelecomElecCount);
            if (eTelecomElecCount != null) row[12 + a] = String.valueOf(eTelecomElecCount);
            if (sTelecomElecCount != null && eTelecomElecCount != null)
                row[13 + a] = String.valueOf(sTelecomElecCount - eTelecomElecCount);
        }
        return new String[][][]{sheetData};
    }

    @Override
    public ReportData powerLoad(String reportConfigStr) {
        ReportConfig reportConfig = JSONObject.parseObject(reportConfigStr, ReportConfig.class);
        ExecutorType executorType = reportConfig.getExecutorType();
        if (ExecutorType.query.equals(executorType))
            return powerLoadQuery(reportConfig);
        else return powerLoadExecutor(reportConfig);
    }

    private ReportData powerLoadExecutor(ReportConfig reportConfig) {
        return null;
    }

    private ReportData powerLoadQuery(ReportConfig reportConfig) {
        return null;
    }
}
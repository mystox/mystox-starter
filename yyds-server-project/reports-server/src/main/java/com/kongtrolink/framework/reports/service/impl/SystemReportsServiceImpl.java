package com.kongtrolink.framework.reports.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.entity.OperaCodePrefix;
import com.kongtrolink.framework.entity.StateCode;
import com.kongtrolink.framework.reports.api.SystemReportsService;
import com.kongtrolink.framework.reports.dao.*;
import com.kongtrolink.framework.reports.entity.*;
import com.kongtrolink.framework.reports.entity.SystemReports.ElectricCountTemp;
import com.kongtrolink.framework.reports.entity.SystemReports.StationBreakTemp;
import com.kongtrolink.framework.reports.entity.SystemReports.StationOffDetailsTemp;
import com.kongtrolink.framework.reports.entity.SystemReports.StationOffStatisticsTemp;
import com.kongtrolink.framework.reports.stereotype.ReportExtend;
import com.kongtrolink.framework.reports.stereotype.ReportOperaCode;
import com.kongtrolink.framework.reports.utils.WorkbookUtil;
import com.kongtrolink.framework.service.MqttOpera;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * \* @Author: mystox
 * \* Date: 2020/3/2 16:00
 * \* Description:
 * \
 */
@Service
public class SystemReportsServiceImpl implements SystemReportsService {
    @Autowired
    MqttOpera mqttOpera;
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
    @ReportOperaCode(code = OperaCodePrefix.REPORTS + "stationOffDetails", rhythm = 20, dataType = {DataType.JSON}, extend = {
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
        //获取时间信息
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);

        if (month == 1) {
            month = 12;
            year -= 1;
        } else month -= 1;


        JSONObject stationCondition = new JSONObject();
        stationCondition.put("serverCode", serverCode);
        stationCondition.put("enterpriseCode", enterpriseCode);
        // 获取企业在该云平台下所有站点
        MsgResult stationListResp = mqttOpera.opera("getStationListByEnterpriseCode", stationCondition.toJSONString());
        String stationListMsg = stationListResp.getMsg();
//        List<JSONObject> stationList = JSONArray.parseArray(stationListMsg, JSONObject.class);
        //获取统计数据
        MsgResult statisticResp = mqttOpera.opera("getStationOffStatisticBySiteList", stationListMsg);
        String msg = statisticResp.getMsg();
        List<StationOffStatisticsTemp> stationOffStatisticsTemps = JSONArray.parseArray(msg, StationOffStatisticsTemp.class);

        int finalMonth = month;
        int finalYear = year;
        stationOffStatisticsTemps.forEach(s -> {
            s.setYear(finalYear);
            s.setMonth(finalMonth);
            s.setTempDate(new Date());
        });

        stationOffStatisticsTempDao.save(stationOffStatisticsTemps, reportTask.getId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("executorTime", year + "-" + month);
        jsonObject.put("executorResult", stationOffStatisticsTemps);
        return new ReportData(DataType.TEXT, jsonObject.toJSONString());
    }

    private ReportData stationOffStatisticsQuery(ReportConfig reportConfig, ReportTask reportTask) {

        String taskId = reportTask.getId();
        JSONObject condition = reportConfig.getCondition();//获取查询条件
        JSONArray stationList = condition.getJSONArray("stationList");
        JSONArray regionArr = condition.getJSONArray("region");
        String stationType = condition.getString("stationType");
        String fsuManufactory = condition.getString("fsuManufactory");
        String statisticLevel = condition.getString("statisticLevel");
        String period = condition.getString("period");
        JSONObject timePeriod = condition.getJSONObject("timePeriod");
        List<JSONObject> stationOffStatisticsResult = stationOffStatisticsTempDao.getStationOffStatisticsByCondition(taskId, stationList.toJavaList(JSONObject.class), statisticLevel, period, timePeriod);


        String[][][] resultData = stationOffStatisticsDataCreate(stationOffStatisticsResult, statisticLevel);
        //统计告警量
        String resultType = reportConfig.getDataType();
        if (!DataType.FILE.equals(resultType)) {
            ReportData reportData = new ReportData(DataType.FORM, JSONObject.toJSONString(resultData));
            return reportData;
        }
        JSONObject currentUser = condition.getJSONObject("currentUser");
        if (currentUser == null) currentUser = new JSONObject();
        int length = resultData[0].length;
        resultData[0][length - 4] = new String[]{""};
        resultData[0][length - 3] = new String[]{"统计周期:" + period};
        resultData[0][length - 2] = new String[]{"时间段:" + timePeriod.getString("startTime") + "-" + timePeriod.getString("endTime")};
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
        String[][] sheetData = new String[colLength][rowLength];

        for (int i = 0; i < rowLength; i++) {
            String[] row = sheetData[i];
            if (i == 0) sheetData[i] = tableHead;
            JSONObject jsonObject = stationOffStatisticsResult.get(i - 1);
            row[0] = jsonObject.getString("");
            int a = 0;
            if (colLength > 5) {
                row[1] = jsonObject.getString("");
                row[2] = jsonObject.getString("");
                row[3] = jsonObject.getString("");
                row[4] = jsonObject.getString("");
                a = 4;
            }
            row[1 + a] = jsonObject.getString("");
            row[2 + a] = jsonObject.getString("");
            row[3 + a] = jsonObject.getString("");

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
        //获取时间信息
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        JSONObject stationCondition = new JSONObject();
        stationCondition.put("serverCode", serverCode);
        stationCondition.put("enterpriseCode", enterpriseCode);
        // 获取企业在该云平台下所有站点
        MsgResult stationListResp = mqttOpera.opera("getStationListByEnterpriseCode", stationCondition.toJSONString());
        String stationListMsg = stationListResp.getMsg();
        //获取统计明细数据
        MsgResult statisticResp = mqttOpera.opera("getStationOffDetailsBySiteList", stationListMsg);
        String msg = statisticResp.getMsg();
        List<StationOffDetailsTemp> stationOffDetailsTemps = JSONArray.parseArray(msg, StationOffDetailsTemp.class);
        stationOffDetailsTempDao.save(stationOffDetailsTemps, reportTask.getId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("executorTime", year + "-" + month);
        jsonObject.put("executorResult", stationOffDetailsTemps);
        return new ReportData(DataType.TEXT, jsonObject.toJSONString());
    }

    private ReportData stationOffDetailsQuery(ReportConfig reportConfig, ReportTask reportTask) {
        JSONObject condition = reportConfig.getCondition();//获取查询条件

        JSONArray regionArr = condition.getJSONArray("region");
        String stationType = condition.getString("stationType");
        String runningSate = condition.getString("runningSate");
        String fsuManufactory = condition.getString("fsuManufactory");

        String statisticLevel = condition.getString("statisticLevel");//无效

        //根据用户id的区域权限及其搜索条件获取站点列表筛选
        JSONArray stationList = condition.getJSONArray("stationList");

        String period = condition.getString("period");
        JSONObject timePeriod = condition.getJSONObject("timePeriod");
        List<JSONObject> stationOffStatisticsResult = stationOffStatisticsTempDao.getStationOffStatisticsByCondition(reportTask.getId(), stationList.toJavaList(JSONObject.class), statisticLevel, period, timePeriod);
        String[][][] resultData = stationOffDetailsDataCreate(stationOffStatisticsResult);
        //统计告警量
        String resultType = reportConfig.getDataType();
        if (!DataType.FILE.equals(resultType)) {
            ReportData reportData = new ReportData(DataType.FORM, JSONObject.toJSONString(resultData));
            return reportData;
        }
        JSONObject currentUser = condition.getJSONObject("currentUser");
        if (currentUser == null) currentUser = new JSONObject();
        int length = resultData[0].length;
        resultData[0][length - 4] = new String[]{""};
        resultData[0][length - 3] = new String[]{"统计周期:" + period};
        resultData[0][length - 2] = new String[]{"时间段:" + timePeriod.getString("startTime") + "-" + timePeriod.getString("endTime")};
        resultData[0][length - 1] = new String[]{"操作人员:" + currentUser.getString("name")};
        String excelUri = stationOffDetailsExcelCreate("站点停电明细表", resultData);
        ReportData reportData = new ReportData(DataType.FORM, excelUri);
        return reportData;
    }

    private String[][][] stationOffDetailsDataCreate(List<JSONObject> stationOffDetailsResult) {
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
            JSONObject jsonObject = stationOffDetailsResult.get(i - 1);
            row[0] = jsonObject.getString("");
            int a = 0;
            row[1 + a] = jsonObject.getString("");
            row[2 + a] = jsonObject.getString("");
            row[3 + a] = jsonObject.getString("");
            row[4 + a] = jsonObject.getString("");
            row[5 + a] = jsonObject.getString("");
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
        //获取时间信息
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        JSONObject stationCondition = new JSONObject();
        stationCondition.put("serverCode", serverCode);
        stationCondition.put("enterpriseCode", enterpriseCode);
        // 获取企业在该云平台下所有站点
        MsgResult stationListResp = mqttOpera.opera("getStationListByEnterpriseCode", stationCondition.toJSONString());
        String stationListMsg = stationListResp.getMsg();
        //todo 获取站点下的 fsu 信息判断交维态的数据

        //todo 获取fsu下的开关电源设备id

        //todo 根据设备id 获取对应信号点告警统计信息 查询时间范围内的一脱站点数量和一脱告警列表


        List<StationBreakTemp> stationBreakTemps = new ArrayList<>();

        stationBreakTempDao.save(stationBreakTemps, reportTask.getId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("executorTime", year + "-" + month);
        jsonObject.put("executorResult", stationBreakTemps);
        return new ReportData(DataType.TEXT, jsonObject.toJSONString());

    }

    private ReportData stationBreakQuery(ReportConfig reportConfig, ReportTask reportTask) {
        JSONObject condition = reportConfig.getCondition();//获取查询条件

        JSONArray regionArr = condition.getJSONArray("region");
        String stationType = condition.getString("stationType");
        String fsuManufactory = condition.getString("fsuManufactory");


        //根据用户id的区域权限及其搜索条件获取站点列表筛选
        JSONArray stationList = condition.getJSONArray("stationList");

        String period = condition.getString("period");
        JSONObject timePeriod = condition.getJSONObject("timePeriod");

        List<JSONObject> stationBreakResult = stationBreakTempDao.getStationBreakByCondition(reportTask.getId(), regionArr.toJavaList(JSONObject.class), period, timePeriod);
        String[][][] resultData = stationBreakDataCreate(stationBreakResult);
        //统计告警量
        String resultType = reportConfig.getDataType();
        if (!DataType.FILE.equals(resultType)) {
            ReportData reportData = new ReportData(DataType.FORM, JSONObject.toJSONString(resultData));
            return reportData;
        }
        JSONObject currentUser = condition.getJSONObject("currentUser");
        if (currentUser == null) currentUser = new JSONObject();
        int length = resultData[0].length;
        resultData[0][length - 4] = new String[]{""};
        resultData[0][length - 3] = new String[]{"统计周期:" + period};
        resultData[0][length - 2] = new String[]{"时间段:" + timePeriod.getString("startTime") + "-" + timePeriod.getString("endTime")};
        resultData[0][length - 1] = new String[]{"操作人员:" + currentUser.getString("name")};
        String excelUri = stationOffDetailsExcelCreate("站点停电明细表", resultData);
        ReportData reportData = new ReportData(DataType.FORM, excelUri);
        return reportData;
    }

    private String[][][] stationBreakDataCreate(List<JSONObject> stationBreakResult) {
        String[] tableHead = null;
        tableHead = new String[]{"区域层级", "站点总数", "交维站点数", "日平均断站时长(分钟)", "月累计平均断站时长(分钟)"};

        int colLength = tableHead.length; // 列
        int rowLength = stationBreakResult.size() + 1; //行
        String[][] sheetData = new String[colLength][rowLength];

        for (int i = 0; i < rowLength; i++) {
            String[] row = sheetData[i];
            if (i == 0) {
                sheetData[i] = tableHead;
                continue;
            }
            JSONObject jsonObject = stationBreakResult.get(i - 1);
            row[0] = jsonObject.getString("");
            int a = 0;
            row[1 + a] = jsonObject.getString("");
            row[2 + a] = jsonObject.getString("");
            row[3 + a] = jsonObject.getString("");
            row[4 + a] = jsonObject.getString("");
        }
        return new String[][][]{sheetData};
    }

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
        //获取时间信息
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);

        if (month == 1) {
            month = 12;
            year -= 1;
        } else month -= 1;

        JSONObject stationCondition = new JSONObject();
        stationCondition.put("serverCode", serverCode);
        stationCondition.put("enterpriseCode", enterpriseCode);
        // 获取企业在该云平台下所有站点
        MsgResult stationListResp = mqttOpera.opera("getStationListByEnterpriseCode", stationCondition.toJSONString());
        String stationListMsg = stationListResp.getMsg();
        JSONObject countCondition = new JSONObject();
        countCondition.put("siteList", stationListMsg);
        MsgResult getStationElectricCountList = mqttOpera.opera("getStationElectricCountList", countCondition.toJSONString());
        int stateCode = getStationElectricCountList.getStateCode();
        List<ElectricCountTemp> electricCountTemps = new ArrayList<>();
        if (stateCode == StateCode.SUCCESS) {
            String msg = getStationElectricCountList.getMsg();
            JSONArray countResultList = JSONArray.parseArray(msg);
            int finalYear = year;
            int finalMonth = month;
            countResultList.forEach(e -> {
                ElectricCountTemp electricCountTemp = new ElectricCountTemp();
                electricCountTemp.setYear(finalYear);
                electricCountTemp.setMonth(finalMonth);
            });
        }
        electricCountTempDao.save(electricCountTemps, reportTask.getId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("executorTime", year + "-" + month);
        jsonObject.put("executorResult", electricCountTemps);
        return new ReportData(DataType.TEXT, jsonObject.toJSONString());
    }

    private ReportData electricCountQuery(ReportConfig reportConfig, ReportTask reportTask) {
        JSONObject condition = reportConfig.getCondition();//获取查询条件

        JSONArray regionArr = condition.getJSONArray("region");
        String stationType = condition.getString("stationType");
        String fsuManufactory = condition.getString("fsuManufactory");


        //根据用户id的区域权限及其搜索条件获取站点列表筛选
        JSONArray stationList = condition.getJSONArray("stationList");

        String period = condition.getString("period");
        JSONObject timePeriod = condition.getJSONObject("timePeriod");


        List<JSONObject> electricCountResult = electricCountTempDao.getElectricCountByCondition(reportTask.getId(), regionArr.toJavaList(JSONObject.class), period, timePeriod);
        String[][][] resultData = electricCountDataCreate(electricCountResult);
        //统计告警量
        String resultType = reportConfig.getDataType();
        if (!DataType.FILE.equals(resultType)) {
            ReportData reportData = new ReportData(DataType.FORM, JSONObject.toJSONString(resultData));
            return reportData;
        }
        JSONObject currentUser = condition.getJSONObject("currentUser");
        if (currentUser == null) currentUser = new JSONObject();
        int length = resultData[0].length;
        resultData[0][length - 4] = new String[]{""};
        resultData[0][length - 3] = new String[]{"统计周期:" + period};
        resultData[0][length - 2] = new String[]{"时间段:" + timePeriod.getString("startTime") + "-" + timePeriod.getString("endTime")};
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

    private String[][][] electricCountDataCreate(List<JSONObject> electricCountResult) {
        String[] tableHead1 = new String[]{"站点层级", "站点名称", "站点类型", "开始时间", "结束时间", "移动租户电量(KW·h)", "联通租户电量(KW·h)", "电信租户电量(KW·h)"};
        String[] tableHead2 = new String[]{"站点层级", "站点名称", "站点类型", "开始时间", "结束时间", "开始电量", "结束电量", "用电量", "开始电量", "结束电量", "用电量", "开始电量", "结束电量", "用电量"};

        int colLength = tableHead2.length; // 列
        int rowLength = electricCountResult.size() + 1; //行
        String[][] sheetData = new String[colLength][rowLength];

        for (int i = 0; i < rowLength; i++) {
            String[] row = sheetData[i];
            if (i == 0) {
                sheetData[i] = tableHead1;
                continue;
            }
            JSONObject jsonObject = electricCountResult.get(i - 1);
            row[0] = jsonObject.getString("");
            int a = 0;
            row[1 + a] = jsonObject.getString("");
            row[2 + a] = jsonObject.getString("");
            row[3 + a] = jsonObject.getString("");
            row[4 + a] = jsonObject.getString("");
            row[5 + a] = jsonObject.getString("");
            row[6 + a] = jsonObject.getString("");
            row[7 + a] = jsonObject.getString("");
            row[8 + a] = jsonObject.getString("");
            row[9 + a] = jsonObject.getString("");
            row[10 + a] = jsonObject.getString("");
            row[11 + a] = jsonObject.getString("");
            row[12 + a] = jsonObject.getString("");
            row[13 + a] = jsonObject.getString("");
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
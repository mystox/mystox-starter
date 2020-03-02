package com.kongtrolink.framework.reports.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.entity.OperaCodePrefix;
import com.kongtrolink.framework.reports.api.FsuReportsService;
import com.kongtrolink.framework.reports.dao.FsuOfflineDetailsTempDao;
import com.kongtrolink.framework.reports.dao.ReportTaskDao;
import com.kongtrolink.framework.reports.entity.*;
import com.kongtrolink.framework.reports.entity.alarmCount.AlarmCountTemp;
import com.kongtrolink.framework.reports.entity.fsuOfflineDetails.FsuOfflineDetailsTemp;
import com.kongtrolink.framework.reports.stereotype.ReportExtend;
import com.kongtrolink.framework.reports.stereotype.ReportOperaCode;
import com.kongtrolink.framework.reports.utils.WorkbookUtil;
import com.kongtrolink.framework.service.MqttOpera;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * \* @Author: mystox
 * \* Date: 2020/2/27 14:44
 * \* Description:
 * \
 */
@Service
public class FsuReportsServiceImpl implements FsuReportsService {
    @Autowired
    MqttOpera mqttOpera;
    @Autowired
    ReportTaskDao reportTaskDao;
    @Autowired
    FsuOfflineDetailsTempDao fsuOfflineDetailsTempDao;

    @Override
    @ReportOperaCode(code = OperaCodePrefix.REPORTS + "fsuRunning", rhythm = 20, resultType = {DataType.JSON}, extend = {
            @ReportExtend(field = "month", name = "月份", type = ReportExtend.FieldType.STRING), //时间类型是否需要
            @ReportExtend(field = "region", name = "区域层级", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, value = "/proxy_ap/region/getCurrentRegion"), //区域层级
            @ReportExtend(field = "stationList", name = "区域层级(站点级)", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, value = "/region/getStationList", hide = true), //站点列表
            @ReportExtend(field = "currentUser", name = "当前用户", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, value = "/proxy_ap/commonFunc/getUserInfo", hide = true), //当前用户信息
            @ReportExtend(field = "stationType", name = "站点类型", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"全部", "A级机房", "B级机房", "C级机房", "D级机房"}),
            @ReportExtend(field = "fsuManufactory", name = "fsu厂家", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"全部", "义益钛迪"}),
            @ReportExtend(field = "runningSate", name = "运行状态", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"全部", "交维态", "工程态", "测试态"}),
            @ReportExtend(field = "period", name = "统计周期", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"月报表", "季报表", "年报表"}),
            @ReportExtend(field = "timePeriod", name = "时间段", type = ReportExtend.FieldType.DATE, belong = ExecutorType.query),
    })
    public ReportData fsuOffLine(String reportConfigStr) {
        ReportConfig reportConfig = JSONObject.parseObject(reportConfigStr, ReportConfig.class);
        ExecutorType executorType = reportConfig.getExecutorType();
        if (ExecutorType.query.equals(executorType))
            return fsuOffLineQuery(reportConfig);
        else return fsuOffLineExecutor(reportConfig);
    }

    private ReportData fsuOffLineExecutor(ReportConfig reportConfig) {
        ReportTask reportTask = reportTaskDao.findByByUniqueCondition(
                reportConfig.getServerCode(), reportConfig.getEnterpriseCode(), reportConfig.getOperaCode(), reportConfig.getReportServerCode());
        if (reportTask == null)
            return null;
        JSONObject condition = reportConfig.getCondition();
        String serverCode = reportConfig.getServerCode();
        String enterpriseCode = reportConfig.getEnterpriseCode();
        List<AlarmCountTemp> alarmCountTempList = new ArrayList<AlarmCountTemp>();

        //获取时间信息
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);

        if (month == 1) {
            month = 12;
            year -= 1;
        } else month -= 1;
        return null;
    }

    private ReportData fsuOffLineQuery(ReportConfig reportConfig) {
        JSONObject condition = reportConfig.getCondition();//获取查询条件
        String statisticLevel = condition.getString("statisticLevel");
        //todo 根据条件获取层级下的站点列表
        MsgResult getStationList = mqttOpera.opera("getStationListInSCloud", condition.toJSONString());
        String siteListMsg = getStationList.getMsg();
        List<JSONObject> siteList = JSONArray.parseArray(siteListMsg, JSONObject.class);
        //todo  根据站点分别获取分类获取fsu设备列表
        String siteFsuResultStr = mqttOpera.opera("getFsuListBySiteList", JSONArray.toJSONString(siteList)).getMsg();
        List<JSONObject> siteFsuResult = JSONArray.parseArray(siteFsuResultStr, JSONObject.class);

        //todo 根据fsu 获取fsu离线告警结果
        //

        String[][][] resultData = fsuOfflineDataCreate(siteFsuResult, statisticLevel);
        int length = resultData[1].length;
        resultData[0][length - 4] = new String[]{""};
        resultData[0][length - 3] = new String[]{"统计周期:"};
        resultData[0][length - 2] = new String[]{"时间段:"};
        resultData[0][length - 1] = new String[]{"操作人员:"};
        String excelUri = fsuOfflineExcelCreate("FSU离线统计表-" + statisticLevel, resultData);
        ReportData reportData = new ReportData(DataType.FORM, excelUri);
        return reportData;
    }

    private String[][][] fsuOfflineDataCreate(List<JSONObject> siteFsuResult, String statisticLevel) {

        String[] tableHead = null;
        if (StringUtils.equalsAny(statisticLevel, "省级", "市级", "区县级")) {
            tableHead = new String[]{"区域层级", "站点总数", "离线站点数", "累计离线时长（分钟）", "离线次数", "平均离线时长（分钟）"};
        } else {
            tableHead = new String[]{"区域层级", "站点名称", "累计离线时长（分钟）", "离线次数", "平均离线时长（分钟）"};
        }
        int colLength = tableHead.length; // 行
        int rowLength = siteFsuResult.size() + 1; //列
        String[][] sheetData = new String[colLength + 4][rowLength];

        for (int i = 0; i < rowLength; i++) {
            String[] row = sheetData[i];
            if (i == 0) sheetData[i] = tableHead;
            JSONObject jsonObject = siteFsuResult.get(i - 1);
            row[0] = jsonObject.getString("");
            int a = 0;
            if (StringUtils.equalsAny(statisticLevel, "省级", "市级", "区县级")) {
                row[1 + a] = jsonObject.getString("");//站点总数
                row[2 + a] = jsonObject.getString(""); //离线站点数
                a += 1;
            } else {
                row[1 + a] = jsonObject.getString("");//站点名称
            }
//            row[1 + a] = jsonObject.getString("");
            row[2 + a] = jsonObject.getString("");
            row[3 + a] = jsonObject.getString("");
            row[4 + a] = jsonObject.getString("");
            row[5 + a] = jsonObject.getString("");
            row[6 + a] = jsonObject.getString("");
            row[7 + a] = jsonObject.getString("");
            row[8 + a] = jsonObject.getString("");
        }
        return new String[][][]{sheetData};
    }

    private String fsuOfflineExcelCreate(String s, String[][][] resultData) {
        return null;
    }


    @Override
    @ReportOperaCode(code = OperaCodePrefix.REPORTS + "fsuRunning", rhythm = 20, resultType = {DataType.JSON}, extend = {
            @ReportExtend(field = "month", name = "月份", type = ReportExtend.FieldType.STRING), //时间类型是否需要
            @ReportExtend(field = "region", name = "区域层级", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, value = "/proxy_ap/region/getCurrentRegion"), //区域层级
            @ReportExtend(field = "stationList", name = "区域层级(站点级)", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, value = "/region/getStationList", hide = true), //站点列表
            @ReportExtend(field = "currentUser", name = "当前用户", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, value = "/proxy_ap/commonFunc/getUserInfo", hide = true), //当前用户信息
            @ReportExtend(field = "stationType", name = "站点类型", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"全部", "A级机房", "B级机房", "C级机房", "D级机房"}),
            @ReportExtend(field = "fsuManufactory", name = "fsu厂家", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"全部", "义益钛迪"}),
            @ReportExtend(field = "runningSate", name = "运行状态", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"全部", "交维态", "工程态", "测试态"}),
    })
    public ReportData fsuRunning(String reportConfigStr) {
        ReportConfig reportConfig = JSONObject.parseObject(reportConfigStr, ReportConfig.class);
        ExecutorType executorType = reportConfig.getExecutorType();
        if (ExecutorType.query.equals(executorType))
            return fsuRunningQuery(reportConfig);
        else return fsuRunningExecutor(reportConfig);
    }

    private ReportData fsuRunningExecutor(ReportConfig reportConfig) {
        return null;
    }

    /**
     * @return com.kongtrolink.framework.reports.entity.ReportData
     * @Date 14:42 2020/2/28
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description //实时业务的数据即可
     **/
    private ReportData fsuRunningQuery(ReportConfig reportConfig) {

        JSONObject condition = reportConfig.getCondition();
        //todo 通过接口获取
        String getFsuRunningStatistic = mqttOpera.opera("getFsuRunningStatistic", condition.toJSONString()).getMsg();
        List<JSONObject> jsonObjects = JSONArray.parseArray(getFsuRunningStatistic, JSONObject.class);

        String statisticLevel = condition.getString("statisticLevel");
        String[][][] resultData = fsuRunningDataCreate(jsonObjects, statisticLevel);
        int length = resultData[1].length;
        resultData[0][length] = new String[]{""};
        resultData[0][length] = new String[]{"操作人员:"};
        String excelUri = fsuRunningExcelCreate("FSU离线统计表-" + statisticLevel, resultData);
        ReportData reportData = new ReportData(DataType.FORM, excelUri);
        return reportData;
    }

    private String[][][] fsuRunningDataCreate(List<JSONObject> runingList, String statisticLevel) {
        String[] tableHead = null;
        tableHead = new String[]{"区域层级", "入网总量", "在线量", "离线量", "在线率", "离线率"};
        int colLength = tableHead.length; // 行
        int rowLength = runingList.size() + 1; //列
        String[][] sheetData = new String[colLength + 4][rowLength];

        for (int i = 0; i < rowLength; i++) {
            String[] row = sheetData[i];
            if (i == 0) sheetData[i] = tableHead;
            JSONObject jsonObject = runingList.get(i - 1);
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

    private String fsuRunningExcelCreate(String sheetName, String[][][] resultData) {
        long currentTime = System.currentTimeMillis();
        String path = "/reportsResources/report_fsuRunning";
        String filename = "FSU运行统计表_" + currentTime + ".xls";
        WorkbookUtil.save("." + path, filename, WorkbookUtil.createWorkBook(new String[]{sheetName}, resultData));
        return path + "/" + filename;

    }


    @Override
    @ReportOperaCode(code = OperaCodePrefix.REPORTS + "fsuOffLineDetails", rhythm = 20, resultType = {DataType.JSON}, extend = {
            @ReportExtend(field = "month", name = "月份", type = ReportExtend.FieldType.STRING), //时间类型是否需要
            @ReportExtend(field = "region", name = "区域层级", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, value = "/proxy_ap/region/getCurrentRegion"), //区域层级
            @ReportExtend(field = "stationList", name = "区域层级(站点级)", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, value = "/region/getStationList", hide = true), //站点列表
            @ReportExtend(field = "currentUser", name = "当前用户", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, value = "/proxy_ap/commonFunc/getUserInfo", hide = true), //当前用户信息
            @ReportExtend(field = "fsuManufactory", name = "fsu厂家", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"全部", "义益钛迪"}),
            @ReportExtend(field = "stationType", name = "站点类型", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"全部", "A级机房", "B级机房", "C级机房", "D级机房"}),
            @ReportExtend(field = "runningSate", name = "运行状态", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"全部", "交维态", "工程态", "测试态"}),
            @ReportExtend(field = "period", name = "统计周期", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"月报表", "季报表", "年报表"}),
            @ReportExtend(field = "timePeriod", name = "时间段", type = ReportExtend.FieldType.DATE, belong = ExecutorType.query),
    })
    public ReportData fsuOffLineDetails(String reportConfigStr) {
        ReportConfig reportConfig = JSONObject.parseObject(reportConfigStr, ReportConfig.class);
        ExecutorType executorType = reportConfig.getExecutorType();
        if (ExecutorType.query.equals(executorType))
            return fsuOffLineDetailsQuery(reportConfig);
        else return fsuOffLineDetailsExecutor(reportConfig);
    }

    private ReportData fsuOffLineDetailsExecutor(ReportConfig reportConfig) {
        ReportTask reportTask = reportTaskDao.findByByUniqueCondition(
                reportConfig.getServerCode(), reportConfig.getEnterpriseCode(), reportConfig.getOperaCode(), reportConfig.getReportServerCode());
        if (reportTask == null)
            return null;

        JSONObject condition = reportConfig.getCondition();
        String serverCode = reportConfig.getServerCode();
        String enterpriseCode = reportConfig.getEnterpriseCode();
        List<AlarmCountTemp> alarmCountTempList = new ArrayList<AlarmCountTemp>();

        //获取时间信息
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);

        if (month == 1) {
            month = 12;
            year -= 1;
        } else month -= 1;
        //todo 获取企业在该云平台下所有站点

        JSONObject msg = new JSONObject();
        msg.put("serverCode", serverCode);
        msg.put("enterpriseCode", enterpriseCode);
        MsgResult siteListResult = mqttOpera.opera("getStationListByEnterpriseCodeInSCloud", msg.toJSONString());
        String siteListMsg = siteListResult.getMsg();
        List<JSONObject> siteList = JSONArray.parseArray(siteListMsg, JSONObject.class);
        List<FsuOfflineDetailsTemp> fsuOfflineDetailsTemps = new ArrayList<>();
        if (!CollectionUtils.isEmpty(siteList)) {
            int finalMonth = month;
            int finalYear = year;
            siteList.forEach(s -> {
                //todo 获取fsu列表
                String stationId = s.getString("siteId");
                MsgResult getFsuListByStationId = mqttOpera.opera("getFsuListByStationId", stationId);
                //todo 获取fsu
                String fsuListMsg = getFsuListByStationId.getMsg();
                List<JSONObject> fsuList = JSONArray.parseArray(fsuListMsg, JSONObject.class);
                fsuList.forEach(f -> {
                    String fsuId = f.getString("id");
                    MsgResult fsuOfflineDetailResult = mqttOpera.opera("getFsuOfflineDetailResultBySiteId", fsuId);
                    String fsuOfflineDetailResultMsg = fsuOfflineDetailResult.getMsg();
                    List<JSONObject> fsuOfflineDetailsResultList = JSONArray.parseArray(fsuOfflineDetailResultMsg, JSONObject.class);
                    fsuOfflineDetailsResultList.forEach(fr -> {
                        FsuOfflineDetailsTemp fsuOfflineDetailsTemp = new FsuOfflineDetailsTemp();
                        fsuOfflineDetailsTemps.add(fsuOfflineDetailsTemp);
                    });
                });
            });
        }
        fsuOfflineDetailsTempDao.save(fsuOfflineDetailsTemps, reportTask.getId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("executorTime", year + "-" + month);
        jsonObject.put("executorResult", alarmCountTempList);
        return new ReportData(DataType.TEXT, jsonObject.toJSONString());
    }


    private ReportData fsuOffLineDetailsQuery(ReportConfig reportConfig) {
        JSONObject condition = reportConfig.getCondition();//获取查询条件
        String statisticLevel = condition.getString("statisticLevel");
        //todo 根据条件获取层级下的站点列表
        MsgResult getStationList = mqttOpera.opera("getStationListInSCloud", condition.toJSONString());
        String siteListMsg = getStationList.getMsg();
        List<JSONObject> siteList = JSONArray.parseArray(siteListMsg, JSONObject.class);
        //todo  根据站点分别获取分类获取fsu设备列表
        String siteFsuResultStr = mqttOpera.opera("getFsuListBySiteList", JSONArray.toJSONString(siteList)).getMsg();
        List<JSONObject> siteFsuResult = JSONArray.parseArray(siteFsuResultStr, JSONObject.class);
        //统计告警量
        String[][][] resultData = fsuOfflineDetailsDataCreate(siteFsuResult, statisticLevel);
        int length = resultData[1].length;
        resultData[0][length - 4] = new String[]{""};
        resultData[0][length - 3] = new String[]{"统计周期:"};
        resultData[0][length - 2] = new String[]{"时间段:"};
        resultData[0][length - 1] = new String[]{"操作人员:"};
        String excelUri = fsuOfflineExcelCreate("FSU离线统计表-" + statisticLevel, resultData);
        ReportData reportData = new ReportData(DataType.FORM, excelUri);
        return reportData;
    }

    private String[][][] fsuOfflineDetailsDataCreate(List<JSONObject> fsuOfflineDetailsResult, String statisticLevel) {

        String[] tableHead = new String[]{"站点层级", "站点名称", "站点编号", "站点类型", "FSU状态", "开始时间", "结束时间", "时长（分钟）"};
        int colLength = tableHead.length; //行
        int rowLength = fsuOfflineDetailsResult.size() + 1; //列
        String[][] sheetData = new String[colLength + 4][rowLength];
        for (int i = 0; i < rowLength; i++) {
            String[] row = sheetData[i];
            if (i == 0) sheetData[i] = tableHead;
            JSONObject jsonObject = fsuOfflineDetailsResult.get(i - 1);
            row[0] = jsonObject.getString("");
            int a = 0;
            row[1 + a] = jsonObject.getString("");
            row[2 + a] = jsonObject.getString("");
            row[3 + a] = jsonObject.getString("");
            row[4 + a] = jsonObject.getString("");
            row[5 + a] = jsonObject.getString("");
            row[6 + a] = jsonObject.getString("");
            row[7 + a] = jsonObject.getString("");
        }
        return new String[][][]{sheetData};
    }
}
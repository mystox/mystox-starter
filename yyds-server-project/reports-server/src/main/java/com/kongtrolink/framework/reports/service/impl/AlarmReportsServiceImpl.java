package com.kongtrolink.framework.reports.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.entity.OperaCodePrefix;
import com.kongtrolink.framework.entity.StateCode;
import com.kongtrolink.framework.reports.api.AlarmReportsService;
import com.kongtrolink.framework.reports.dao.AlarmCategoryTempDao;
import com.kongtrolink.framework.reports.dao.AlarmCountTempDao;
import com.kongtrolink.framework.reports.dao.ReportTaskDao;
import com.kongtrolink.framework.reports.entity.*;
import com.kongtrolink.framework.reports.entity.alarmCategory.AlarmCategoryTemp;
import com.kongtrolink.framework.reports.entity.alarmCount.AlarmCountTemp;
import com.kongtrolink.framework.reports.stereotype.ReportExtend;
import com.kongtrolink.framework.reports.stereotype.ReportOperaCode;
import com.kongtrolink.framework.reports.utils.DateUtil;
import com.kongtrolink.framework.reports.utils.WorkbookUtil;
import com.kongtrolink.framework.service.MqttOpera;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * \* @Author: mystox
 * \* Date: 2020/2/13 13:32
 * \* Description:
 * \
 */
@Service
public class AlarmReportsServiceImpl implements AlarmReportsService {

    @Autowired
    MqttOpera mqttOpera;

    @Autowired
    AlarmCountTempDao alarmCountTempDao;

    @Autowired
    AlarmCategoryTempDao alarmCategoryTempDao;
    @Autowired
    ReportTaskDao reportTaskDao;

    @Override
    @ReportOperaCode(code = OperaCodePrefix.REPORTS + "alarmCount", rhythm = 3600*24*30, dataType = {DataType.TABLE, DataType.FILE}, extend = {
            @ReportExtend(field = "month", name = "月份", type = ReportExtend.FieldType.STRING), //时间类型是否需要
            @ReportExtend(field = "region", name = "区域层级", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, uri = "/proxy_ap/region/getCurrentRegion"), //区域层级
            @ReportExtend(field = "stationList", name = "区域层级(站点级)", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, uri = "/region/getStationList", hide = true), //站点列表
            @ReportExtend(field = "currentUser", name = "当前用户", type = ReportExtend.FieldType.JSON, belong = ExecutorType.query, uri = "/proxy_ap/commonFunc/getUserInfo", hide = true), //当前用户信息
            @ReportExtend(field = "stationType", name = "站点类型", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "A级机房", "B级机房", "C级机房", "D级机房"}),
            @ReportExtend(field = "runningSate", name = "运行状态", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "交维态", "工程态", "测试态"}),
            @ReportExtend(field = "alarmStatus", name = "告警状态", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"历史告警"}),
            @ReportExtend(field = "alarmLevel", name = "告警等级", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query,uri = "/reportsOpera/getAlarmLevel"),
            @ReportExtend(field = "period", name = "统计周期", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"月报表", "季报表", "年报表"}),
            @ReportExtend(field = "timePeriod", name = "时间段", type = ReportExtend.FieldType.DATE, belong = ExecutorType.query),
    })
    public ReportData alarmCount(String reportConfigStr) {
        ReportConfig reportConfig = JSONObject.parseObject(reportConfigStr, ReportConfig.class);
        ExecutorType executorType = reportConfig.getExecutorType();
        if (ExecutorType.query.equals(executorType))
            return AlarmCountQuery(reportConfig);
        else return alarmCountExecutor(reportConfig);
    }

    /**
     * @return com.kongtrolink.framework.reports.entity.ReportData
     * @Date 17:29 2020/2/23
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description 告警量统计表的执行逻辑
     * 1.每月执行一次单月告警量统计数据，并检查最初告警时间做数据检查与填充，并支持指定月份的数据更新
     * 2.告警量统计数据以 {月份编号|区域层级|站点名称|站点类型|告警等级|告警状态|告警总数|告警恢复数} 为基本数据结构按站点为基本数据单元进行数据的归纳存储
     * 3.告警量统计表名以 t_report_execute_temp_${taskId}为基本格式
     * 4.根据搜索条件获取站点列表，进行报表数据查询
     * 5.统计范围为serverCode_businessCode 下所有的站点
     **/
    private ReportData alarmCountExecutor(ReportConfig reportConfig) {
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


        JSONObject stationCondition = new JSONObject();
        stationCondition.put("serverCode", serverCode);
        stationCondition.put("enterpriseCode", enterpriseCode);
        //todo 获取企业在该云平台下所有站点
        MsgResult siteListResult = mqttOpera.opera("getStationListByEnterpriseCode", stationCondition.toJSONString());
        String siteListMsg = siteListResult.getMsg();
        List<JSONObject> siteList = JSONArray.parseArray(siteListMsg, JSONObject.class);
        if (!CollectionUtils.isEmpty(siteList)) {
            int finalMonth = month;
            int finalYear = year;
            siteList.forEach(s -> {
                //todo 获取站点下所有fsu及其相关告警数据

                String stationId = s.getString("siteId");
                JSONObject fsuCondition = new JSONObject();
                fsuCondition.put("stationId", stationId);
                fsuCondition.put("enterpriseCode", enterpriseCode);
                MsgResult getFsuListByStationId = mqttOpera.opera("getFsuListByStationId", fsuCondition.toJSONString());
                String fsuListMsg = getFsuListByStationId.getMsg();
                List<JSONObject> fsuList = JSONArray.parseArray(fsuListMsg, JSONObject.class);
                fsuList.forEach(f -> {
                    //todo 获取设备id
                    String fsuId = "";
                    MsgResult getDeviceListByFsuId = mqttOpera.opera("getDeviceListByFsuId", fsuId);
                    List<String> deviceList = new ArrayList<>();
                    //todo 添加fsuId
                    deviceList.add("fsuDeviceId");
                    //todo 获取上月告警统计信息 ,包括多项告警统计信息 根据等级统计上个月内的所有历史告警数量和告警恢复数量
                    MsgResult getAlarmCountByDeviceIdList = mqttOpera.opera("getAlarmCountByDeviceIdListLastMonth", JSONArray.toJSONString(deviceList));
                    String alarmCountListMsg = getAlarmCountByDeviceIdList.getMsg();
                    List<JSONObject> jsonObjects = JSONArray.parseArray(alarmCountListMsg, JSONObject.class);
                    jsonObjects.forEach(c -> {
                        AlarmCountTemp alarmCountTemp = new AlarmCountTemp();
                        alarmCountTemp.setYear(finalYear);
                        alarmCountTemp.setMonth(finalMonth);
                        //todo 填充站点告警统计信息
                        alarmCountTempList.add(alarmCountTemp);
                    });
                });
            });
        }
        alarmCountTempDao.save(alarmCountTempList, reportTask.getId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("executorTime", year + "-" + month);
        jsonObject.put("executorResult", alarmCountTempList);
        return new ReportData(DataType.TEXT, jsonObject.toJSONString());
    }

    /**
     * @return com.kongtrolink.framework.reports.entity.ReportData
     * @Date 18:38 2020/2/23
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description //报表的查询根据执行的temp表
     **/
    private ReportData AlarmCountQuery(ReportConfig reportConfig) {
        ReportTask reportTask = reportTaskDao.findByByUniqueCondition(
                reportConfig.getServerCode(), reportConfig.getEnterpriseCode(), reportConfig.getOperaCode(), reportConfig.getReportServerCode());
        if (reportTask == null)
            return null;

        String taskId = reportTask.getId();
        JSONObject condition = reportConfig.getCondition();//获取查询条件

        JSONArray regionArr = condition.getJSONArray("region");
        String stationType = condition.getString("stationType");
        String runningSate = condition.getString("runningSate");
        String fsuManufactory = condition.getString("fsuManufactory");
        String statisticLevel = condition.getString("statisticLevel");
        //根据用户id的区域权限及其搜索条件获取站点列表筛选
        JSONArray stationList = condition.getJSONArray("stationList");
        JSONObject currentUser = condition.getJSONObject("currentUser");
        if (currentUser == null) currentUser = new JSONObject();
        JSONObject msg = new JSONObject();
        List<JSONObject> alarmCountList = new ArrayList<>();
        String alarmLevel = condition.getString("alarmLevel");
        String period = condition.getString("period");
        JSONObject timePeriod = condition.getJSONObject("timePeriod");
        Calendar instance = Calendar.getInstance();
        if (timePeriod == null) {
            timePeriod = new JSONObject();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            timePeriod.put("endTime", format.format(new Date(System.currentTimeMillis())));
            timePeriod.put("startTime", format.format(DateUtil.getInstance().getFirstDayOfMonth()));
        }
        MsgResult getStationList = mqttOpera.opera("getStationListInSCloud", condition.toJSONString());
        if (getStationList.getStateCode() == StateCode.SUCCESS) {
            String siteListMsg = getStationList.getMsg();
            List<JSONObject> siteList = JSONArray.parseArray(siteListMsg, JSONObject.class);

            //todo 根据查询条件极其内容生成结果文件
            alarmCountList = alarmCountTempDao.getCountDataByCondition(taskId, siteList, alarmLevel, statisticLevel, period, timePeriod);
            //todo 获取本月告警分类数据
        }
        //统计告警量
        alarmCountList.add(new JSONObject());
        String[][][] resultData = AlarmCountDataCreate(alarmCountList, statisticLevel);
        String resultType = reportConfig.getDataType();
        if (!DataType.FILE.equals(resultType)) {
            JsonData jsonData = new JsonData();
            jsonData.setName("告警量统计表");
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
        resultData[0][length - 2] = new String[]{"时间段:" + timePeriod.getString("startTime") + "-" + timePeriod.getString("endTime")};
        resultData[0][length - 1] = new String[]{"操作人员:" + currentUser.getString("name")};
        String excelUri = AlarmCountExcelCreate("告警量统计表-" + statisticLevel, resultData);
        ReportData reportData = new ReportData(DataType.TABLE, excelUri);
        return reportData;
    }


    private String[][][] AlarmCountDataCreate(List<JSONObject> alarmCountList, String statisticLevel) {
        String[] tableHead = null;
        if (StringUtils.equalsAny(statisticLevel, "省级", "市级", "区县级")) {
            tableHead = new String[]{"区域层级", "告警总数", "告警恢复数"};
        } else
            tableHead = new String[]{"区域层级", "站点名称", "站点类型", "告警总数", "告警恢复数"};

        int colLength = tableHead.length; // 列
        int rowLength = alarmCountList.size() + 1; //行数
        String[][] sheetData = new String[rowLength+4][colLength];

        for (int i = 0; i < rowLength; i++) {
            String[] row = sheetData[i];
            if (i == 0) {
                sheetData[i] = tableHead;
                continue;
            }
            JSONObject jsonObject = alarmCountList.get(i - 1);
            row[0] = jsonObject.getString("");
            int a = 0;
            if (colLength > 3) {
                row[1] = jsonObject.getString("1");
                row[2] = jsonObject.getString("");
                a = 2;
            }
            row[1 + a] = jsonObject.getString("");
            row[2 + a] = jsonObject.getString("");
            row[0] = "a";
            if (colLength > 3) {
                row[1] = "b";
                row[2] = "c";
                a = 2;
            }
            row[1 + a] = "d";
            row[2 + a] = "e";

        }
        return new String[][][]{sheetData};
    }

    private String AlarmCountExcelCreate(String sheetName, String[][][] resultData) {
        long currentTime = System.currentTimeMillis();
        String path = "/reportsResources/report_alarmCount";
        String filename = "告警量统计表_" + currentTime;
        WorkbookUtil.save("." + path, filename, WorkbookUtil.createWorkBook(new String[]{sheetName}, resultData));
        return path + "/" + filename + ".xls";
    }

    @Override
    @ReportOperaCode(code = OperaCodePrefix.REPORTS + "alarmDetails", rhythm = 20, dataType = {DataType.TABLE, DataType.FILE}, extend = {
            @ReportExtend(field = "month", name = "月份", type = ReportExtend.FieldType.STRING), //时间类型是否需要
            //@ReportExtend(field = "date", name = "时间", type = ReportExtend.FieldType.DATE), //时间类型是否需要
            @ReportExtend(field = "region", name = "区域层级", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, value = "/reportsOpera/getRegionCodeList"), //区域层级
            @ReportExtend(field = "currentUser", name = "当前用户", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, value = "/proxy_ap/commonFunc/getUserInfo", hide = true), //当前用户信息
            @ReportExtend(field = "stationType", name = "站点类型", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "A级机房", "B级机房", "C级机房", "D级机房"}),
            @ReportExtend(field = "runningSate", name = "运行状态", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "交维态", "工程态", "测试态"}),
            @ReportExtend(field = "fsuManufactory", name = "fsu厂家", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "义益钛迪"}),
            @ReportExtend(field = "alarmStatus", name = "告警状态", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"活动告警", "历史告警"}),
            @ReportExtend(field = "alarmLevel", name = "告警等级", type = ReportExtend.FieldType.URI, belong = ExecutorType.query, uri = "/reportsOpera/getAlarmLevel"),
            @ReportExtend(field = "statisticLevel", name = "统计维度", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"省级", "市级", "县级", "站点级"}),
            @ReportExtend(field = "period", name = "统计周期", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"月报表", "季报表", "年报表"}),
            @ReportExtend(field = "timePeriod", name = "时间段", type = ReportExtend.FieldType.DATE, belong = ExecutorType.query),
    })
    public ReportData alarmDetails(String reportConfigStr) {

        ReportConfig reportConfig = JSONObject.parseObject(reportConfigStr, ReportConfig.class);
        ExecutorType executorType = reportConfig.getExecutorType();
        if (ExecutorType.query.equals(executorType))
            return alarmDetailsQuery(reportConfig);
        else return alarmDetailsExecutor(reportConfig);

    }

    private ReportData alarmDetailsExecutor(ReportConfig reportConfig) {
        ReportTask reportTask = reportTaskDao.findByByUniqueCondition(
                reportConfig.getServerCode(), reportConfig.getEnterpriseCode(), reportConfig.getOperaCode(), reportConfig.getReportServerCode());
        if (reportTask == null)
            return null;
        return null;
    }

    private ReportData alarmDetailsQuery(ReportConfig reportConfig) {

        //todo 直接通过接口获取数据
        JSONObject condition = reportConfig.getCondition();//获取查询条件

        JSONArray regionArr = condition.getJSONArray("region");
        String stationType = condition.getString("stationType");
        String runningSate = condition.getString("runningSate");
        String fsuManufactory = condition.getString("fsuManufactory");

        String statisticLevel = condition.getString("statisticLevel");

        //根据用户id的区域权限及其搜索条件获取站点列表筛选
        JSONArray stationList = condition.getJSONArray("stationList");
        JSONObject msg = new JSONObject();
        MsgResult getStationList = mqttOpera.opera("getAlarmDetails", condition.toJSONString());
        String detailsMsg = getStationList.getMsg();
        List<JSONObject> detailsList = JSONArray.parseArray(detailsMsg, JSONObject.class);
        String[][][] resultData = alarmDetailsDataCreate(detailsList);
        int length = resultData[0].length;
        resultData[0][length] = new String[]{""};
        resultData[0][length] = new String[]{"统计周期:"};
        resultData[0][length] = new String[]{"时间段:"};
        resultData[0][length] = new String[]{"操作人员:"};
        String excelUri = alarmDetailsExcelCreate("告警量统计表-" + statisticLevel, resultData);
        ReportData reportData = new ReportData(DataType.TABLE, excelUri);
        return reportData;
    }

    private String alarmDetailsExcelCreate(String sheetName, String[][][] resultData) {
        long currentTime = System.currentTimeMillis();
        String path = "/reportsResources/report_alarmDetails";
        String filename = "告警明细表_" + currentTime;
        WorkbookUtil.save("." + path, filename, WorkbookUtil.createWorkBook(new String[]{sheetName}, resultData));
        return path + "/" + filename + ".xls";
    }

    private String[][][] alarmDetailsDataCreate(List<JSONObject> detailsList) {
        String[] tableHead = null;
        tableHead = new String[]{"站点名称", "站点编号", "站点类型", "运行状态", "告警名称", "告警等级", "开始时间", "恢复时间", "时长（分钟）"};

        int colLength = tableHead.length; // 列
        int rowLength = detailsList.size() + 1; //行
        String[][] sheetData = new String[rowLength+4][colLength];
        for (int i = 0; i < rowLength; i++) {
            String[] row = sheetData[i];
            if (i == 0) {
                sheetData[i] = tableHead;
                continue;
            }
            JSONObject jsonObject = detailsList.get(i - 1);
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
        }
        return new String[][][]{sheetData};
    }

    @Override
    @ReportOperaCode(code = OperaCodePrefix.REPORTS + "alarmCategory", rhythm = 20, dataType = {DataType.TABLE, DataType.FILE}, extend = {
            @ReportExtend(field = "month", name = "月份", type = ReportExtend.FieldType.STRING), //时间类型是否需要
            //@ReportExtend(field = "date", name = "时间", type = ReportExtend.FieldType.DATE), //时间类型是否需要
            @ReportExtend(field = "region", name = "区域层级", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, value = "/reportsOpera/getRegionCodeList"), //区域层级
            @ReportExtend(field = "currentUser", name = "当前用户", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, value = "/proxy_ap/commonFunc/getUserInfo", hide = true), //当前用户信息
            @ReportExtend(field = "stationType", name = "站点类型", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "A级机房", "B级机房", "C级机房", "D级机房"}),
            @ReportExtend(field = "runningSate", name = "运行状态", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "交维态", "工程态", "测试态"}),
            @ReportExtend(field = "fsuManufactory", name = "fsu厂家", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "义益钛迪"}),
            @ReportExtend(field = "alarmStatus", name = "告警状态", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"活动告警", "历史告警"}),
            @ReportExtend(field = "alarmLevel", name = "告警等级", type = ReportExtend.FieldType.URI, belong = ExecutorType.query, value = "/reportsOpera/getAlarmLevel"),
            @ReportExtend(field = "statisticLevel", name = "统计维度", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"省级", "市级", "县级", "站点级"}),
            @ReportExtend(field = "period", name = "统计周期", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"月报表", "季报表", "年报表"}),
            @ReportExtend(field = "timePeriod", name = "时间段", type = ReportExtend.FieldType.DATE, belong = ExecutorType.query),
    })
    public ReportData alarmCategory(String reportConfigStr) {
        ReportConfig reportConfig = JSONObject.parseObject(reportConfigStr, ReportConfig.class);
        ExecutorType executorType = reportConfig.getExecutorType();
        if (ExecutorType.query.equals(executorType))
            return alarmCategoryQuery(reportConfig);
        else return alarmCategoryExecutor(reportConfig);

    }

    /**
     * @return com.kongtrolink.framework.reports.entity.ReportData
     * @Date 16:03 2020/2/25
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description //告警分类统计表执行逻辑
     **/
    private ReportData alarmCategoryExecutor(ReportConfig reportConfig) {
        ReportTask reportTask = reportTaskDao.findByByUniqueCondition(
                reportConfig.getServerCode(), reportConfig.getEnterpriseCode(), reportConfig.getOperaCode(), reportConfig.getReportServerCode());
        if (reportTask == null)
            return null;
        JSONObject condition = reportConfig.getCondition();
        String serverCode = reportConfig.getServerCode();
        String enterpriseCode = reportConfig.getEnterpriseCode();
        //获取时间信息
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);


        if (month == 1) {
            month = 12;
            year -= 1;
        } else month -= 1;


        JSONObject msg = new JSONObject();
        msg.put("serverCode", serverCode);
        msg.put("enterpriseCode", enterpriseCode);
        //todo 获取企业在该云平台下所有站点
        MsgResult siteListResult = mqttOpera.opera("getStationListByEnterpriseCodeInSCloud", msg.toJSONString());
        String siteListMsg = siteListResult.getMsg();
        List<JSONObject> siteList = JSONArray.parseArray(siteListMsg, JSONObject.class);
        List<AlarmCategoryTemp> alarmCategoryTempList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(siteList)) {
            int finalMonth = month;
            int finalYear = year;
            siteList.forEach(s -> {


                //todo 筛选设备列表中的设备类型组
                List<String> deviceList = new ArrayList<>();
                //todo 添加fsuId
                deviceList.add("fsuDeviceId");
                //todo 获取上月告警统计信息 ,包括多项告警统计信息 根据等级统计上个月站点内设备的告警分类统计结果
                MsgResult categoryResult = mqttOpera.opera("getAlarmCategoryByDeviceIdListLastMonth", JSONArray.toJSONString(deviceList));
                String categoryListMsg = categoryResult.getMsg();
                JSONObject categoryList = JSONObject.parseObject(categoryListMsg);
                AlarmCategoryTemp alarmCategoryTemp = new AlarmCategoryTemp();
                alarmCategoryTemp.setYear(finalYear);
                alarmCategoryTemp.setMonth(finalMonth);
                //todo 填充站点信息
                alarmCategoryTempList.add(alarmCategoryTemp);
            });
        }

        alarmCategoryTempDao.save(alarmCategoryTempList, reportTask.getId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("executorTime", year + "-" + month);
        return new ReportData(DataType.TEXT, jsonObject.toJSONString());
    }

    private ReportData alarmCategoryQuery(ReportConfig reportConfig) {
        ReportTask reportTask = reportTaskDao.findByByUniqueCondition(
                reportConfig.getServerCode(), reportConfig.getEnterpriseCode(), reportConfig.getOperaCode(), reportConfig.getReportServerCode());
        if (reportTask == null)
            return null;

        String taskId = reportTask.getId();
        JSONObject condition = reportConfig.getCondition();//获取查询条件

        JSONArray regionArr = condition.getJSONArray("region");
        String stationType = condition.getString("stationType");
        String runningSate = condition.getString("runningSate");
        String fsuManufactory = condition.getString("fsuManufactory");

        String statisticLevel = condition.getString("statisticLevel");

        //根据用户id的区域权限及其搜索条件获取站点列表筛选
        JSONArray stationList = condition.getJSONArray("stationList");
        JSONObject msg = new JSONObject();
        MsgResult getStationList = mqttOpera.opera("getStationListInSCloud", condition.toJSONString());
        String siteListMsg = getStationList.getMsg();
        List<JSONObject> siteList = JSONArray.parseArray(siteListMsg, JSONObject.class);


        //todo 根据查询条件极其内容生成结果文件
        String alarmLevel = condition.getString("alarmLevel");
        String period = condition.getString("period");
        JSONObject timePeriod = condition.getJSONObject("timePeriod");

        List<JSONObject> alarmCountList = alarmCategoryTempDao.getCategoryDataByCondition(taskId, siteList, alarmLevel, statisticLevel, period, timePeriod);
        //todo 获取本月告警分类数据

        //统计告警分类

        String[][][] resultData = alarmCategoryDataCreate(alarmCountList, statisticLevel);
        int length = resultData[0].length;
        resultData[0][length] = new String[]{""};
        resultData[0][length] = new String[]{"统计周期:"};
        resultData[0][length] = new String[]{"时间段:"};
        resultData[0][length] = new String[]{"操作人员:"};
        String excelUri = alarmCategoryExcelCreate("告警分类统计表-" + statisticLevel, resultData);
        ReportData reportData = new ReportData(DataType.TABLE, excelUri);
        return reportData;
    }

    private String[][][] alarmCategoryDataCreate(List<JSONObject> alarmCategoryList, String statisticLevel) {
        String[] tableHead = null;
        if (StringUtils.equalsAny(statisticLevel, "省级", "市级", "区县级")) {
            tableHead = new String[]{"区域层级", "告警总数", "单站平均告警", "FSU离线", "烟感", "温湿度", "开关电源", "蓄电池", "红外设备", "门磁", "水浸", "空调", "其他"};
        } else
            tableHead = new String[]{"区域层级", "站点名称", "站点类型", "生产厂家", "告警总数", "单站平均告警", "FSU离线", "烟感", "温湿度", "开关电源", "蓄电池", "红外设备", "门磁", "水浸", "空调", "其他"};

        int colLength = tableHead.length; // 列
        int rowLength = alarmCategoryList.size() + 1; //行
        String[][] sheetData = new String[rowLength+4][colLength];

        for (int i = 0; i < rowLength; i++) {
            String[] row = sheetData[i];
            if (i == 0) {
                sheetData[i] = tableHead;
                continue;
            }
            JSONObject jsonObject = alarmCategoryList.get(i - 1);
            row[0] = jsonObject.getString("");
            int a = 0;
            if (colLength > 13) {
                row[1] = jsonObject.getString("");
                row[2] = jsonObject.getString("");
                row[3] = jsonObject.getString("");
                a = 3;
            }
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

        }
        return new String[][][]{sheetData};
    }

    private String alarmCategoryExcelCreate(String sheetName, String[][][] resultData) {
        long currentTime = System.currentTimeMillis();
        String path = "/reportsResources/report_alarmCount";
        String filename = "告警分类统计表_" + currentTime + ".xls";
        WorkbookUtil.save("." + path, filename, WorkbookUtil.createWorkBook(new String[]{sheetName}, resultData));
        return path + "/" + filename;
    }
}
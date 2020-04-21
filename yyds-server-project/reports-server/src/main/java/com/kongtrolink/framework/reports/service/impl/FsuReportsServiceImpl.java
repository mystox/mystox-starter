package com.kongtrolink.framework.reports.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.utils.ReflectionUtils;
import com.kongtrolink.framework.entity.OperaCodePrefix;
import com.kongtrolink.framework.reports.api.FsuReportsService;
import com.kongtrolink.framework.reports.dao.FsuOfflineDetailsTempDao;
import com.kongtrolink.framework.reports.dao.FsuOfflineStatisticTempDao;
import com.kongtrolink.framework.reports.dao.FsuRunStateDao;
import com.kongtrolink.framework.reports.dao.ReportTaskDao;
import com.kongtrolink.framework.reports.entity.*;
import com.kongtrolink.framework.reports.entity.fsu.FsuOfflineDetailsTemp;
import com.kongtrolink.framework.reports.entity.fsu.FsuOfflineStatisticTemp;
import com.kongtrolink.framework.reports.entity.fsu.FsuRunStateTemp;
import com.kongtrolink.framework.reports.entity.query.CommonConstant;
import com.kongtrolink.framework.reports.entity.query.FsuEntity;
import com.kongtrolink.framework.reports.entity.query.FsuOperationState;
import com.kongtrolink.framework.reports.entity.query.SiteEntity;
import com.kongtrolink.framework.reports.service.MqttCommonInterface;
import com.kongtrolink.framework.reports.stereotype.ReportExtend;
import com.kongtrolink.framework.reports.stereotype.ReportOperaCode;
import com.kongtrolink.framework.reports.utils.CommonCheck;
import com.kongtrolink.framework.reports.utils.DateUtil;
import com.kongtrolink.framework.reports.utils.WorkbookUtil;
import com.kongtrolink.framework.service.MqttOpera;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * \* @Author: mystox
 * \* Date: 2020/2/27 14:44
 * \* Description:
 * \
 */
@Service
public class FsuReportsServiceImpl implements FsuReportsService {

    Logger logger = LoggerFactory.getLogger(FsuReportsServiceImpl.class);
    @Value("${server.routeMark}")
    private String routeMark;

    MqttOpera mqttOpera;
    @Autowired
    MqttCommonInterface mqttCommonInterface;
    @Autowired
    ReportTaskDao reportTaskDao;
    @Autowired
    FsuOfflineDetailsTempDao fsuOfflineDetailsTempDao;
    @Autowired
    FsuOfflineStatisticTempDao fsuOfflineStatisticTempDao;

    @Autowired
    FsuRunStateDao fsuRunStateDao;

    @Override
    @ReportOperaCode(code = OperaCodePrefix.REPORTS + "fsuOffLine", rhythm = 20, dataType = {DataType.JSON, DataType.FILE}, extend = {
            @ReportExtend(field = "stationList", name = "区域层级(站点级)", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, uri = "/reportsOpera/getStationList"), //站点列表            @ReportExtend(field = "stationList", name = "区域层级(站点级)", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, value = "/region/getStationList", hide = true), //站点列表
            @ReportExtend(field = "currentUser", name = "当前用户", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, value = "/proxy_ap/commonFunc/getUserInfo", hide = true), //当前用户信息
            @ReportExtend(field = "stationType", name = "站点类型", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "A级机房", "B级机房", "C级机房", "D级机房"}),
            @ReportExtend(field = "fsuManufactory", name = "fsu厂家", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "义益钛迪"}),
            @ReportExtend(field = "runningSate", name = "运行状态", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "交维态", "工程态", "测试态"}),
            @ReportExtend(field = "statisticLevel", name = "统计维度", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"省级", "市级", "区县级", "站点级"}),
            @ReportExtend(field = "statisticPeriod", name = "统计周期", type = ReportExtend.FieldType.STATISTIC_PERIOD, belong = ExecutorType.query, select = {"月报表", "季报表", "年报表"}, description = "{dimension:月报表,timePeriod:{startTime:yyyy-MM-dd,endTime:yyyy-MM-dd}}"),
    })
    public ReportData fsuOffLine(String reportConfigStr) {
        ReportConfig reportConfig = JSONObject.parseObject(reportConfigStr, ReportConfig.class);
        ExecutorType executorType = reportConfig.getExecutorType();
        ReportTask reportTask = reportTaskDao.findByByUniqueCondition(
                reportConfig.getServerCode(), reportConfig.getEnterpriseCode(), reportConfig.getOperaCode(), reportConfig.getReportServerCode());
        if (reportTask == null)
            return new ReportData(DataType.ERROR, "report task is not exists [" + JSONObject.toJSONString(reportTask) + "]");
        if (ExecutorType.query.equals(executorType))
            return fsuOffLineQuery(reportConfig, reportTask);
        else return fsuOffLineExecutor(reportConfig, reportTask);
    }

    /**
     * @return com.kongtrolink.framework.reports.entity.ReportData
     * @Date 20:21 2020/3/17
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description //每月月底统计当前月的fsu运行状态信息
     **/
    private ReportData fsuOffLineExecutor(ReportConfig reportConfig, ReportTask reportTask) {
        String serverCode = reportConfig.getServerCode();
        String enterpriseCode = reportConfig.getEnterpriseCode();
        List<FsuOfflineStatisticTemp> fsuOfflineStatisticTemps = new ArrayList<FsuOfflineStatisticTemp>();
        //获取时间信息

        JSONObject baseCondition = new JSONObject();
        baseCondition.put("serverCode", serverCode);
        baseCondition.put("enterpriseCode", enterpriseCode);
        Integer alarmCycle = mqttCommonInterface.getAlarmCycle(baseCondition);
        int month = CommonCheck.getMonth(alarmCycle);
        int year = CommonCheck.getYear(alarmCycle);
        // 获取企业在该云平台下所有站点
        List<SiteEntity> siteList = mqttCommonInterface.getSiteList(baseCondition);
        logger.debug("get site list is:[{}]", JSONObject.toJSONString(siteList));
        if (!CollectionUtils.isEmpty(siteList)) {
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
                if (CollectionUtils.isEmpty(fsuList)) {
                    logger.warn("get fsu list is null:[{}]", JSONObject.toJSONString(s));
                    return;
                }
                FsuEntity fsuEntity = fsuList.get(0);
                String manufacturer = fsuEntity.getManufacturer();
                //判断交维态
                String operationState = CommonCheck.fsuOperaStateCheck(fsuList);
                List<String> fsuIds = ReflectionUtils.convertElementPropertyToList(fsuList, "fsuId");
                // 获取上月告警统计信息 ,包括多项告警统计信息 根据等级统计上个月内的所有历史告警数量和告警恢复数量
                logger.debug("statistic count Alarm ByDeviceIds, site reportTaskId is [{}]", stationId);
                JSONObject jsonObjects = mqttCommonInterface.statisticFsuOfflineData(fsuIds, year, month, baseCondition);
                //填充站点告警统计信息
                FsuOfflineStatisticTemp fsuOfflineStatisticTemp = new FsuOfflineStatisticTemp();
                fsuOfflineStatisticTemp.setYear(year);
                fsuOfflineStatisticTemp.setMonth(month);
                fsuOfflineStatisticTemp.setStationId(stationId);
                fsuOfflineStatisticTemp.setStationName(stationName);
                fsuOfflineStatisticTemp.setStationType(siteType);
                fsuOfflineStatisticTemp.setOperationState(operationState);
                fsuOfflineStatisticTemp.setProvince(province);
                fsuOfflineStatisticTemp.setMunicipality(municipality);
                fsuOfflineStatisticTemp.setCounty(county);
                fsuOfflineStatisticTemp.setFsuManufactory(manufacturer);
                Double durationSum = jsonObjects.getDouble("durationSum");
                fsuOfflineStatisticTemp.setDurationSum(durationSum);
                Integer count = jsonObjects.getInteger("count");
                fsuOfflineStatisticTemp.setTimes(count);
                fsuOfflineStatisticTemp.setOfflineMark((count == null || count == 0) ? 0 : 1);
//                fsuOfflineStatisticTemp.setDurationAvg(durationSum/count);
                fsuOfflineStatisticTemps.add(fsuOfflineStatisticTemp);

            });
        } else {
            logger.error("返回站点结果为空");
        }
        String reportTaskId = reportTask.getId();
        if (!CollectionUtils.isEmpty(fsuOfflineStatisticTemps)) {
            fsuOfflineStatisticTempDao.updateDelete(year, month, reportTaskId);
            fsuOfflineStatisticTempDao.save(fsuOfflineStatisticTemps, reportTaskId);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("executorTime", year + "-" + month);
        jsonObject.put("executorResult", fsuOfflineStatisticTemps);
        logger.debug("[{}] executorResult:[{}]", reportTaskId, jsonObject);
        return new ReportData(DataType.TEXT, jsonObject.toJSONString());
    }

    private ReportData fsuOffLineQuery(ReportConfig reportConfig, ReportTask reportTask) {

        JSONObject condition = reportConfig.getCondition();//获取查询条件
        String taskId = reportTask.getId();
        String statisticLevel = condition.getString("statisticLevel");
        JSONObject currentUser = condition.getJSONObject("currentUser");
        if (currentUser == null) currentUser = new JSONObject();
        JSONObject statisticPeriod = condition.getJSONObject("statisticPeriod");
        TimePeriod timePeriod = CommonCheck.getTimePeriod(statisticPeriod);
        String period = timePeriod.getDimension();
        List<JSONObject> fsuRunStateData = fsuOfflineStatisticTempDao.getFsuOfflineData(taskId, condition, timePeriod);

        if (fsuRunStateData == null)
            fsuRunStateData = new ArrayList<>();
        String[][][] resultData = fsuOfflineDataCreate(fsuRunStateData, statisticLevel);
        String resultType = reportConfig.getDataType();
        if (!DataType.FILE.equals(resultType)) {
            JsonData jsonData = new JsonData();
            jsonData.setName("FSU离线统计表");
            List<String[]> dataArr = Arrays.asList(resultData[0]);
            dataArr = new ArrayList<>(dataArr);
            dataArr.remove(0);
            jsonData.setData(dataArr.toArray(new String[dataArr.size()][]));
            jsonData.setUnit("");
            jsonData.setxAxis(resultData[0][0]);
            ReportData reportData = new ReportData(DataType.JSON, JSONObject.toJSONString(jsonData));
            return reportData;
        }
        int length = resultData[0].length;
        resultData[0][length - 4] = new String[]{""};
        resultData[0][length - 3] = new String[]{"统计周期:" + period};
        resultData[0][length - 2] = new String[]{"时间段:" + timePeriod.getStartTimeStr() + "-" + timePeriod.getEndTimeStr()};
        resultData[0][length - 1] = new String[]{"操作人员:" + currentUser.getString("name")};
        String excelUri = fsuOfflineExcelCreate("FSU离线统计表-" + statisticLevel, resultData);
        ReportData reportData = new ReportData(DataType.TABLE, excelUri);
        return reportData;
    }

    private String[][][] fsuOfflineDataCreate(List<JSONObject> fsuOfflineDatas, String statisticLevel) {

        String[] tableHead = null;
        if (StringUtils.equalsAny(statisticLevel, "省级", "市级", "区县级")) {
            tableHead = new String[]{"区域层级", "站点总数", "离线站点数", "累计离线时长（分钟）", "离线次数", "平均离线时长（分钟）"};
        } else {
            tableHead = new String[]{"区域层级", "站点名称", "站点类型", "生产厂家", "累计离线时长（分钟）", "离线次数", "平均离线时长（分钟）"};
        }
        int colLength = tableHead.length; // 列
        int rowLength = fsuOfflineDatas.size() + 1; //行
        String[][] sheetData = new String[rowLength + 4][colLength];

        for (int i = 0; i < rowLength; i++) {
            String[] row = sheetData[i];
            if (i == 0) {
                sheetData[i] = tableHead;
                continue;
            }
            JSONObject jsonObject = fsuOfflineDatas.get(i - 1);
            row[0] = CommonCheck.aggregateTierName(jsonObject);
            int a = 0;
            if (StringUtils.equalsAny(statisticLevel, "省级", "市级", "区县级")) {
                row[1 + a] = jsonObject.getString("siteCount");//站点总数
                row[2 + a] = jsonObject.getString("offlineSiteCount"); //离线站点数
                a += 2;
            } else {
                row[1 + a] = jsonObject.getString("stationName");//站点名称
                row[2 + a] = jsonObject.getString("stationType");//站点类型
                row[3 + a] = jsonObject.getString("fsuManufactory");//厂家
                a = 3;
            }
            Double durationSum = jsonObject.getDouble("durationSum");
            Integer times = jsonObject.getInteger("times");
            row[1 + a] = String.format("%.2f", durationSum);
            row[2 + a] = String.valueOf(times);
            double avgTime = 0;
            if (times != 0) avgTime = durationSum / times;
            row[3 + a] = String.format("%.2f", avgTime);
        }
        return new String[][][]{sheetData};
    }

    private String fsuOfflineExcelCreate(String sheetName, String[][][] resultData) {
        long currentTime = System.currentTimeMillis();
        String path = "/reportsResources/report_fsuOffLine";
        String filename = "FSU离线统计表_" + currentTime;
        WorkbookUtil.save("." + path, filename, WorkbookUtil.createWorkBook(new String[]{sheetName}, resultData));
        return "/" + routeMark + path + "/" + filename + ".xls";
    }


    @Override
    @ReportOperaCode(code = OperaCodePrefix.REPORTS + "fsuRunning", rhythm = 20, dataType = {DataType.JSON, DataType.FILE}, extend = {
//            @ReportExtend(field = "month", name = "月份", type = ReportExtend.FieldType.STRING), //时间类型是否需要
//            @ReportExtend(field = "region", name = "区域层级", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, value = "/proxy_ap/region/getCurrentRegion"), //区域层级
            @ReportExtend(field = "stationList", name = "区域层级(站点级)", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, value = "/region/getStationList", hide = true), //站点列表
            @ReportExtend(field = "currentUser", name = "当前用户", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, uri = "/proxy_ap/commonFunc/getUserInfo", hide = true), //当前用户信息
            @ReportExtend(field = "stationType", name = "站点类型", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "A级机房", "B级机房", "C级机房", "D级机房"}),
            @ReportExtend(field = "fsuManufactory", name = "fsu厂家", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "义益钛迪"}),
            @ReportExtend(field = "runningSate", name = "运行状态", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"交维态"}),
            @ReportExtend(field = "statisticLevel", name = "统计维度", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"省级", "市级", "区县级"}),
//            @ReportExtend(field = "statisticPeriod", name = "统计周期", type = ReportExtend.FieldType.STATISTIC_PERIOD, belong = ExecutorType.query, select = {"月报表", "季报表", "年报表"}, description = "{dimension:月报表,timePeriod:{startTime:yyyy-MM-dd,endTime:yyyy-MM-dd}}"),

    })
    public ReportData fsuRunning(String reportConfigStr) {
        ReportConfig reportConfig = JSONObject.parseObject(reportConfigStr, ReportConfig.class);
        ExecutorType executorType = reportConfig.getExecutorType();
        ReportTask reportTask = reportTaskDao.findByByUniqueCondition(
                reportConfig.getServerCode(), reportConfig.getEnterpriseCode(), reportConfig.getOperaCode(), reportConfig.getReportServerCode());
        if (reportTask == null)
            return new ReportData(DataType.ERROR, "report task is not exists [" + JSONObject.toJSONString(reportTask) + "]");
        if (ExecutorType.query.equals(executorType))
            return fsuRunningQuery(reportConfig, reportTask);
        else return fsuRunningExecutor(reportConfig, reportTask);
    }

    private ReportData fsuRunningExecutor(ReportConfig reportConfig, ReportTask reportTask) {
        String serverCode = reportConfig.getServerCode();
        String enterpriseCode = reportConfig.getEnterpriseCode();
        JSONObject baseCondition = new JSONObject();
        baseCondition.put("serverCode", serverCode);
        baseCondition.put("enterpriseCode", enterpriseCode);
        // 获取企业在该云平台下所有站点
        List<SiteEntity> siteList = mqttCommonInterface.getSiteList(baseCondition);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        List<FsuRunStateTemp> fsuRunStateTemps = new ArrayList<>();
        if (!CollectionUtils.isEmpty(siteList)) {
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

                baseCondition.put("operationState", FsuOperationState.MAINTENANCE);
                List<FsuEntity> fsuList = mqttCommonInterface.getFsuList(stationId, baseCondition);
                if (CollectionUtils.isEmpty(fsuList)) {
                    logger.error("get fsu list is null:[{}]", JSONObject.toJSONString(s));
                    return;
                }
                String manufacturer = fsuList.get(0).getManufacturer();
                int fsuMaintenanceCount = fsuList.size(); //入网总量
                int onlineCount = 0;
                for (FsuEntity fsuEntity : fsuList) {
                    String state = fsuEntity.getState();
                    if (CommonConstant.OFFLINE.equals(state)) {
                        onlineCount += 1;
                    }
                }
                int offlineCount = fsuMaintenanceCount - onlineCount;
                FsuRunStateTemp fsuRunStateTemp = new FsuRunStateTemp();
                fsuRunStateTemp.setYear(year);
                fsuRunStateTemp.setMonth(month);
                fsuRunStateTemp.setTempDate(new Date());
                fsuRunStateTemp.setCounty(county);
                fsuRunStateTemp.setProvince(province);
                fsuRunStateTemp.setMunicipality(municipality);
                fsuRunStateTemp.setOperationState(FsuOperationState.MAINTENANCE);
                fsuRunStateTemp.setFsuManufactory(manufacturer);
                fsuRunStateTemp.setFsuMaintenanceCount(fsuMaintenanceCount);
                fsuRunStateTemp.setOnlineCount(onlineCount);
                fsuRunStateTemp.setOfflineCount(offlineCount);
                fsuRunStateTemp.setStationName(stationName);
                fsuRunStateTemp.setStationType(siteType);
                fsuRunStateTemps.add(fsuRunStateTemp);
            });
        } else {
            logger.error("返回站点结果为空");
        }

        String reportTaskId = reportTask.getId();
        if (!CollectionUtils.isEmpty(fsuRunStateTemps)) {
            fsuRunStateDao.updateDelete(year, month, reportTaskId);
            fsuRunStateDao.save(fsuRunStateTemps, reportTaskId);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("executorTime", year + "-" + month);
        jsonObject.put("executorResult", fsuRunStateTemps);
        logger.debug("[{}] executorResult:[{}]", reportTaskId, jsonObject);
        return new ReportData(DataType.TEXT, jsonObject.toJSONString());
    }

    /**
     * @return com.kongtrolink.framework.reports.entity.ReportData
     * @Date 14:42 2020/2/28
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description //实时业务的数据即可
     **/
    private ReportData fsuRunningQuery(ReportConfig reportConfig, ReportTask reportTask) {
        JSONObject condition = reportConfig.getCondition();//获取查询条件
        String taskId = reportTask.getId();
        String statisticLevel = condition.getString("statisticLevel");
        JSONObject currentUser = condition.getJSONObject("currentUser");
        if (currentUser == null) currentUser = new JSONObject();
        JSONObject statisticPeriod = condition.getJSONObject("statisticPeriod");
        TimePeriod timePeriod = CommonCheck.getTimePeriod(statisticPeriod);
        String period = timePeriod.getDimension();

        List<JSONObject> fsuRunStateData = fsuRunStateDao.getFsuRunStateData(taskId, condition, timePeriod);
        String[][][] resultData = fsuRunningDataCreate(fsuRunStateData, statisticLevel);
        String resultType = reportConfig.getDataType();
        if (!DataType.FILE.equals(resultType)) {
            JsonData jsonData = new JsonData();
            jsonData.setName("FSU运行统计表");
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
        resultData[0][length - 3] = new String[]{"统计时间:" + condition.getString("month")};
        resultData[0][length - 2] = new String[]{"操作人员:" + currentUser.getString("name")};
        String excelUri = fsuRunningExcelCreate("FSU运行统计表-" + statisticLevel, resultData);
        ReportData reportData = new ReportData(DataType.TABLE, excelUri);
        return reportData;
    }

    private String[][][] fsuRunningDataCreate(List<JSONObject> runningList, String statisticLevel) {
        String[] tableHead = null;
        tableHead = new String[]{"区域层级", "入网总量", "在线量", "离线量", "在线率", "离线率"};
        int colLength = tableHead.length; // 行
        int rowLength = runningList.size() + 1; //列
        String[][] sheetData = new String[colLength + 4][rowLength];

        for (int i = 0; i < rowLength; i++) {
            String[] row = sheetData[i];
            if (i == 0) sheetData[i] = tableHead;
            JSONObject jsonObject = runningList.get(i - 1);
            row[0] = CommonCheck.aggregateTierName(jsonObject);
            int a = 0;
            row[1 + a] = jsonObject.getString("fsuMaintenanceCount");
            row[2 + a] = jsonObject.getString("onlineCount");
            row[3 + a] = jsonObject.getString("offlineCount");
            row[4 + a] = jsonObject.getString("onlineRate");
            row[5 + a] = jsonObject.getString("offlineRate");
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
    @ReportOperaCode(code = OperaCodePrefix.REPORTS + "fsuOffLineDetails", rhythm = 20, dataType = {DataType.TABLE, DataType.FILE}, extend = {
            @ReportExtend(field = "month", name = "月份", type = ReportExtend.FieldType.STRING), //时间类型是否需要
            @ReportExtend(field = "region", name = "区域层级", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, value = "/proxy_ap/region/getCurrentRegion"), //区域层级
            @ReportExtend(field = "stationList", name = "区域层级(站点级)", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, value = "/region/getStationList", hide = true), //站点列表
            @ReportExtend(field = "currentUser", name = "当前用户", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, value = "/proxy_ap/commonFunc/getUserInfo", hide = true), //当前用户信息
            @ReportExtend(field = "fsuManufactory", name = "fsu厂家", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "义益钛迪"}),
            @ReportExtend(field = "stationType", name = "站点类型", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "A级机房", "B级机房", "C级机房", "D级机房"}),
            @ReportExtend(field = "runningSate", name = "运行状态", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "交维态", "工程态", "测试态"}),
            @ReportExtend(field = "period", name = "统计周期", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"月报表", "季报表", "年报表"}),
            @ReportExtend(field = "timePeriod", name = "时间段", type = ReportExtend.FieldType.DATE, belong = ExecutorType.query),
    })
    public ReportData fsuOffLineDetails(String reportConfigStr) {
        ReportConfig reportConfig = JSONObject.parseObject(reportConfigStr, ReportConfig.class);
        ExecutorType executorType = reportConfig.getExecutorType();
        ReportTask reportTask = reportTaskDao.findByByUniqueCondition(
                reportConfig.getServerCode(), reportConfig.getEnterpriseCode(), reportConfig.getOperaCode(), reportConfig.getReportServerCode());
        if (reportTask == null)
            return new ReportData(DataType.ERROR, "report task is not exists [" + JSONObject.toJSONString(reportTask) + "]");
        if (ExecutorType.query.equals(executorType))
            return fsuOffLineDetailsQuery(reportConfig, reportTask);
        else return fsuOffLineDetailsExecutor(reportConfig, reportTask);
    }

    private ReportData fsuOffLineDetailsExecutor(ReportConfig reportConfig, ReportTask reportTask) {
        String serverCode = reportConfig.getServerCode();
        String enterpriseCode = reportConfig.getEnterpriseCode();
        List<FsuOfflineDetailsTemp> fsuOfflineDetailsTemps = new ArrayList<FsuOfflineDetailsTemp>();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;

        /*if (month == 1) {
            month = 12;
            year -= 1;
        } else month -= 1;*/

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
                    logger.warn("get fsu list is null:[{}]", JSONObject.toJSONString(s));
                    return;
                }
                FsuEntity fsuEntity = fsuList.get(0);
                String manufacturer = fsuEntity.getManufacturer();
                String operationState = CommonCheck.fsuOperaStateCheck(fsuList);
                List<String> fsuIds = ReflectionUtils.convertElementPropertyToList(fsuList, "fsuId");
                // 获取上月告警统计信息 ,包括多项告警统计信息 根据等级统计上个月内的所有历史告警数量和告警恢复数量
                logger.debug("statistic count Alarm ByDeviceIds, site reportTaskId is [{}]", stationId);
                List<JSONObject> jsonObjects = mqttCommonInterface.getFsuOfflineDetails(fsuIds, finalYear, finalMonth, baseCondition);

                jsonObjects.forEach(entity -> {
                    //填充站点告警统计信息
                    FsuOfflineDetailsTemp fsuOfflineDetailsTemp = new FsuOfflineDetailsTemp();
                    fsuOfflineDetailsTemp.setYear(finalYear);
                    fsuOfflineDetailsTemp.setMonth(finalMonth);
                    fsuOfflineDetailsTemp.setStationId(stationId);
                    fsuOfflineDetailsTemp.setStationName(stationName);
                    fsuOfflineDetailsTemp.setStationType(siteType);
                    fsuOfflineDetailsTemp.setOperationState(operationState);
                    fsuOfflineDetailsTemp.setProvince(province);
                    fsuOfflineDetailsTemp.setMunicipality(municipality);
                    fsuOfflineDetailsTemp.setCounty(county);
                    fsuOfflineDetailsTemp.setFsuManufactory(manufacturer);
                    Date treport = entity.getDate("treport");
                    fsuOfflineDetailsTemp.setStartTime(treport);
                    Date trecover = entity.getDate("trecover");
                    fsuOfflineDetailsTemp.setRecoveryTime(trecover);
                    Double duration = (double) (trecover.getTime() - treport.getTime()) / (1000 * 60);
                    fsuOfflineDetailsTemp.setDuration(duration);//分钟
                    fsuOfflineDetailsTemps.add(fsuOfflineDetailsTemp);

                });
            });
        } else {
            logger.error("返回站点结果为空");
        }
        String reportTaskId = reportTask.getId();
        if (!CollectionUtils.isEmpty(fsuOfflineDetailsTemps)) {
            fsuOfflineDetailsTempDao.updateDelete(year, month, reportTaskId);
            fsuOfflineDetailsTempDao.save(fsuOfflineDetailsTemps, reportTaskId);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("executorTime", year + "-" + month);
        jsonObject.put("executorResult", fsuOfflineDetailsTemps);
        logger.debug("[{}] executorResult:[{}]", reportTaskId, jsonObject);
        return new ReportData(DataType.TEXT, jsonObject.toJSONString(jsonObject));
    }


    private ReportData fsuOffLineDetailsQuery(ReportConfig reportConfig, ReportTask reportTask) {
        JSONObject condition = reportConfig.getCondition();//获取查询条件
        String taskId = reportTask.getId();
        String period = condition.getString("period");
        String statisticLevel = condition.getString("statisticLevel");
        TimePeriod timePeriod = condition.getObject("timePeriod", TimePeriod.class);
        if (timePeriod == null) {
            timePeriod = new TimePeriod();
            timePeriod.setEndTime(new Date(System.currentTimeMillis()));
            timePeriod.setStartTime(DateUtil.getInstance().getFirstDayOfMonth());
        }
        //根据用户id的区域权限及其搜索条件获取站点列表筛选
        JSONObject currentUser = condition.getJSONObject("currentUser");
        if (currentUser == null) currentUser = new JSONObject();
        List<FsuOfflineDetailsTemp> fsuOfflineDetailsTemps = fsuOfflineDetailsTempDao.findFsuOfflineDetailsList(taskId, condition, timePeriod);
        //统计离线明细，生成excel数据表
        String[][][] resultData = fsuOfflineDetailsDataCreate(fsuOfflineDetailsTemps, statisticLevel);
        String resultType = reportConfig.getDataType();
        if (!DataType.FILE.equals(resultType)) {
            JsonData jsonData = new JsonData();
            jsonData.setName("FSU离线明细表");
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
        String excelUri = fsuOfflineDetailsExcelCreate("FSU离线明细表-" + statisticLevel, resultData);
        ReportData reportData = new ReportData(DataType.TABLE, excelUri);
        return reportData;
    }

    private String fsuOfflineDetailsExcelCreate(String sheetName, String[][][] resultData) {
        long currentTime = System.currentTimeMillis();
        String path = "/reportsResources/report_fsuOffLineDetails";
        String filename = "FSU离线明细表_" + currentTime + ".xls";
        WorkbookUtil.save("." + path, filename, WorkbookUtil.createWorkBook(new String[]{sheetName}, resultData));
        return path + "/" + filename;
    }

    private String[][][] fsuOfflineDetailsDataCreate(List<FsuOfflineDetailsTemp> fsuOfflineDetailsResult, String statisticLevel) {

        String[] tableHead = new String[]{"站点层级", "站点名称", "站点编号", "站点类型", "FSU状态", "开始时间", "结束时间", "时长（分钟）"};
        int colLength = tableHead.length; //行
        int rowLength = fsuOfflineDetailsResult.size() + 1; //列
        String[][] sheetData = new String[colLength + 4][rowLength];
        for (int i = 0; i < rowLength; i++) {
            String[] row = sheetData[i];
            if (i == 0) sheetData[i] = tableHead;
            FsuOfflineDetailsTemp fsuOfflineDetailsTemp = fsuOfflineDetailsResult.get(i - 1);
            row[0] = CommonCheck.aggregateTierName((JSONObject) JSONObject.toJSON(fsuOfflineDetailsTemp));
            int a = 0;
            row[1 + a] = fsuOfflineDetailsTemp.getStationName();
            row[2 + a] = fsuOfflineDetailsTemp.getStationId();
            row[3 + a] = fsuOfflineDetailsTemp.getStationType();
            row[4 + a] = fsuOfflineDetailsTemp.getOperationState();
            row[5 + a] = DateUtil.getInstance().format(fsuOfflineDetailsTemp.getStartTime());
            row[6 + a] = DateUtil.getInstance().format(fsuOfflineDetailsTemp.getRecoveryTime());
            row[7 + a] = fsuOfflineDetailsTemp.getDuration() + "";
        }
        return new String[][][]{sheetData};
    }
}
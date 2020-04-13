package com.kongtrolink.framework.reports.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.utils.ReflectionUtils;
import com.kongtrolink.framework.entity.OperaCodePrefix;
import com.kongtrolink.framework.reports.api.AlarmReportsService;
import com.kongtrolink.framework.reports.dao.AlarmCategoryTempDao;
import com.kongtrolink.framework.reports.dao.AlarmCountTempDao;
import com.kongtrolink.framework.reports.dao.AlarmDetailsDao;
import com.kongtrolink.framework.reports.dao.ReportTaskDao;
import com.kongtrolink.framework.reports.entity.*;
import com.kongtrolink.framework.reports.entity.alarmCategory.AlarmCategoryTemp;
import com.kongtrolink.framework.reports.entity.alarmCount.AlarmCountTemp;
import com.kongtrolink.framework.reports.entity.alarmDetails.AlarmDetailsTemp;
import com.kongtrolink.framework.reports.entity.query.DeviceEntity;
import com.kongtrolink.framework.reports.entity.query.FsuEntity;
import com.kongtrolink.framework.reports.entity.query.SiteEntity;
import com.kongtrolink.framework.reports.service.MqttCommonInterface;
import com.kongtrolink.framework.reports.stereotype.ReportExtend;
import com.kongtrolink.framework.reports.stereotype.ReportOperaCode;
import com.kongtrolink.framework.reports.utils.CommonCheck;
import com.kongtrolink.framework.reports.utils.DateUtil;
import com.kongtrolink.framework.reports.utils.WorkbookUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.DecimalFormat;
import java.util.*;

/**
 * \* @Author: mystox
 * \* Date: 2020/2/13 13:32
 * \* Description:
 * \
 */
@Service
public class AlarmReportsServiceImpl implements AlarmReportsService {
    private Logger logger = LoggerFactory.getLogger(AlarmReportsServiceImpl.class);

    @Value("${server.routeMark}")
    private String routeMark;
    //    @Autowired
//    MqttOpera mqttOpera;
    @Autowired
    MqttCommonInterface mqttCommonInterface;
    @Autowired
    AlarmCountTempDao alarmCountTempDao;

    @Autowired
    AlarmDetailsDao alarmDetailsDao;
    @Autowired
    AlarmCategoryTempDao alarmCategoryTempDao;
    @Autowired
    ReportTaskDao reportTaskDao;

    @Override
    @ReportOperaCode(code = OperaCodePrefix.REPORTS + "alarmCount", rhythm = 3600 * 24, dataType = {DataType.JSON, DataType.FILE}, extend = {
            @ReportExtend(field = "month", name = "月份", type = ReportExtend.FieldType.STRING, description = "格式为year-month"), //时间类型是否需要
//            @ReportExtend(field = "region", name = "区域层级", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, uri = "/proxy_ap/region/getCurrentRegion"), //区域层级
            @ReportExtend(field = "stationList", name = "区域层级(站点级)", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, uri = "/reportsOpera/getStationList"), //站点列表
            @ReportExtend(field = "currentUser", name = "当前用户", type = ReportExtend.FieldType.JSON, belong = ExecutorType.query, uri = "/proxy_ap/commonFunc/getUserInfo", hide = true), //当前用户信息
            @ReportExtend(field = "stationType", name = "站点类型", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "A级机房", "B级机房", "C级机房", "D级机房"}),
            @ReportExtend(field = "operationState", name = "运行状态", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "交维态", "工程态", "测试态"}),
//            @ReportExtend(field = "fsuManufactory", name = "fsu厂家", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "义益钛迪"}),
            @ReportExtend(field = "alarmStatus", name = "告警状态", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"历史告警"}),
            @ReportExtend(field = "alarmLevel", name = "告警等级", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, uri = "/reportsOpera/getAlarmLevel"),
            @ReportExtend(field = "statisticLevel", name = "统计维度", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"省级", "市级", "区县级", "站点级"}),
            @ReportExtend(field = "statisticPeriod", name = "统计周期", type = ReportExtend.FieldType.STATISTIC_PERIOD, belong = ExecutorType.query, select = {"月报表", "季报表", "年报表"}, description = "{dimension:月报表,timePeriod:{startTime:yyyy-MM-dd,endTime:yyyy-MM-dd}}"),
//            @ReportExtend(field = "timePeriod", name = "时间段", type = ReportExtend.FieldType.DATE_PERIOD, belong = ExecutorType.query, description = "时间范围,返回格式为{startTime:yyyy-MM-dd,endTime:yyyy-MM-dd}"),
    })
    public ReportData alarmCount(String reportConfigStr) {
        ReportConfig reportConfig = JSONObject.parseObject(reportConfigStr, ReportConfig.class);
        ExecutorType executorType = reportConfig.getExecutorType();
        ReportTask reportTask = reportTaskDao.findByByUniqueCondition(
                reportConfig.getServerCode(), reportConfig.getEnterpriseCode(), reportConfig.getOperaCode(), reportConfig.getReportServerCode());
        if (reportTask == null)
            return new ReportData(DataType.ERROR, "report task is not exists [" + JSONObject.toJSONString(reportTask) + "]");
        if (ExecutorType.query.equals(executorType)) {
            return AlarmCountQuery(reportConfig, reportTask);
        } else return alarmCountExecutor(reportConfig, reportTask);
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
    private ReportData alarmCountExecutor(ReportConfig reportConfig, ReportTask reportTask) {
        String serverCode = reportConfig.getServerCode();
        String enterpriseCode = reportConfig.getEnterpriseCode();
        List<AlarmCountTemp> alarmCountTempList = new ArrayList<AlarmCountTemp>();
        //获取时间信息
        /*JSONObject condition = reportConfig.getCondition();
        if (TaskType.singleTask.name().equals(reportTask.getTaskType())) {
            String monthStr = condition.getString("month");

        } else {

        }*/
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);


        JSONObject baseCondition = new JSONObject();
        baseCondition.put("serverCode", serverCode);
        baseCondition.put("enterpriseCode", enterpriseCode);
        Integer alarmCycle = mqttCommonInterface.getAlarmCycle(baseCondition);
        if (alarmCycle != null) {
            int cycleDay = alarmCycle / 24 + 1;
            if (day <= cycleDay)  //每月前几天周期统计上月的历史数据
                if (month == 1) {
                    month = 12;
                    year -= 1;
                } else month -= 1;
        }
        // 获取企业在该云平台下所有站点
        List<SiteEntity> siteList = mqttCommonInterface.getSiteList(baseCondition);
        logger.debug("get site list is:[{}]", JSONObject.toJSONString(siteList));
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
                if (CollectionUtils.isEmpty(fsuList)) {
                    logger.error("get fsu list is null:[{}]", JSONObject.toJSONString(s));
                    return;
                }
                FsuEntity fsuEntity = fsuList.get(0);
                String manufacturer = fsuEntity.getManufacturer();
                String operationState = CommonCheck.fsuOperaStateCheck(fsuList);
                List<String> fsuIds = ReflectionUtils.convertElementPropertyToList(fsuList, "fsuId");
                logger.debug("get fsu list is:[{}]", JSONObject.toJSONString(fsuIds));
                List<DeviceEntity> deviceList = mqttCommonInterface.getDeviceList(fsuIds, baseCondition);
                List<String> deviceIds = new ArrayList<>();
                if (!CollectionUtils.isEmpty(deviceList)) {
                    deviceIds = ReflectionUtils.convertElementPropertyToList(deviceList, "deviceId");
                    logger.debug("get device list is:[{}]", JSONObject.toJSONString(deviceIds));
                }
                deviceIds.addAll(fsuIds);
               /* / Test
                List<String> deviceIds = new ArrayList<>();
                String operationState = "交维态";
                deviceIds.add("10010_1021006");
                deviceIds.add("10010_1021015");
                List<JSONObject> jsonObjects = mqttCommonInterface.countAlarmByDeviceIds(deviceIds, 2020, 3, baseCondition);*/
                // 获取上月告警统计信息 ,包括多项告警统计信息 根据等级统计上个月内的所有历史告警数量和告警恢复数量
                List<JSONObject> jsonObjects = mqttCommonInterface.countAlarmByDeviceIds(deviceIds, finalYear, finalMonth, baseCondition);
                logger.debug("statistic count Alarm ByDeviceIds, site reportTaskId is [{}]", reportTask.getId());
                jsonObjects.forEach(entity -> {
                    //填充站点告警统计信息
                    AlarmCountTemp alarmCountTemp = new AlarmCountTemp();
                    alarmCountTemp.setYear(finalYear);
                    alarmCountTemp.setMonth(finalMonth);
                    alarmCountTemp.setAlarmLevel(entity.getString("name"));
                    alarmCountTemp.setAlarmCount(entity.getLong("count"));
                    alarmCountTemp.setAlarmRecoveryCount(entity.getLong("recoverCount"));
                    alarmCountTemp.setStationId(stationId);
                    alarmCountTemp.setStationName(stationName);
                    alarmCountTemp.setStationType(siteType);
                    alarmCountTemp.setOperationState(operationState);
                    alarmCountTemp.setProvince(province);
                    alarmCountTemp.setMunicipality(municipality);
                    alarmCountTemp.setCounty(county);
                    alarmCountTemp.setFsuManufactory(manufacturer);
                    alarmCountTemp.setTempDate(new Date());
                    alarmCountTempList.add(alarmCountTemp);
                });
            });
        } else {
            logger.error("返回站点结果为空");
        }
        String reportTaskId = reportTask.getId();
        if (!CollectionUtils.isEmpty(alarmCountTempList)) {
            alarmCountTempDao.updateDelete(year, month, reportTaskId);
            alarmCountTempDao.save(alarmCountTempList, reportTaskId);
        }
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
    private ReportData AlarmCountQuery(ReportConfig reportConfig, ReportTask reportTask) {

        String taskId = reportTask.getId();
        JSONObject condition = reportConfig.getCondition();//获取查询条件

        String statisticLevel = condition.getString("statisticLevel");
        //根据用户id的区域权限及其搜索条件获取站点列表筛选
        JSONObject currentUser = condition.getJSONObject("currentUser");
        if (currentUser == null) currentUser = new JSONObject();
        List<JSONObject> alarmCountList = new ArrayList<>();
        JSONObject statisticPeriod = condition.getJSONObject("statisticPeriod");
        TimePeriod timePeriod = CommonCheck.getTimePeriod(statisticPeriod);
        String period = timePeriod.getDimension();
        // 根据查询条件极其内容生成结果文件
        alarmCountList = alarmCountTempDao.getCountDataByCondition(taskId, condition, timePeriod);
        //统计告警量
//        alarmCountList.add(new JSONObject());
        if (alarmCountList == null) alarmCountList = new ArrayList<>();
        String[][][] resultData = AlarmCountDataCreate(alarmCountList, statisticLevel);
        String resultType = reportConfig.getDataType();
        if (!DataType.FILE.equals(resultType)) {
            JsonData jsonData = new JsonData();
            jsonData.setName("告警量统计表");
            List<String[]> dataArr = Arrays.asList(resultData[0]);
            dataArr = new ArrayList<>(dataArr);
            dataArr.remove(0);
            for (int i = 0; i<4; i++) {
                dataArr.remove(dataArr.size()-1);
            }
            jsonData.setData(dataArr.toArray(new String[dataArr.size()][]));
            jsonData.setUnit("");
            jsonData.setxAxis(resultData[0][0]);
            ReportData reportData = new ReportData(DataType.JSON, JSONObject.toJSONString(jsonData));
            return reportData;
        }
        int length = resultData[0].length;
        resultData[0][length - 4] = new String[]{""};
        resultData[0][length - 3] = new String[]{"统计周期:" + period};
        resultData[0][length - 2] = new String[]{"时间段:" + timePeriod.getStartTimeStr() + "至" + timePeriod.getEndTimeStr()};
        resultData[0][length - 1] = new String[]{"操作人员:" + currentUser.getString("name")};
        String excelUri = AlarmCountExcelCreate("告警量统计表-" + statisticLevel, resultData);
        ReportData reportData = new ReportData(DataType.FILE, excelUri);
        return reportData;
    }


    private String[][][] AlarmCountDataCreate(List<JSONObject> alarmCountList, String statisticLevel) {
        String[] tableHead = null;
        if (StringUtils.equalsAny(statisticLevel, "省级", "市级", "区县级")) {
            tableHead = new String[]{"区域层级", "告警总数", "告警恢复数"};
        } else
            tableHead = new String[]{"区域层级", "站点名称", "站点类型", "告警总数", "告警恢复数"};

        int colLength = tableHead.length; // 列
        int rowLength = alarmCountList.size() + 1; //行数 vc
        String[][] sheetData = new String[rowLength + 4][colLength];

        for (int i = 0; i < rowLength; i++) {
            String[] row = sheetData[i];
            if (i == 0) {
                sheetData[i] = tableHead;
                continue;
            }
            JSONObject jsonObject = alarmCountList.get(i - 1);
            row[0] = CommonCheck.aggregateTierName(jsonObject);
            int a = 0;
            if (colLength > 3) {
                row[1] = jsonObject.getString("stationName");
                row[2] = jsonObject.getString("stationType");
                a = 2;
            }
            row[1 + a] = jsonObject.getString("alarmCount");
            row[2 + a] = jsonObject.getString("alarmRecoveryCount");
        }
        return new String[][][]{sheetData};
    }

    private String AlarmCountExcelCreate(String sheetName, String[][][] resultData) {
        long currentTime = System.currentTimeMillis();
        String path = "/reportsResources/report_alarmCount";
        String filename = "告警量统计表_" + currentTime;
        WorkbookUtil.save("." + path, filename, WorkbookUtil.createWorkBook(new String[]{sheetName}, resultData));
        return "/" + routeMark + path + "/" + filename + ".xls";
    }

    @Override
    @ReportOperaCode(code = OperaCodePrefix.REPORTS + "alarmDetails", rhythm = 3600 * 24, dataType = {DataType.JSON, DataType.FILE}, extend = {
//            @ReportExtend(field = "month", name = "月份", type = ReportExtend.FieldType.STRING), //时间类型是否需要
//            @ReportExtend(field = "date", name = "时间", type = ReportExtend.FieldType.DATE), //时间类型是否需要
            @ReportExtend(field = "stationList", name = "区域层级(站点级)", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, uri = "/reportsOpera/getStationList"), //站点列表
            @ReportExtend(field = "currentUser", name = "当前用户", type = ReportExtend.FieldType.JSON, belong = ExecutorType.query, uri = "/proxy_ap/commonFunc/getUserInfo", hide = true), //当前用户信息
            @ReportExtend(field = "stationType", name = "站点类型", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "A级机房", "B级机房", "C级机房", "D级机房"}),
            @ReportExtend(field = "operationState", name = "运行状态", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "交维态", "工程态", "测试态"}),
            @ReportExtend(field = "fsuManufactory", name = "fsu厂家", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "义益钛迪"}),
            @ReportExtend(field = "alarmStatus", name = "告警状态", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"历史告警","活动告警"}),
            @ReportExtend(field = "alarmLevel", name = "告警等级", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, uri = "/reportsOpera/getAlarmLevel"),
            @ReportExtend(field = "statisticLevel", name = "统计维度", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"站点级"}),
            @ReportExtend(field = "statisticPeriod", name = "统计周期", type = ReportExtend.FieldType.STATISTIC_PERIOD, belong = ExecutorType.query, select = {"月报表", "季报表", "年报表"}, description = "{dimension:月报表,timePeriod:{startTime:yyyy-MM-dd,endTime:yyyy-MM-dd}}"),
//         @ReportExtend(field = "timePeriod", name = "时间段", type = ReportExtend.FieldType.DATE, belong = ExecutorType.query),
    })
    public ReportData alarmDetails(String reportConfigStr) {

        ReportConfig reportConfig = JSONObject.parseObject(reportConfigStr, ReportConfig.class);
        ExecutorType executorType = reportConfig.getExecutorType();
        ReportTask reportTask = reportTaskDao.findByByUniqueCondition(
                reportConfig.getServerCode(), reportConfig.getEnterpriseCode(), reportConfig.getOperaCode(), reportConfig.getReportServerCode());
        if (reportTask == null)
            return new ReportData(DataType.ERROR, "report task is not exists [" + JSONObject.toJSONString(reportTask) + "]");
        if (ExecutorType.query.equals(executorType))
            return alarmDetailsQuery(reportConfig, reportTask);
        else return alarmDetailsExecutor(reportConfig, reportTask);

    }

    private ReportData alarmDetailsExecutor(ReportConfig reportConfig, ReportTask reportTask) {
        String serverCode = reportConfig.getServerCode();
        String enterpriseCode = reportConfig.getEnterpriseCode();
        List<AlarmDetailsTemp> alarmDetailsTempList = new ArrayList<AlarmDetailsTemp>();
        //获取时间信息
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        JSONObject baseCondition = new JSONObject();
        baseCondition.put("serverCode", serverCode);
        baseCondition.put("enterpriseCode", enterpriseCode);
        Integer alarmCycle = mqttCommonInterface.getAlarmCycle(baseCondition);
        if (alarmCycle != null) {
            int cycleDay = alarmCycle / 24 + 1;
            if (day <= cycleDay)  //每月前两天统计上月的历史数据
                if (month == 1) {
                    month = 12;
                    year -= 1;
                } else month -= 1;
        }
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
                if (CollectionUtils.isEmpty(fsuList)) {
                    logger.warn("get fsu list is null:[{}]", JSONObject.toJSONString(s));
                    return;
                }
                FsuEntity fsuEntity = fsuList.get(0);
                String manufacturer = fsuEntity.getManufacturer();
                String operationState = CommonCheck.fsuOperaStateCheck(fsuList);
                List<String> fsuIds = ReflectionUtils.convertElementPropertyToList(fsuList, "fsuId");
                logger.debug("get fsu list is:[{}]", JSONObject.toJSONString(fsuIds));
                List<DeviceEntity> deviceList = mqttCommonInterface.getDeviceList(fsuIds, baseCondition);
                List<String> deviceIds = new ArrayList<>();
                if (!CollectionUtils.isEmpty(deviceList)) {
                    deviceIds = ReflectionUtils.convertElementPropertyToList(deviceList, "deviceId");
                    logger.debug("get device list is:[{}]", JSONObject.toJSONString(deviceIds));
                }
                deviceIds.addAll(fsuIds);
                // 获取上月告警统计信息 ,包括多项告警统计信息 根据等级统计上个月内的所有历史告警数量和告警恢复数量
                logger.debug("statistic count Alarm ByDeviceIds, site reportTaskId is [{}]", stationId);
               /* / Test
                List<String> deviceIds = new ArrayList<>();
                String operationState = "交维态";
                deviceIds.add("10010_1021006");
                deviceIds.add("10010_1021015");
                List<JSONObject> jsonObjects = mqttCommonInterface.countAlarmByDeviceIds(deviceIds, 2020, 3, baseCondition);*/
                List<JSONObject> jsonObjects = mqttCommonInterface.getAlarmDetails(deviceIds, finalYear, finalMonth, baseCondition);
                jsonObjects.forEach(entity -> {
                    //填充站点告警统计信息
                    AlarmDetailsTemp alarmDetailsTemp = new AlarmDetailsTemp();
                    alarmDetailsTemp.setYear(finalYear);
                    alarmDetailsTemp.setMonth(finalMonth);
                    alarmDetailsTemp.setAlarmLevel(entity.getString("targetLevelName"));
                    alarmDetailsTemp.setStationId(stationId);
                    alarmDetailsTemp.setStationName(stationName);
                    alarmDetailsTemp.setStationType(siteType);
                    alarmDetailsTemp.setOperationState(operationState);
                    alarmDetailsTemp.setProvince(province);
                    alarmDetailsTemp.setMunicipality(municipality);
                    alarmDetailsTemp.setCounty(county);
                    alarmDetailsTemp.setFsuManufactory(manufacturer);
                    alarmDetailsTemp.setAlarmName(entity.getString("name"));
                    Date treport = entity.getDate("treport");
                    alarmDetailsTemp.setStartTime(treport);
                    Date trecover = entity.getDate("trecover");
                    alarmDetailsTemp.setRecoveryTime(trecover);
                    if (trecover != null) {
                        Double duration = (double) (trecover.getTime() - treport.getTime()) / (1000 * 60);
                        alarmDetailsTemp.setDuration(duration);//分钟
                    }
                    alarmDetailsTempList.add(alarmDetailsTemp);
                });
            });
        } else {
            logger.error("返回站点结果为空");
        }
        String reportTaskId = reportTask.getId();
        if (!CollectionUtils.isEmpty(alarmDetailsTempList)) {
            alarmDetailsDao.updateDelete(year, month, reportTaskId);
            alarmDetailsDao.save(alarmDetailsTempList, reportTaskId);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("executorTime", year + "-" + month);
        jsonObject.put("executorResult", alarmDetailsTempList.size());
        return new ReportData(DataType.TEXT, jsonObject.toJSONString());
    }

    private ReportData alarmDetailsExecutorCurrent(ReportConfig reportConfig, ReportTask reportTask) {
        String serverCode = reportConfig.getServerCode();
        String enterpriseCode = reportConfig.getEnterpriseCode();
        List<AlarmDetailsTemp> alarmDetailsTempList = new ArrayList<AlarmDetailsTemp>();
        //获取时间信息
        int year = 0;
        int month = 0;
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
                if (CollectionUtils.isEmpty(fsuList)) {
                    logger.warn("get fsu list is null:[{}]", JSONObject.toJSONString(s));
                    return;
                }
                FsuEntity fsuEntity = fsuList.get(0);
                String manufacturer = fsuEntity.getManufacturer();
                String operationState = CommonCheck.fsuOperaStateCheck(fsuList);
                List<String> fsuIds = ReflectionUtils.convertElementPropertyToList(fsuList, "fsuId");
                List<DeviceEntity> deviceList = mqttCommonInterface.getDeviceList(fsuIds, baseCondition);
                List<String> deviceIds = ReflectionUtils.convertElementPropertyToList(deviceList, "deviceId");
                deviceIds.addAll(fsuIds);
                // 获取上月告警统计信息 ,包括多项告警统计信息 根据等级统计上个月内的所有历史告警数量和告警恢复数量
                logger.debug("statistic count Alarm ByDeviceIds, site reportTaskId is [{}]", stationId);
               /* / Test
                List<String> deviceIds = new ArrayList<>();
                String operationState = "交维态";
                deviceIds.add("10010_1021006");
                deviceIds.add("10010_1021015");
                List<JSONObject> jsonObjects = mqttCommonInterface.countAlarmByDeviceIds(deviceIds, 2020, 3, baseCondition);*/
                List<JSONObject> jsonObjects = mqttCommonInterface.getAlarmCurrentDetails(deviceIds, finalYear, finalMonth, baseCondition);
                jsonObjects.forEach(entity -> {
                    //填充站点告警统计信息
                    AlarmDetailsTemp alarmDetailsTemp = new AlarmDetailsTemp();
                    alarmDetailsTemp.setYear(finalYear);
                    alarmDetailsTemp.setMonth(finalMonth);
                    alarmDetailsTemp.setAlarmLevel(entity.getString("targetLevelName"));
                    alarmDetailsTemp.setStationId(stationId);
                    alarmDetailsTemp.setStationName(stationName);
                    alarmDetailsTemp.setStationType(siteType);
                    alarmDetailsTemp.setOperationState(operationState);
                    alarmDetailsTemp.setProvince(province);
                    alarmDetailsTemp.setMunicipality(municipality);
                    alarmDetailsTemp.setCounty(county);
                    alarmDetailsTemp.setFsuManufactory(manufacturer);
                    alarmDetailsTemp.setAlarmName(entity.getString("name"));
                    Date treport = entity.getDate("treport");
                    alarmDetailsTemp.setStartTime(treport);
                    Date trecover = entity.getDate("trecover");
                    alarmDetailsTemp.setRecoveryTime(trecover);
                    if (trecover != null) {
                        Double duration = (double) (trecover.getTime() - treport.getTime()) / (1000 * 60);
                        alarmDetailsTemp.setDuration(duration);//分钟
                    }
                    alarmDetailsTempList.add(alarmDetailsTemp);
                });
            });
        } else {
            logger.error("返回站点结果为空");
        }
        String reportTaskId = reportTask.getId();
        if (!CollectionUtils.isEmpty(alarmDetailsTempList)) {
            alarmDetailsDao.updateDelete(year, month, reportTaskId);
            alarmDetailsDao.save(alarmDetailsTempList, reportTaskId);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("executorTime", year + "-" + month);
        jsonObject.put("executorResult", alarmDetailsTempList.size());
        return new ReportData(DataType.TEXT, jsonObject.toJSONString());
    }

    private ReportData alarmDetailsQuery(ReportConfig reportConfig, ReportTask reportTask) {

        JSONObject condition = reportConfig.getCondition();//获取查询条件
        String alarmStatus = condition.getString("alarmStatus");
        if ("活动告警".equals(alarmStatus)) {
            alarmDetailsExecutorCurrent(reportConfig, reportTask);
        }
        String taskId = reportTask.getId();
        String statisticLevel = condition.getString("statisticLevel");
        JSONObject currentUser = condition.getJSONObject("currentUser");
        if (currentUser == null) currentUser = new JSONObject();
        JSONObject statisticPeriod = condition.getJSONObject("statisticPeriod");
        TimePeriod timePeriod = CommonCheck.getTimePeriod(statisticPeriod);
        String period = timePeriod.getDimension();
        if ("活动告警".equals(alarmStatus)) {
            timePeriod = new TimePeriod();
            timePeriod.setDimension("current");
            alarmDetailsExecutorCurrent(reportConfig, reportTask);
        }
        List<AlarmDetailsTemp> alarmDetailsTemps = alarmDetailsDao.findAlarmDetailsList(taskId, condition, timePeriod);
        String[][][] resultData = alarmDetailsDataCreate(alarmDetailsTemps);
        String resultType = reportConfig.getDataType();
        if (!DataType.FILE.equals(resultType)) {
            JsonData jsonData = new JsonData();
            jsonData.setName("告警明细统计表");
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
        resultData[0][length - 2] = new String[]{"时间段:" + timePeriod.getStartTimeStr() + "至" + timePeriod.getEndTimeStr()};
        resultData[0][length - 1] = new String[]{"操作人员:" + currentUser.getString("name")};
        String excelUri = alarmDetailsExcelCreate("告警量统计表-" + statisticLevel, resultData);
        ReportData reportData = new ReportData(DataType.JSON, excelUri);
        return reportData;
    }


    private String alarmDetailsExcelCreate(String sheetName, String[][][] resultData) {
        long currentTime = System.currentTimeMillis();
        String path = "/reportsResources/report_alarmDetails";
        String filename = "告警明细表_" + currentTime;
        WorkbookUtil.save("." + path, filename, WorkbookUtil.createWorkBook(new String[]{sheetName}, resultData));
        return "/" + routeMark + path + "/" + filename + ".xls";
    }

    private String[][][] alarmDetailsDataCreate(List<AlarmDetailsTemp> detailsList) {
        String[] tableHead = null;
        tableHead = new String[]{"区域层级", "站点名称", "站点编号", "站点类型", "运行状态", "告警名称", "告警等级", "开始时间", "恢复时间", "时长（分钟）"};

        int colLength = tableHead.length; // 列
        int rowLength = detailsList.size() + 1; //行
        String[][] sheetData = new String[rowLength + 4][colLength];
        for (int i = 0; i < rowLength; i++) {
            String[] row = sheetData[i];
            if (i == 0) {
                sheetData[i] = tableHead;
                continue;
            }
            AlarmDetailsTemp alarmDetailsTemp = detailsList.get(i - 1);
            row[0] = CommonCheck.aggregateTierName((JSONObject) JSONObject.toJSON(alarmDetailsTemp));
            int a = 0;
            row[1 + a] = alarmDetailsTemp.getStationName();
            row[2 + a] = alarmDetailsTemp.getStationId();
            row[3 + a] = alarmDetailsTemp.getStationType();
            row[4 + a] = alarmDetailsTemp.getOperationState();
            row[5 + a] = alarmDetailsTemp.getAlarmName();
            row[6 + a] = alarmDetailsTemp.getAlarmLevel();
            row[7 + a] = DateUtil.getInstance().format(alarmDetailsTemp.getStartTime());
            Date recoveryTime = alarmDetailsTemp.getRecoveryTime();
            if (recoveryTime != null)
                row[8 + a] = DateUtil.getInstance().format(recoveryTime);
            Double duration = alarmDetailsTemp.getDuration();
            if (duration != null)
                row[9 + a] = new DecimalFormat("#.00").format(duration);
        }
        return new String[][][]{sheetData};
    }

    @Override
    @ReportOperaCode(code = OperaCodePrefix.REPORTS + "alarmCategory", rhythm = 3600 * 24, dataType = {DataType.JSON, DataType.FILE}, extend = {
//            @ReportExtend(field = "month", name = "月份", type = ReportExtend.FieldType.STRING), //时间类型是否需要
            //@ReportExtend(field = "date", name = "时间", type = ReportExtend.FieldType.DATE), //时间类型是否需要
            @ReportExtend(field = "stationList", name = "区域层级(站点级)", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, uri = "/reportsOpera/getStationList"), //站点列表
            @ReportExtend(field = "currentUser", name = "当前用户", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, value = "/proxy_ap/commonFunc/getUserInfo", hide = true), //当前用户信息
            @ReportExtend(field = "stationType", name = "站点类型", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "A级机房", "B级机房", "C级机房", "D级机房"}),
            @ReportExtend(field = "operationState", name = "运行状态", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "交维态", "工程态", "测试态"}),
            @ReportExtend(field = "fsuManufactory", name = "fsu厂家", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"全部", "义益钛迪"}),
            @ReportExtend(field = "alarmStatus", name = "告警状态", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"历史告警","活动告警"}),
            @ReportExtend(field = "alarmLevel", name = "告警等级", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, uri = "/reportsOpera/getAlarmLevel"),
            @ReportExtend(field = "statisticPeriod", name = "统计周期", type = ReportExtend.FieldType.STATISTIC_PERIOD, belong = ExecutorType.query, select = {"月报表", "季报表", "年报表"}, description = "{dimension:月报表,timePeriod:{startTime:yyyy-MM-dd,endTime:yyyy-MM-dd}}"),
    })
    public ReportData alarmCategory(String reportConfigStr) {
        ReportConfig reportConfig = JSONObject.parseObject(reportConfigStr, ReportConfig.class);
        ExecutorType executorType = reportConfig.getExecutorType();
        ReportTask reportTask = reportTaskDao.findByByUniqueCondition(
                reportConfig.getServerCode(), reportConfig.getEnterpriseCode(), reportConfig.getOperaCode(), reportConfig.getReportServerCode());
        if (reportTask == null)
            return new ReportData(DataType.ERROR, "report task is not exists [" + JSONObject.toJSONString(reportTask) + "]");
        if (ExecutorType.query.equals(executorType))
            return alarmCategoryQuery(reportConfig, reportTask);
        else return alarmCategoryExecutor(reportConfig, reportTask);

    }


    /**
     * @return com.kongtrolink.framework.reports.entity.ReportData
     * @Date 16:03 2020/2/25
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description //告警分类统计表执行逻辑
     **/
    private ReportData alarmCategoryExecutor(ReportConfig reportConfig, ReportTask reportTask) {
        String serverCode = reportConfig.getServerCode();
        String enterpriseCode = reportConfig.getEnterpriseCode();
        List<AlarmCategoryTemp> alarmCountTempList = new ArrayList<>();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        JSONObject baseCondition = new JSONObject();
        baseCondition.put("serverCode", serverCode);
        baseCondition.put("enterpriseCode", enterpriseCode);
        Integer alarmCycle = mqttCommonInterface.getAlarmCycle(baseCondition);
        if (alarmCycle != null) {
            int cycleDay = alarmCycle / 24 + 1;
            if (day <= cycleDay)  //每月前两天统计上月的历史数据
                if (month == 1) {
                    month = 12;
                    year -= 1;
                } else month -= 1;
        }
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
                if (CollectionUtils.isEmpty(fsuList)) {
                    logger.warn("get fsu list is null. site:[{}]", JSONObject.toJSONString(s));
                    return;
                }
                FsuEntity fsuEntity = fsuList.get(0);
                String manufacturer = fsuEntity.getManufacturer();
                String operationState = CommonCheck.fsuOperaStateCheck(fsuList);
                List<String> fsuIds = ReflectionUtils.convertElementPropertyToList(fsuList, "fsuId");
                logger.debug("get fsu list is:[{}]", JSONObject.toJSONString(fsuIds));
                List<DeviceEntity> deviceList = mqttCommonInterface.getDeviceList(fsuIds, baseCondition);
                List<String> deviceIds = new ArrayList<>();
                if (!CollectionUtils.isEmpty(deviceList)) {
                    deviceIds = ReflectionUtils.convertElementPropertyToList(deviceList, "deviceId");
                    logger.debug("get device list is:[{}]", JSONObject.toJSONString(deviceIds));
                }
                deviceIds.addAll(fsuIds);
                // 获取上月告警统计信息 ,包括多项告警统计信息 根据等级统计上个月内的所有历史告警数量和告警恢复数量
                logger.debug("statistic category Alarm ByDeviceIds, site reportTaskId is [{}]", stationId);
               /* / Test
                List<String> deviceIds = new ArrayList<>();
                String operationState = "交维态";
                deviceIds.add("10010_1021006");
                deviceIds.add("10010_1021015");
                List<JSONObject> jsonObjects = mqttCommonInterface.countAlarmByDeviceIds(deviceIds, 2020, 3, baseCondition);*/
                List<JSONObject> jsonObjects = mqttCommonInterface.getAlarmCategoryByDeviceIds(deviceIds, finalYear, finalMonth, baseCondition);
                if (!CollectionUtils.isEmpty(jsonObjects))
                jsonObjects.forEach(entity -> {
                    //填充站点告警明细统计信息
                    AlarmCategoryTemp alarmCategoryTemp = JSONObject.toJavaObject(entity, AlarmCategoryTemp.class);
                    alarmCategoryTemp.setYear(finalYear);
                    alarmCategoryTemp.setMonth(finalMonth);
                    alarmCategoryTemp.setStationId(stationId);
                    alarmCategoryTemp.setStationName(stationName);
                    alarmCategoryTemp.setStationType(siteType);
                    alarmCategoryTemp.setOperationState(operationState);
                    alarmCategoryTemp.setProvince(province);
                    alarmCategoryTemp.setMunicipality(municipality);
                    alarmCategoryTemp.setCounty(county);
                    alarmCategoryTemp.setFsuManufactory(manufacturer);
                    alarmCountTempList.add(alarmCategoryTemp);
                });
            });
        } else {
            logger.error("返回站点结果为空");
        }
        String reportTaskId = reportTask.getId();
        if (!CollectionUtils.isEmpty(alarmCountTempList)) {
            alarmCategoryTempDao.updateDelete(year, month, reportTaskId);
            alarmCategoryTempDao.save(alarmCountTempList, reportTaskId);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("executorTime", year + "-" + month);
        jsonObject.put("executorResult", alarmCountTempList);
        return new ReportData(DataType.TEXT, jsonObject.toJSONString());
    }

    private ReportData alarmCategoryQueryCurrent(ReportConfig reportConfig, ReportTask reportTask) {
        String serverCode = reportConfig.getServerCode();
        String enterpriseCode = reportConfig.getEnterpriseCode();
        List<AlarmCategoryTemp> alarmCountTempList = new ArrayList<>();
        int year = 0; //活动告警的年和月为0
        int month = 0;

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
                if (CollectionUtils.isEmpty(fsuList)) {
                    logger.error("get fsu list is null:[{}]", JSONObject.toJSONString(s));
                    return;
                }
                FsuEntity fsuEntity = fsuList.get(0);
                String manufacturer = fsuEntity.getManufacturer();
                String operationState = CommonCheck.fsuOperaStateCheck(fsuList);
                List<String> fsuIds = ReflectionUtils.convertElementPropertyToList(fsuList, "fsuId");
                logger.debug("get fsu list is:[{}]", JSONObject.toJSONString(fsuIds));
                List<DeviceEntity> deviceList = mqttCommonInterface.getDeviceList(fsuIds, baseCondition);
                List<String> deviceIds = new ArrayList<>();
                if (!CollectionUtils.isEmpty(deviceList)) {
                    deviceIds = ReflectionUtils.convertElementPropertyToList(deviceList, "deviceId");
                    logger.debug("get device list is:[{}]", JSONObject.toJSONString(deviceIds));
                }
                deviceIds.addAll(fsuIds);
                // 获取上月告警统计信息 ,包括多项告警统计信息 根据等级统计上个月内的所有历史告警数量和告警恢复数量
                logger.debug("statistic category Alarm ByDeviceIds, site reportTaskId is [{}]", stationId);
                List<JSONObject> jsonObjects = mqttCommonInterface.getAlarmCurrentCategoryByDeviceIds(deviceIds, finalYear, finalMonth, baseCondition);
                if (!CollectionUtils.isEmpty(jsonObjects))
                jsonObjects.forEach(entity -> {
                    //填充站点告警明细统计信息
                    AlarmCategoryTemp alarmCategoryTemp = JSONObject.toJavaObject(entity, AlarmCategoryTemp.class);
                    alarmCategoryTemp.setYear(finalYear);
                    alarmCategoryTemp.setMonth(finalMonth);
                    alarmCategoryTemp.setStationId(stationId);
                    alarmCategoryTemp.setStationName(stationName);
                    alarmCategoryTemp.setStationType(siteType);
                    alarmCategoryTemp.setOperationState(operationState);
                    alarmCategoryTemp.setProvince(province);
                    alarmCategoryTemp.setMunicipality(municipality);
                    alarmCategoryTemp.setCounty(county);
                    alarmCategoryTemp.setFsuManufactory(manufacturer);
                    alarmCountTempList.add(alarmCategoryTemp);
                });
            });
        } else {
            logger.error("返回站点结果为空");
        }
        String reportTaskId = reportTask.getId();
        if (!CollectionUtils.isEmpty(alarmCountTempList)) {
            alarmCategoryTempDao.updateDelete(year, month, reportTaskId);
            alarmCategoryTempDao.save(alarmCountTempList, reportTaskId);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("executorTime", year + "-" + month);
        jsonObject.put("executorResult", alarmCountTempList);
        return new ReportData(DataType.TEXT, jsonObject.toJSONString());
    }

    private ReportData alarmCategoryQuery(ReportConfig reportConfig, ReportTask reportTask) {

        String taskId = reportTask.getId();
        JSONObject condition = reportConfig.getCondition();//获取查询条件
        String alarmStatus = condition.getString("alarmStatus");
        if ("活动告警".equals(alarmStatus)) {
            alarmCategoryQueryCurrent(reportConfig, reportTask);
        }
        String statisticLevel = condition.getString("statisticLevel");
        //根据用户id的区域权限及其搜索条件获取站点列表筛选
        JSONObject currentUser = condition.getJSONObject("currentUser");
        if (currentUser == null) currentUser = new JSONObject();
        JSONObject statisticPeriod = condition.getJSONObject("statisticPeriod");
        TimePeriod timePeriod = CommonCheck.getTimePeriod(statisticPeriod);
        String period = timePeriod.getDimension();
        if ("活动告警".equals(alarmStatus)) {
            timePeriod = new TimePeriod();
            timePeriod.setDimension("current");
            alarmDetailsExecutorCurrent(reportConfig, reportTask);
        }
        List<JSONObject> alarmCategoryDatas = alarmCategoryTempDao.getCategoryDataByCondition(taskId, condition, timePeriod);
        //统计告警分类
        String[][][] resultData = alarmCategoryDataCreate(alarmCategoryDatas, statisticLevel);
        String resultType = reportConfig.getDataType();
        if (!DataType.FILE.equals(resultType)) {
            JsonData jsonData = new JsonData();
            jsonData.setName("告警分类统计表");
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
        resultData[0][length - 2] = new String[]{"时间段:" + timePeriod.getStartTimeStr() + "至" + timePeriod.getEndTimeStr()};
        resultData[0][length - 1] = new String[]{"操作人员:" + currentUser.getString("name")};
        String excelUri = alarmCategoryExcelCreate("告警分类统计表-" + statisticLevel, resultData);
        ReportData reportData = new ReportData(DataType.JSON, excelUri);
        return reportData;
    }


    private String[][][] alarmCategoryDataCreate(List<JSONObject> alarmCategoryList, String statisticLevel) {
        String[] tableHead = null;
        if (StringUtils.equalsAny(statisticLevel, "省级", "市级", "区县级")) {
            tableHead = new String[]{"区域层级", "告警总数", "单站平均告警", "FSU离线", "烟感", "温湿度", "开关电源", "蓄电池", "红外设备", "门磁", "水浸", "空调", "其他"};
        } else
            tableHead = new String[]{"区域层级", "站点名称", "站点类型", "生产厂家", "告警总数", "FSU离线", "烟感", "温湿度", "开关电源", "蓄电池", "红外设备", "门磁", "水浸", "空调", "其他"};

        int colLength = tableHead.length; // 列
        int rowLength = alarmCategoryList.size() + 1; //行
        String[][] sheetData = new String[rowLength + 4][colLength];

        for (int i = 0; i < rowLength; i++) {
            String[] row = sheetData[i];
            if (i == 0) {
                sheetData[i] = tableHead;
                continue;
            }
            JSONObject jsonObject = alarmCategoryList.get(i - 1);
            row[0] = CommonCheck.aggregateTierName(jsonObject);
            int a = 0;
            Long otherCount = 0L;
            Long alarmCount = jsonObject.getLong("alarmCount");
            if (colLength > 13) {
                row[1] = jsonObject.getString("stationName");
                row[2] = jsonObject.getString("stationType");
                row[3] = jsonObject.getString("fsuManufactory");
                row[4] = String.valueOf(alarmCount);
                a = 4;
            } else {
                row[1] = String.valueOf(alarmCount);
                row[2 + a] = jsonObject.getString("countAvg");
            }
            Long fsuOffline = jsonObject.getLong("fsuOffline");
            otherCount -= fsuOffline == null ? 0L : fsuOffline;
            row[1 + a] = String.valueOf(fsuOffline);
            Long smokeSensation = jsonObject.getLong("smokeSensation");
            otherCount -= smokeSensation == null ? 0L : smokeSensation;
            row[2 + a] = String.valueOf(smokeSensation);
            Long sensirion = jsonObject.getLong("sensirion");
            otherCount -= sensirion == null ? 0L : sensirion;
            row[3 + a] = String.valueOf(sensirion);
            Long switchPower = jsonObject.getLong("switchPower");
            otherCount -= switchPower == null ? 0L : switchPower;
            row[4 + a] = String.valueOf(switchPower);
            Long battery = jsonObject.getLong("battery");
            otherCount -= battery == null ? 0L : battery;
            row[5 + a] = String.valueOf(battery);
            Long infrared = jsonObject.getLong("infrared");
            otherCount -= infrared == null ? 0L : infrared;
            row[6 + a] = String.valueOf(infrared);
            Long gateMagnetism = jsonObject.getLong("gateMagnetism");
            otherCount -= gateMagnetism == null ? 0L : gateMagnetism;
            row[7 + a] = String.valueOf(gateMagnetism);
            Long waterImmersion = jsonObject.getLong("waterImmersion");
            otherCount -= waterImmersion == null ? 0L : waterImmersion;
            row[8 + a] = String.valueOf(gateMagnetism);
            Long airConditioning = jsonObject.getLong("airConditioning");
            otherCount -= airConditioning == null ? 0L : airConditioning;
            row[9 + a] = String.valueOf(airConditioning);
            row[10 + a] = String.valueOf(otherCount);

        }
        return new String[][][]{sheetData};
    }

    private String alarmCategoryExcelCreate(String sheetName, String[][][] resultData) {
        long currentTime = System.currentTimeMillis();
        String path = "/reportsResources/report_alarmCount";
        String filename = "告警分类统计表_" + currentTime + ".xls";
        WorkbookUtil.save("." + path, filename, WorkbookUtil.createWorkBook(new String[]{sheetName}, resultData));
        return "/" + routeMark + path + "/" + filename + ".xls";
    }


}
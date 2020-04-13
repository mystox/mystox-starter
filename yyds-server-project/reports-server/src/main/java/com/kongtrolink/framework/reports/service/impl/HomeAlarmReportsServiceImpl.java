package com.kongtrolink.framework.reports.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.utils.ReflectionUtils;
import com.kongtrolink.framework.entity.OperaCodePrefix;
import com.kongtrolink.framework.reports.api.AlarmReportsService;
import com.kongtrolink.framework.reports.api.HomeAlarmReportsService;
import com.kongtrolink.framework.reports.dao.*;
import com.kongtrolink.framework.reports.entity.*;
import com.kongtrolink.framework.reports.entity.alarmCategory.AlarmCategoryTemp;
import com.kongtrolink.framework.reports.entity.alarmCount.AlarmCountTemp;
import com.kongtrolink.framework.reports.entity.alarmCount.HomeAlarmCountTemp;
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
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * \* @Author: Mag
 * \* Date: 2020年4月8日 09:42:03
 * \* Description:
 * \ 首页需求：根据站点 按天统计历史告警数量
 */
@Service
public class HomeAlarmReportsServiceImpl implements HomeAlarmReportsService {
    private Logger logger = LoggerFactory.getLogger(HomeAlarmReportsServiceImpl.class);

    @Autowired
    MqttCommonInterface mqttCommonInterface;
    @Autowired
    HomeAlarmCountTempDao homeAlarmCountTempDao;
    @Autowired
    ReportTaskDao reportTaskDao;

    @Override
    @ReportOperaCode(code = OperaCodePrefix.REPORTS + "homeAlarmCount", rhythm = 3600 * 24, dataType = {DataType.TABLE, DataType.FILE}, extend = {
            @ReportExtend(field = "region", name = "区域层级", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, uri = "/proxy_ap/region/getCurrentRegion"), //区域层级
            @ReportExtend(field = "stationList", name = "区域层级(站点级)", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, uri = "/region/getStationList", hide = true), //站点列表
            @ReportExtend(field = "currentUser", name = "当前用户", type = ReportExtend.FieldType.JSON, belong = ExecutorType.query, uri = "/proxy_ap/commonFunc/getUserInfo", hide = true), //当前用户信息
            @ReportExtend(field = "timePeriod", name = "时间段", type = ReportExtend.FieldType.DATE_PERIOD, belong = ExecutorType.query, description = "时间范围,返回格式为{startTime:yyyy-MM-dd,endTime:yyyy-MM-dd}"),
    })
    public ReportData alarmCount(String reportConfigStr) {
        ReportConfig reportConfig = JSONObject.parseObject(reportConfigStr, ReportConfig.class);
        ExecutorType executorType = reportConfig.getExecutorType();
        ReportTask reportTask = reportTaskDao.findByByUniqueCondition(
                reportConfig.getServerCode(), reportConfig.getEnterpriseCode(), reportConfig.getOperaCode(), reportConfig.getReportServerCode());
        if (reportTask == null)
            return new ReportData(DataType.ERROR, "report task is not exists [" + JSONObject.toJSONString(reportTask) + "]");
        if (ExecutorType.query.equals(executorType))
            return AlarmCountQuery(reportConfig, reportTask);
        else return alarmCountExecutor(reportConfig, reportTask);
    }

    /**
     * 首页 告警量统计表的执行逻辑
     * 1.每月执行一次单月告警量统计数据，并检查最初告警时间做数据检查与填充，并支持指定月份的数据更新
     * 2.告警量统计数据以 {月份编号|区域层级|站点名称|站点类型|告警等级|告警状态|告警总数|告警恢复数} 为基本数据结构按站点为基本数据单元进行数据的归纳存储
     * 3.告警量统计表名以 t_report_execute_temp_${taskId}为基本格式
     * 4.根据搜索条件获取站点列表，进行报表数据查询
     * 5.统计范围为serverCode_businessCode 下所有的站点
     **/
    private ReportData alarmCountExecutor(ReportConfig reportConfig, ReportTask reportTask) {
        String serverCode = reportConfig.getServerCode();
        String enterpriseCode = reportConfig.getEnterpriseCode();
        List<HomeAlarmCountTemp> alarmCountTempList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date end =  calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date start =  calendar.getTime();
        long timeDate =  start.getTime();//统计日期
        String time = sdf.format(new Date(timeDate));
        JSONObject baseCondition = new JSONObject();
        baseCondition.put("serverCode", serverCode);
        baseCondition.put("enterpriseCode", enterpriseCode);
        // 获取企业在该云平台下所有站点
        List<SiteEntity> siteList = mqttCommonInterface.getSiteList(baseCondition);
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
                if (fsuList == null) {
                    logger.error("get fsu list is null:[{}]", JSONObject.toJSONString(s));
                    return;
                }
                String operationState = CommonCheck.fsuOperaStateCheck(fsuList);
                List<String> fsuIds = ReflectionUtils.convertElementPropertyToList(fsuList, "fsuId");
                List<DeviceEntity> deviceList = mqttCommonInterface.getDeviceList(fsuIds, baseCondition);
                List<String> deviceIds = ReflectionUtils.convertElementPropertyToList(deviceList, "deviceId");
                deviceIds.addAll(fsuIds);
               // 获取 昨天告警统计信息 ,包括多项告警统计信息 根据等级统计昨天的所有历史告警数量和告警恢复数量
                List<JSONObject> jsonObjects = mqttCommonInterface.countHomeAlarmByDeviceIds(deviceIds, start, end, baseCondition);
                logger.debug("statistic count Alarm ByDeviceIds, site reportTaskId is [{}]", reportTask.getId());
                jsonObjects.forEach(entity -> {
                    //填充站点告警统计信息
                    HomeAlarmCountTemp alarmCountTemp = new HomeAlarmCountTemp();
                    alarmCountTemp.setTime(time);
                    alarmCountTemp.setTempDate(timeDate);
                    alarmCountTemp.setAlarmLevel(entity.getString("name"));
                    alarmCountTemp.setAlarmCount(entity.getLong("count"));
                    alarmCountTemp.setStationId(stationId);
                    alarmCountTemp.setStationName(stationName);
                    alarmCountTemp.setStationType(siteType);
                    alarmCountTemp.setOperationState(operationState);
                    alarmCountTemp.setProvince(province);
                    alarmCountTemp.setMunicipality(municipality);
                    alarmCountTemp.setCounty(county);
                    alarmCountTempList.add(alarmCountTemp);
                });
            });
        } else {
            logger.error("返回站点结果为空");
        }
        String reportTaskId = reportTask.getId();
        if (!CollectionUtils.isEmpty(alarmCountTempList)) {
            homeAlarmCountTempDao.updateDelete(time, reportTaskId);
            homeAlarmCountTempDao.save(alarmCountTempList, reportTaskId);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("executorTime", "time");
        jsonObject.put("executorResult", alarmCountTempList);
        return new ReportData(DataType.TEXT, jsonObject.toJSONString());
    }

    /**
     * @Date 2020年4月8日 13:35:07
     * @Param No such property: code for class: Script1
     * @Author Mag
     * @Description 报表的查询根据执行的temp表
     **/
    private ReportData AlarmCountQuery(ReportConfig reportConfig, ReportTask reportTask) {

        String taskId = reportTask.getId();
        JSONObject condition = reportConfig.getCondition();//获取查询条件
        String statisticLevel = condition.getString("statisticLevel");
        List<JSONObject> alarmCountList = new ArrayList<>();
        TimePeriod timePeriod = condition.getObject("timePeriod", TimePeriod.class);
        if (timePeriod == null) {
            timePeriod = new TimePeriod();
            timePeriod.setEndTime(new Date(System.currentTimeMillis()));
            timePeriod.setStartTime(DateUtil.getInstance().getFirstDayOfMonth());
        }
        // 根据查询条件极其内容生成结果文件
        alarmCountList = homeAlarmCountTempDao.getCountDataByCondition(taskId, condition, timePeriod);
        if ( alarmCountList == null) alarmCountList = new ArrayList<>();
        String[][][] resultData = AlarmCountDataCreate(alarmCountList, statisticLevel);
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

    private String[][][] AlarmCountDataCreate(List<JSONObject> alarmCountList, String statisticLevel) {
        String[] tableHead = null;
        if (StringUtils.equalsAny(statisticLevel, "省级", "市级", "区县级")) {
            tableHead = new String[]{"区域层级", "告警总数"};
        } else
            tableHead = new String[]{"区域层级", "站点名称", "告警总数"};

        int colLength = tableHead.length; // 列
        int rowLength = alarmCountList.size() + 1; //行数
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
            if (colLength > 2) {
                row[1] = jsonObject.getString("stationName");
                a = 1;
            }
            row[1 + a] = jsonObject.getString("alarmCount");
        }
        return new String[][][]{sheetData};
    }

}
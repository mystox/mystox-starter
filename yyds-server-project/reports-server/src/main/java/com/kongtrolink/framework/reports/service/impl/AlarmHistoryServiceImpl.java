package com.kongtrolink.framework.reports.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.OperaCodePrefix;
import com.kongtrolink.framework.reports.api.AlarmHistoryService;
import com.kongtrolink.framework.reports.dao.AlarmHistoryTempDao;
import com.kongtrolink.framework.reports.dao.ReportTaskDao;
import com.kongtrolink.framework.reports.entity.*;
import com.kongtrolink.framework.reports.entity.alarmHistory.AlarmHistoryTemp;
import com.kongtrolink.framework.reports.service.MqttCommonInterface;
import com.kongtrolink.framework.reports.stereotype.ReportExtend;
import com.kongtrolink.framework.reports.stereotype.ReportOperaCode;
import com.kongtrolink.framework.reports.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * \* @Author: mystox
 * \* Date: 2020/4/8 13:47
 * \* Description:
 * \
 */
@Service
public class AlarmHistoryServiceImpl implements AlarmHistoryService {

    @Autowired
    ReportTaskDao reportTaskDao;
    @Autowired
    MqttCommonInterface mqttCommonInterface;

    @Autowired
    AlarmHistoryTempDao alarmHistoryTempDao;


    @Override
    @ReportOperaCode(code = OperaCodePrefix.REPORTS + "alarmHistoryReports", rhythm = 3600 * 24, dataType = {DataType.TABLE, DataType.FILE}, extend = {
//            @ReportExtend(field = "month", name = "月份", type = ReportExtend.FieldType.STRING, description = "格式为year-month"), //时间类型是否需要
            @ReportExtend(field = "statisticPeriod", name = "统计周期", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"周度报表", "月度报表", "季度报表", "年度报表"}),
//            @ReportExtend(field = "period", name = "统计周期", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, select = {"月报表", "季报表", "年报表"}),
//            @ReportExtend(field = "timePeriod", name = "时间段", type = ReportExtend.FieldType.DATE_PERIOD, belong = ExecutorType.query, description = "时间范围,返回格式为{startTime:yyyy-MM-dd,endTime:yyyy-MM-dd}"),
    })
    public ReportData alarmHistoryReports(String reportConfigStr) {
        ReportConfig reportConfig = JSONObject.parseObject(reportConfigStr, ReportConfig.class);
        ExecutorType executorType = reportConfig.getExecutorType();
        ReportTask reportTask = reportTaskDao.findByByUniqueCondition(
                reportConfig.getServerCode(), reportConfig.getEnterpriseCode(), reportConfig.getOperaCode(), reportConfig.getReportServerCode());
        if (reportTask == null)
            return new ReportData(DataType.ERROR, "report task is not exists [" + JSONObject.toJSONString(reportTask) + "]");
        if (ExecutorType.query.equals(executorType))
            return alarmReportsQuery(reportConfig, reportTask);
        else return alarmReportsExecutor(reportConfig, reportTask);

    }

    private ReportData alarmReportsExecutor(ReportConfig reportConfig, ReportTask reportTask) {
        String serverCode = reportConfig.getServerCode();
        String enterpriseCode = reportConfig.getEnterpriseCode();
        Calendar currentCalendar = Calendar.getInstance();
        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH);

        JSONObject baseCondition = new JSONObject();
        baseCondition.put("serverCode", serverCode);
        baseCondition.put("enterpriseCode", enterpriseCode);

        List<AlarmHistoryTemp> resultList = new ArrayList<>();
        Integer alarmCycle = mqttCommonInterface.getAlarmCycle(baseCondition);
        int cycleDay = 0;
        if (alarmCycle != null) {
            cycleDay = alarmCycle / 24 + 1;
        }
        JSONObject exportResult = new JSONObject();
        //获取执行时间
        int dayOfWeek = currentCalendar.get(Calendar.DAY_OF_WEEK);

        Date createTime = new Date();
        if (dayOfWeek - cycleDay == 0) {//如果判断为周初
            //获取上周的时间范围
            int weekOfYear = currentCalendar.get(Calendar.WEEK_OF_YEAR);
            Date endTime = DateUtil.getInstance().getLastDayOfWeek(weekOfYear - 1, year);
            Date startTime = DateUtil.getInstance().getFirstDayOfWeek(weekOfYear - 1, year);
            String fileName = year + "年第" + (weekOfYear - 1) + "周报表";
            baseCondition.put("fileName", fileName);
            exportResult = mqttCommonInterface.exportAlarmHistory(endTime, startTime, baseCondition);
            String uri = exportResult.getString("uri");
            AlarmHistoryTemp alarmHistoryTemp = new AlarmHistoryTemp();
            alarmHistoryTemp.setCreateTime(createTime);
            alarmHistoryTemp.setName(fileName);
            alarmHistoryTemp.setType("周度报表");
            alarmHistoryTemp.setUri(uri);
            alarmHistoryTemp.setTimePeriod(DateUtil.getInstance().format(startTime) + "至" + DateUtil.getInstance().format(endTime));
            resultList.add(alarmHistoryTemp);

        }
        int dayOfMonth = currentCalendar.get(Calendar.DAY_OF_MONTH);
        if (dayOfMonth - cycleDay == 0) {//如果判断为月初

            if ((month - 1) % 3 == 0) { //如果判断为季初
                if (month == 1) {
                    month = 12;
                    year -= 1;
                } else month -= 1;
                Date endTime = DateUtil.getInstance().getLastDayOfMonth(year, month);
                Date startTime = DateUtil.getInstance().getFirstDayOfMonth(year, month - 2);
                String fileName = year + "年第" + ((month - 1) / 3) + "季报表";
                baseCondition.put("fileName", fileName);
                exportResult = mqttCommonInterface.exportAlarmHistory(endTime, startTime, baseCondition);
                String uri = exportResult.getString("uri");
                AlarmHistoryTemp alarmHistoryTemp = new AlarmHistoryTemp();
                alarmHistoryTemp.setName(fileName);
                alarmHistoryTemp.setCreateTime(createTime);
                alarmHistoryTemp.setType("季度报表");
                alarmHistoryTemp.setUri(uri);
                alarmHistoryTemp.setTimePeriod(DateUtil.getInstance().format(startTime) + "至" + DateUtil.getInstance().format(endTime));
                resultList.add(alarmHistoryTemp);
            }
            if (month == 1) {
                month = 12;
                year -= 1;
            } else month -= 1;
            Date endTime = DateUtil.getInstance().getLastDayOfMonth(month, year);
            Date startTime = DateUtil.getInstance().getFirstDayOfMonth(month - 2, year);
            String fileName = year + "年第" + month + "月报表";
            baseCondition.put("fileName", fileName);
            exportResult = mqttCommonInterface.exportAlarmHistory(endTime, startTime, baseCondition);
            String uri = exportResult.getString("uri");
            AlarmHistoryTemp alarmHistoryTemp = new AlarmHistoryTemp();
            alarmHistoryTemp.setCreateTime(createTime);
            alarmHistoryTemp.setName(fileName);
            alarmHistoryTemp.setType("月度报表");
            alarmHistoryTemp.setUri(uri);
            alarmHistoryTemp.setTimePeriod(DateUtil.getInstance().format(startTime) + "至" + DateUtil.getInstance().format(endTime));
            resultList.add(alarmHistoryTemp);
        }


        int dayOfYear = currentCalendar.get(Calendar.DAY_OF_YEAR);
        if (dayOfYear - cycleDay == 0) {   //如果判断为年初
            Date endTime = DateUtil.getInstance().getLastDayOfYear(year - 1);
            Date startTime = DateUtil.getInstance().getFirstDayOfYear(year - 1);
            String fileName = year - 1 + "年报表";
            baseCondition.put("fileName", fileName);
            exportResult = mqttCommonInterface.exportAlarmHistory(endTime, startTime, baseCondition);
            String uri = exportResult.getString("uri");
            AlarmHistoryTemp alarmHistoryTemp = new AlarmHistoryTemp();
            alarmHistoryTemp.setCreateTime(createTime);
            alarmHistoryTemp.setName(fileName);
            alarmHistoryTemp.setType("年度报表");
            alarmHistoryTemp.setUri(uri);
            alarmHistoryTemp.setTimePeriod(DateUtil.getInstance().format(startTime) + "至" + DateUtil.getInstance().format(endTime));
            resultList.add(alarmHistoryTemp);
        }
        //查找上一次数据，并做填充
        alarmHistoryTempDao.save(resultList, reportTask.getId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("executorTime", DateUtil.getInstance().format(createTime));
        jsonObject.put("executorResult", resultList);
        return new ReportData(DataType.TEXT, jsonObject.toJSONString());
    }


    private ReportData alarmReportsQuery(ReportConfig reportConfig, ReportTask reportTask) {
        String taskId = reportTask.getId();
        JSONObject condition = reportConfig.getCondition();//获取查询条件


        List<AlarmHistoryTemp> result = alarmHistoryTempDao.findAlarmDetailsList(taskId, condition);
        String[][] resultData = alarmHistoryTempDataCreate(result);
        JsonData jsonData = new JsonData();
        jsonData.setName("告警量统计表");
        List<String[]> dataArr = Arrays.asList(resultData);
        dataArr = new ArrayList<>(dataArr);
        dataArr.remove(0);
        jsonData.setData(dataArr.toArray(new String[dataArr.size()][]));
        jsonData.setUnit("");
        jsonData.setxAxis(resultData[0]);
        ReportData reportData = new ReportData(DataType.TABLE, JSONObject.toJSONString(jsonData));
        return reportData;

    }

    private String[][] alarmHistoryTempDataCreate(List<AlarmHistoryTemp> result) {

        String[] tableHead = null;
        tableHead = new String[]{"历史告警名称", "统计时间", "报表生成时间", "操作"};

        int colLength = tableHead.length; // 列
        int rowLength = result.size() + 1; //行数
        String[][] sheetData = new String[rowLength][colLength];

        for (int i = 0; i < rowLength; i++) {
            String[] row = sheetData[i];
            if (i == 0) {
                sheetData[i] = tableHead;
                continue;
            }
            AlarmHistoryTemp alarmHistoryTemp = result.get(i - 1);
            row[0] = alarmHistoryTemp.getName();
            row[1] = alarmHistoryTemp.getTimePeriod();
            row[2] = DateUtil.getInstance().format(alarmHistoryTemp.getCreateTime());
            row[3] = alarmHistoryTemp.getUri();
        }
        return sheetData;
    }

}
package com.kongtrolink.framework.reports.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.OperaCodePrefix;
import com.kongtrolink.framework.reports.api.AlarmReportsService;
import com.kongtrolink.framework.reports.entity.DataType;
import com.kongtrolink.framework.reports.entity.ExecutorType;
import com.kongtrolink.framework.reports.entity.ReportConfig;
import com.kongtrolink.framework.reports.entity.ReportData;
import com.kongtrolink.framework.reports.stereotype.ReportExtend;
import com.kongtrolink.framework.reports.stereotype.ReportOperaCode;
import org.springframework.stereotype.Service;

/**
 * \* @Author: mystox
 * \* Date: 2020/2/13 13:32
 * \* Description:
 * \
 */
@Service
public class AlarmReportsServiceImpl implements AlarmReportsService {
    @Override
    @ReportOperaCode(code = OperaCodePrefix.REPORTS + "alarmCount", rhythm = 20, resultType = {DataType.JSON}, extend = {
            @ReportExtend(field = "date", name = "时间", type = ReportExtend.FieldType.DATE), //时间类型是否需要
            @ReportExtend(field = "region", name = "区域", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query), //区域层级
            @ReportExtend(field = "stationType", name = "站点类型", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"全部", "A级机房", "B级机房", "C级机房", "D级机房"}),
            @ReportExtend(field = "runningSate", name = "运行状态", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"全部", "交维态", "工程态", "测试态"}),
            @ReportExtend(field = "fsuManufactory", name = "fsu厂家", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"全部", "义益钛迪"}),
            @ReportExtend(field = "alarmStatus", name = "告警状态", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"活动告警", "历史告警"}),
            @ReportExtend(field = "alarmStatus", name = "告警等级", type = ReportExtend.FieldType.URI, belong = ExecutorType.query),
            @ReportExtend(field = "period", name = "统计周期", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"月报表", "季报表", "年报表"}),
    })
    public ReportData alarmCount(String reportConfigStr) {
        ReportConfig reportConfig = JSONObject.parseObject(reportConfigStr, ReportConfig.class);
        ExecutorType executorType = reportConfig.getExecutorType();
       // if (ExecutorType.query.equals(executorType))
           // return queryCount(reportConfig);
        //else return executor(reportConfig);
            return null;
    }

    @Override
    public ReportData alarmDetails(String reportConfigStr) {

        return null;
    }

    @Override
    public ReportData alarmCategory(String reportConfigStr) {
        return null;
    }
}
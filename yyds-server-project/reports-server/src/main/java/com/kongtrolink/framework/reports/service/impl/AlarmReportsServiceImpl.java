package com.kongtrolink.framework.reports.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.entity.OperaCodePrefix;
import com.kongtrolink.framework.reports.api.AlarmReportsService;
import com.kongtrolink.framework.reports.entity.DataType;
import com.kongtrolink.framework.reports.entity.ExecutorType;
import com.kongtrolink.framework.reports.entity.ReportConfig;
import com.kongtrolink.framework.reports.entity.ReportData;
import com.kongtrolink.framework.reports.entity.alarmCount.AlarmCountTemp;
import com.kongtrolink.framework.reports.stereotype.ReportExtend;
import com.kongtrolink.framework.reports.stereotype.ReportOperaCode;
import com.kongtrolink.framework.service.MqttOpera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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


    @Override
    @ReportOperaCode(code = OperaCodePrefix.REPORTS + "alarmCount", rhythm = 20, resultType = {DataType.JSON}, extend = {
            @ReportExtend(field = "month", name = "月份", type = ReportExtend.FieldType.STRING), //时间类型是否需要
            @ReportExtend(field = "region", name = "区域层级", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, value = "/proxy_ap/region/getCurrentRegion"), //区域层级
            @ReportExtend(field = "stationType", name = "站点类型", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"全部", "A级机房", "B级机房", "C级机房", "D级机房"}),
            @ReportExtend(field = "runningSate", name = "运行状态", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"全部", "交维态", "工程态", "测试态"}),
            @ReportExtend(field = "fsuManufactory", name = "fsu厂家", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"全部", "义益钛迪"}),
            @ReportExtend(field = "alarmStatus", name = "告警状态", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"历史告警"}),
            @ReportExtend(field = "alarmLevel", name = "告警等级", type = ReportExtend.FieldType.URI, belong = ExecutorType.query, choices = "/reportsOpera/getAlarmLevel"),
            @ReportExtend(field = "statisticLevel", name = "统计维度", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"省级", "市级", "县级", "站点级"}),
            @ReportExtend(field = "period", name = "统计周期", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"月报表", "季报表", "年报表"}),
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
     * 3.告警量统计表名以 t_opera_execute_temp_${taskId}为基本格式
     * 4.根据搜索条件获取站点列表，进行报表数据查询
     * 5.统计范围为serverCode_businessCode 下所有的站点
     **/
    private ReportData alarmCountExecutor(ReportConfig reportConfig) {
        JSONObject condition = reportConfig.getCondition();
        String serverCode = reportConfig.getServerCode();
        String enterpriseCode = reportConfig.getEnterpriseCode();
        List<AlarmCountTemp> alarmCountTempList = new ArrayList<AlarmCountTemp>();

        //获取时间信息
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);

        if (month == 1) {
            month = 12;
        } else month -= 1;


        JSONObject msg = new JSONObject();
        msg.put("serverCode", serverCode);
        msg.put("enterpriseCode", enterpriseCode);
        //todo 获取企业在该服务下所有站点
        MsgResult siteListResult = mqttOpera.opera("getStationListByEnterpriseCodeAndServerCode", msg.toJSONString());
        String siteListMsg = siteListResult.getMsg();
        List<JSONObject> siteList = JSONArray.parseArray(siteListMsg,JSONObject.class);
        if (!CollectionUtils.isEmpty(siteList)) {
            siteList.forEach(s->{
                //todo 获取站点下所有fsu及其相关告警数据

                String stationId = s.getString("siteId");
                MsgResult getFsuListByStationId = mqttOpera.opera("getFsuListByStationId", stationId);
                //todo 获取设备id
                String fsuId = "";
                MsgResult getDeviceListByFsuId = mqttOpera.opera("getDeviceListByFsuId", fsuId);

                List<String> deviceList = new ArrayList<>();
                //todo 获取上月告警统计信息 ,包括多项告警统计信息 根据等级统计上个月内的所有历史告警数量和告警恢复数量
                MsgResult getAlarmCountByDeviceIdList = mqttOpera.opera("getAlarmCountByDeviceIdListLastMonth", JSONArray.toJSONString(deviceList));

                AlarmCountTemp alarmCountTemp = new AlarmCountTemp();
                //todo 填充站点信息
                alarmCountTempList.add(alarmCountTemp);
            });
        }



        return null;
    }

    /**
     * @return com.kongtrolink.framework.reports.entity.ReportData
     * @Date 18:38 2020/2/23
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description //报表的查询根据执行的temp表
     **/
    private ReportData AlarmCountQuery(ReportConfig reportConfig) {
        JSONObject condition = reportConfig.getCondition();//获取查询条件

        JSONArray regionArr = condition.getJSONArray("region");
        String stationType = condition.getString("stationType");
        String runningSate = condition.getString("runningSate");
        String fsuManufactory = condition.getString("fsuManufactory");



        //todo 根据用户的区域权限及其搜索条件获取站点列表
        JSONObject msg = new JSONObject();
        MsgResult getStationList = mqttOpera.opera("getStationList", condition.toJSONString());

        //根据查询条件极其内容生成结果文件

        String excelUri = AlarmCountExcelCreate();
        
        
        
        long currentTime = System.currentTimeMillis();
        String s = "http://${host}/${reportsRoute}/reportsResources/report_alarmCount/告警分类统计表_" + currentTime + ".xls";
        ReportData reportData = new ReportData(DataType.FORM, s);
        return reportData;
    }

    private String AlarmCountExcelCreate() {
        long currentTime = System.currentTimeMillis();
        String s = "http://${host}/${reportsRoute}/reportsResources/report_alarmCount/告警分类统计表_" + currentTime + ".xls";
        return s;
    }

    @Override
    @ReportOperaCode(code = OperaCodePrefix.REPORTS + "alarmDetails", rhythm = 20, resultType = {DataType.JSON}, extend = {
            //@ReportExtend(field = "date", name = "时间", type = ReportExtend.FieldType.DATE), //时间类型是否需要
            @ReportExtend(field = "region", name = "区域层级", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, value = "/reportsOpera/getRegionCodeList"), //区域层级
            @ReportExtend(field = "stationType", name = "站点类型", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"全部", "A级机房", "B级机房", "C级机房", "D级机房"}),
            @ReportExtend(field = "runningSate", name = "运行状态", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"全部", "交维态", "工程态", "测试态"}),
            @ReportExtend(field = "fsuManufactory", name = "fsu厂家", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"全部", "义益钛迪"}),
            @ReportExtend(field = "alarmStatus", name = "告警状态", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"活动告警", "历史告警"}),
            @ReportExtend(field = "alarmLevel", name = "告警等级", type = ReportExtend.FieldType.URI, belong = ExecutorType.query, value = "/reportsOpera/getAlarmLevel"),
            @ReportExtend(field = "statisticLevel", name = "统计维度", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"省级", "市级", "县级", "站点级"}),
            @ReportExtend(field = "period", name = "统计周期", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"月报表", "季报表", "年报表"}),
            @ReportExtend(field = "timePeriod", name = "时间段", type = ReportExtend.FieldType.DATE, belong = ExecutorType.query),
    })
    public ReportData alarmDetails(String reportConfigStr) {

        return null;
    }

    @Override
    @ReportOperaCode(code = OperaCodePrefix.REPORTS + "alarmCategory", rhythm = 20, resultType = {DataType.JSON}, extend = {
            //@ReportExtend(field = "date", name = "时间", type = ReportExtend.FieldType.DATE), //时间类型是否需要
            @ReportExtend(field = "region", name = "区域层级", type = ReportExtend.FieldType.DISTRICT, belong = ExecutorType.query, value = "/reportsOpera/getRegionCodeList"), //区域层级
            @ReportExtend(field = "stationType", name = "站点类型", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"全部", "A级机房", "B级机房", "C级机房", "D级机房"}),
            @ReportExtend(field = "runningSate", name = "运行状态", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"全部", "交维态", "工程态", "测试态"}),
            @ReportExtend(field = "fsuManufactory", name = "fsu厂家", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"全部", "义益钛迪"}),
            @ReportExtend(field = "alarmStatus", name = "告警状态", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"活动告警", "历史告警"}),
            @ReportExtend(field = "alarmLevel", name = "告警等级", type = ReportExtend.FieldType.URI, belong = ExecutorType.query, value = "/reportsOpera/getAlarmLevel"),
            @ReportExtend(field = "statisticLevel", name = "统计维度", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"省级", "市级", "县级", "站点级"}),
            @ReportExtend(field = "period", name = "统计周期", type = ReportExtend.FieldType.STRING, belong = ExecutorType.query, choices = {"月报表", "季报表", "年报表"}),
            @ReportExtend(field = "timePeriod", name = "时间段", type = ReportExtend.FieldType.DATE, belong = ExecutorType.query),
    })
    public ReportData alarmCategory(String reportConfigStr) {
        return null;
    }
}
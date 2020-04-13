package com.kongtrolink.framework.reports.service;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.reports.entity.query.DeviceEntity;
import com.kongtrolink.framework.reports.entity.query.FsuEntity;
import com.kongtrolink.framework.reports.entity.query.SiteEntity;

import java.util.Date;
import java.util.List;

/**
 * \* @Author: mystox
 * \* Date: 2020/3/9 16:16
 * \* Description:
 * \
 */
public interface MqttCommonInterface {


    List<FsuEntity> getFsuList(String stationId, JSONObject baseCondition);

    List<DeviceEntity> getDeviceList(List<String> fsuIds, JSONObject baseCondition);

    List<JSONObject> countAlarmByDeviceIds(List<String> deviceIds, int finalYear, int finalMonth, JSONObject baseCondition);

    List<JSONObject> countHomeAlarmByDeviceIds(List<String> deviceIds, Date start, Date end, JSONObject baseCondition);

    List<SiteEntity> getSiteList(JSONObject baseCondition);

    String getRegionName(String address);

    List<JSONObject> getAlarmDetails(List<String> deviceIds, int finalYear, int finalMonth, JSONObject baseCondition);

    List<JSONObject> getAlarmCategoryByDeviceIds(List<String> deviceIds, int finalYear, int finalMonth, JSONObject baseCondition);

    JSONObject statisticFsuOfflineData(List<String> fsuIds, int finalYear, int finalMonth, JSONObject baseCondition);

    List<JSONObject> getFsuOfflineDetails(List<String> fsuIds, int finalYear, int finalMonth, JSONObject baseCondition);

    JSONObject stationOffStatistic(List<String> deviceIds, int finalYear, int finalMonth, JSONObject baseCondition);

    List<JSONObject> getStationOffDetails(List<String> fsuIds, int finalYear, int finalMonth, JSONObject baseCondition);

    JSONObject getStationBreakStatistic(List<String> fsuIds, int finalYear, int finalMonth, JSONObject baseCondition);

    JSONObject getStationElectricCountList(List<String> fsuIds, int finalYear, int finalMonth, JSONObject baseCondition);

    List<String> getAlarmLevel(JSONObject query);

    Integer getAlarmCycle(JSONObject baseCondition);

    List<JSONObject> getAlarmCurrentDetails(List<String> deviceIds, int finalYear, int finalMonth, JSONObject baseCondition);

    List<JSONObject> getAlarmCurrentCategoryByDeviceIds(List<String> deviceIds, int finalYear, int finalMonth, JSONObject baseCondition);

    /**
     * @Date 16:26 2020/4/8
     * @Param No such property: code for class: Script1
     * @return com.alibaba.fastjson.JSONObject
     * @Author mystox
     * @Description //告警服务生成历史报表
     **/
    JSONObject exportAlarmHistory(Date endTime, Date startTime, JSONObject baseCondition);


    List<JSONObject> getCurrentStationList(JSONObject query);
}
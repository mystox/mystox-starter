package com.kongtrolink.framework.reports.service;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.reports.entity.query.DeviceEntity;
import com.kongtrolink.framework.reports.entity.query.FsuEntity;
import com.kongtrolink.framework.reports.entity.query.SiteEntity;

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

    List<SiteEntity> getSiteList(JSONObject baseCondition);

    String getRegionName(String address);
}
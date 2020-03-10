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

    List<SiteEntity> getSiteList(String enterpriseCode, String serverCode);

    List<FsuEntity> getFsuList(String stationId, String enterpriseCode,String serverCode);

    List<DeviceEntity> getDeviceList(List<String> fsuId,String enterpriseCode,String serverCode);

    List<JSONObject> countAlarmByDeviceIds(List<String> deviceIds);
}
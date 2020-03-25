package com.kongtrolink.framework.mqtt;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.enttiy.EnterpriseLevel;
import com.kongtrolink.framework.stereotype.OperaCode;
import com.kongtrolink.framework.stereotype.Register;

import java.util.List;

/**
 * \* @Author: mystox
 * \* Date: 2020/3/2 13:19
 * \* Description:
 * \
 */
@Register
public interface ReportsInterface {

    @OperaCode
    public List<JSONObject> getAlarmCountByDeviceIdList(String msg);


    @OperaCode(description = "告警明细统计")
    public List<JSONObject> getAlarmsByDeviceList(String msg);


    @OperaCode(description = "告警分类统计")
    public List<JSONObject> getAlarmCategoryByDeviceIdList(String msg);


    @OperaCode(description = "FSU离线告警统计")
    public JSONObject getFsuOfflineStatistic(String msg);


    @OperaCode(description = "fsu离线明细")
    public List<JSONObject> getFsuOfflineDetails(String msg);

    @OperaCode(description = "站点停电统计表")
    public JSONObject stationOffStatistic(String msg);


    @OperaCode(description = "站点停电明细表")
    public List<JSONObject> getStationOffDetails(String msg);


    @OperaCode(description = "站点停电断站统计表")
    public JSONObject getStationBreakStatistic(String msg);


    @OperaCode(description = "获取企业下启用的告警等级分类数据")
    public List<EnterpriseLevel> getAlarmLevel(String body);

}
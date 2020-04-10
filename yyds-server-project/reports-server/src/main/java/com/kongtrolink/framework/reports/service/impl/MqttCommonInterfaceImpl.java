package com.kongtrolink.framework.reports.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.utils.ReflectionUtils;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.entity.StateCode;
import com.kongtrolink.framework.reports.entity.query.*;
import com.kongtrolink.framework.reports.service.MqttCommonInterface;
import com.kongtrolink.framework.reports.utils.DateUtil;
import com.kongtrolink.framework.service.MqttOpera;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * \* @Author: mystox
 * \* Date: 2020/3/9 16:16
 * \* Description:
 * \
 */
@Service
public class MqttCommonInterfaceImpl implements MqttCommonInterface {
    Logger logger = LoggerFactory.getLogger(MqttCommonInterfaceImpl.class);
    //获取CI
    public static final String GET_CI_SCLOUD = "getCISCloud";
    public static final String GET_FSU_SCLOUD = "getFsuSCloud";
    @Autowired
    MqttOpera mqttOpera;

    @Override
    public List<SiteEntity> getSiteList(JSONObject baseCondition) {
        BasicSiteQuery basicSiteQuery = new BasicSiteQuery();
        basicSiteQuery.setServerCode(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, baseCondition.getString("serverCode")));
        basicSiteQuery.setEnterpriseCode(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, baseCondition.getString("enterpriseCode")));
        basicSiteQuery.setType(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, AssetTypeConstant.ASSET_TYPE_SITE));
        MsgResult opera = mqttOpera.opera(GET_CI_SCLOUD, JSON.toJSONString(basicSiteQuery));
        int stateCode = opera.getStateCode();
        if (StateCode.SUCCESS == stateCode) {
            List<SiteEntity> siteEntities = new ArrayList<>();
            CIResponseEntity responseEntity = JSONObject.parseObject(opera.getMsg(), CIResponseEntity.class);
            if (CommonConstant.SUCCESSFUL == responseEntity.getResult()) {
                int count = responseEntity.getCount();
                List<JSONObject> infos = responseEntity.getInfos();
                //TODO 分页判断暂时不考虑，滞后待大数据修改实现
                siteEntities = JSONArray.parseArray(JSONArray.toJSONString(infos), SiteEntity.class);
                return siteEntities;
            } else {
                logger.error("get site list result error");
            }
        } else {
            logger.error("get site list error[mqtt]");
        }
        return null;
    }

    @Override
    public String getRegionName(String address) {

        MsgResult opera = mqttOpera.opera("getRegionCodeEntity", JSON.toJSONString(new String[]{address}));
        int stateCode = opera.getStateCode();
        if (StateCode.SUCCESS == stateCode) {
            List<JSONObject> jsonArray = JSONObject.parseArray(opera.getMsg(), JSONObject.class);
            JSONObject jsonObject = jsonArray.get(0);
            String name = jsonObject.getString("name");
            return name;
        }
        return null;
    }

    @Override
    public List<JSONObject> getAlarmDetails(List<String> deviceIds, int finalYear, int finalMonth, JSONObject baseCondition) {
        JSONObject alarmCountCondition = new JSONObject();
        alarmCountCondition.putAll(baseCondition);
        alarmCountCondition.put("deviceIds", deviceIds);
        alarmCountCondition.put("startBeginTime", DateUtil.getInstance().getFirstDayOfMonth(finalYear, finalMonth));
        alarmCountCondition.put("startEndTime", DateUtil.getInstance().getLastDayOfMonth(finalYear, finalMonth));
        MsgResult opera = mqttOpera.opera("getAlarmsByDeviceIdList", baseCondition.toJSONString());
        int stateCode = opera.getStateCode();
        if (StateCode.SUCCESS == stateCode) {
            return JSONObject.parseArray(opera.getMsg(), JSONObject.class);
        } else {
            logger.error("get alarm details list error[mqtt]");
        }
        return null;
    }
    @Override
    public List<JSONObject> getAlarmCurrentDetails(List<String> deviceIds, int finalYear, int finalMonth, JSONObject baseCondition) {
        JSONObject alarmCountCondition = new JSONObject();
        alarmCountCondition.putAll(baseCondition);
        alarmCountCondition.put("deviceIds", deviceIds);
        MsgResult opera = mqttOpera.opera("getCurrentAlarmsByDeviceIdList", baseCondition.toJSONString());
        int stateCode = opera.getStateCode();
        if (StateCode.SUCCESS == stateCode) {
            return JSONObject.parseArray(opera.getMsg(), JSONObject.class);
        } else {
            logger.error("get alarm details list error[mqtt]");
        }
        return null;
    }

    @Override
    public List<JSONObject> getAlarmCurrentCategoryByDeviceIds(List<String> deviceIds, int finalYear, int finalMonth, JSONObject baseCondition) {
        JSONObject alarmCountCondition = new JSONObject();
        alarmCountCondition.putAll(baseCondition);
        alarmCountCondition.put("deviceIds", deviceIds);
        MsgResult opera = mqttOpera.opera("getAlarmCurrentCategoryByDeviceIdList", alarmCountCondition.toJSONString(), 2, 3600L * 2, TimeUnit.SECONDS);
        String alarmCategoryListMsg = opera.getMsg();
        int stateCode = opera.getStateCode();
        if (StateCode.SUCCESS == stateCode) {
            return JSONObject.parseArray(alarmCategoryListMsg, JSONObject.class);
        } else {
            logger.error("get alarm category list error[mqtt]");
        }
        return null;
    }

    @Override
    public JSONObject exportAlarmHistory(Date endTime, Date startTime, JSONObject baseCondition) {
        JSONObject alarmCountCondition = new JSONObject();
        alarmCountCondition.putAll(baseCondition);
        alarmCountCondition.put("startBeginTime", startTime);
        alarmCountCondition.put("startEndTime", endTime);
        MsgResult opera = mqttOpera.opera("exportAlarmHistory", alarmCountCondition.toJSONString(), 2, 3600L * 2, TimeUnit.SECONDS);
        String alarmCategoryListMsg = opera.getMsg();
        int stateCode = opera.getStateCode();
        if (StateCode.SUCCESS == stateCode) {
            return JSONObject.parseObject(alarmCategoryListMsg, JSONObject.class);
        } else {
            logger.error("get alarm category list error[mqtt]");
        }
        return null;

    }

    @Override
    public List<JSONObject> getAlarmCategoryByDeviceIds(List<String> deviceIds, int finalYear, int finalMonth, JSONObject baseCondition) {
        JSONObject alarmCountCondition = new JSONObject();
        alarmCountCondition.putAll(baseCondition);
        alarmCountCondition.put("deviceIds", deviceIds);
        alarmCountCondition.put("startBeginTime", DateUtil.getInstance().getFirstDayOfMonth(finalYear, finalMonth));
        alarmCountCondition.put("startEndTime", DateUtil.getInstance().getLastDayOfMonth(finalYear, finalMonth));
        MsgResult opera = mqttOpera.opera("getAlarmCategoryByDeviceIdList", alarmCountCondition.toJSONString(), 2, 3600L * 2, TimeUnit.SECONDS);
        String alarmCategoryListMsg = opera.getMsg();
        int stateCode = opera.getStateCode();
        if (StateCode.SUCCESS == stateCode) {
            return JSONObject.parseArray(alarmCategoryListMsg, JSONObject.class);
        } else {
            logger.error("get alarm category list error[mqtt]");
        }
        return null;

    }

    /**
     * @return java.util.List<com.alibaba.fastjson.JSONObject>
     * @Date 15:04 2020/3/18
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description //fsu离线告警统计表
     **/
    @Override
    public JSONObject statisticFsuOfflineData(List<String> fsuIds, int finalYear, int finalMonth, JSONObject baseCondition) {
        JSONObject alarmCountCondition = new JSONObject();
        alarmCountCondition.putAll(baseCondition);
        alarmCountCondition.put("deviceIds", fsuIds);
        alarmCountCondition.put("startBeginTime", DateUtil.getInstance().getFirstDayOfMonth(finalYear, finalMonth));
        alarmCountCondition.put("startEndTime", DateUtil.getInstance().getLastDayOfMonth(finalYear, finalMonth));
        MsgResult opera = mqttOpera.opera("getFsuOfflineStatistic", alarmCountCondition.toJSONString(), 2, 3600L * 2, TimeUnit.SECONDS);
        int stateCode = opera.getStateCode();
        if (StateCode.SUCCESS == stateCode) {
            return JSONObject.parseObject(opera.getMsg());
        } else {
            logger.error("get fsu offline alarm statistic list error[mqtt]");
        }
        return null;
    }

    /**
     * @return com.alibaba.fastjson.JSONObject
     * @Date 15:46 2020/3/19
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description //fsu离线明细表
     **/
    @Override
    public List<JSONObject> getFsuOfflineDetails(List<String> fsuIds, int finalYear, int finalMonth, JSONObject baseCondition) {
        JSONObject alarmCountCondition = new JSONObject();
        alarmCountCondition.putAll(baseCondition);
        alarmCountCondition.put("deviceIds", fsuIds);
        alarmCountCondition.put("startBeginTime", DateUtil.getInstance().getFirstDayOfMonth(finalYear, finalMonth));
        alarmCountCondition.put("startEndTime", DateUtil.getInstance().getLastDayOfMonth(finalYear, finalMonth));
        MsgResult opera = mqttOpera.opera("getFsuOfflineDetails", alarmCountCondition.toJSONString(), 2, 3600L * 2, TimeUnit.SECONDS);
        int stateCode = opera.getStateCode();
        if (StateCode.SUCCESS == stateCode) {
            return JSONObject.parseArray(opera.getMsg(), JSONObject.class);
        } else {
            logger.error("get fsu offline alarm statistic list error[mqtt]");
        }
        return null;
    }

    @Override
    public JSONObject stationOffStatistic(List<String> deviceIds, int finalYear, int finalMonth, JSONObject baseCondition) {
        JSONObject alarmCondition = new JSONObject();
        alarmCondition.putAll(baseCondition);
        alarmCondition.put("deviceIds", deviceIds);
        alarmCondition.put("startBeginTime", DateUtil.getInstance().getFirstDayOfMonth(finalYear, finalMonth));
        alarmCondition.put("startEndTime", DateUtil.getInstance().getLastDayOfMonth(finalYear, finalMonth));
        MsgResult opera = mqttOpera.opera("stationOffStatistic", alarmCondition.toJSONString(), 2, 3600L * 2, TimeUnit.SECONDS);
        int stateCode = opera.getStateCode();
        if (StateCode.SUCCESS == stateCode) {
            return JSONObject.parseObject(opera.getMsg());
        } else {
            logger.error("get fsu offline alarm statistic list error[mqtt]");
        }
        return null;

    }

    @Override
    public List<JSONObject> getStationOffDetails(List<String> fsuIds, int finalYear, int finalMonth, JSONObject baseCondition) {
        JSONObject alarmCountCondition = new JSONObject();
        alarmCountCondition.putAll(baseCondition);
        alarmCountCondition.put("deviceIds", fsuIds);
        alarmCountCondition.put("startBeginTime", DateUtil.getInstance().getFirstDayOfMonth(finalYear, finalMonth));
        alarmCountCondition.put("startEndTime", DateUtil.getInstance().getLastDayOfMonth(finalYear, finalMonth));
        MsgResult opera = mqttOpera.opera("getStationOffDetails", alarmCountCondition.toJSONString(), 2, 3600L * 2, TimeUnit.SECONDS);
        int stateCode = opera.getStateCode();
        if (StateCode.SUCCESS == stateCode) {
            return JSONObject.parseArray(opera.getMsg(), JSONObject.class);
        } else {
            logger.error("get station off details list error[mqtt]");
        }
        return null;
    }

    @Override
    public JSONObject getStationBreakStatistic(List<String> fsuIds, int finalYear, int finalMonth, JSONObject baseCondition) {
        JSONObject countCondition = new JSONObject();
        countCondition.putAll(baseCondition);
        countCondition.put("deviceIds", fsuIds);
        countCondition.put("startBeginTime", DateUtil.getInstance().getFirstDayOfMonth(finalYear, finalMonth));
        countCondition.put("startEndTime", DateUtil.getInstance().getLastDayOfMonth(finalYear, finalMonth));
        MsgResult opera = mqttOpera.opera("getStationBreakStatistic", countCondition.toJSONString(), 2, 3600L * 2, TimeUnit.SECONDS);
        int stateCode = opera.getStateCode();
        if (StateCode.SUCCESS == stateCode) {
            return JSONObject.parseObject(opera.getMsg(), JSONObject.class);
        } else {
            logger.error("get station break statistic list error[mqtt]");
        }
        return null;
    }

    @Override
    public JSONObject getStationElectricCountList(List<String> fsuIds, int finalYear, int finalMonth, JSONObject baseCondition) {
        JSONObject countCondition = new JSONObject();
        countCondition.putAll(baseCondition);
        countCondition.put("deviceIds", fsuIds);
        countCondition.put("startBeginTime", DateUtil.getInstance().getFirstDayOfMonth(finalYear, finalMonth));
        countCondition.put("startEndTime", DateUtil.getInstance().getLastDayOfMonth(finalYear, finalMonth));
        MsgResult opera = mqttOpera.opera("stationElectricCountList", countCondition.toJSONString(), 2, 3600L * 2, TimeUnit.SECONDS);
        int stateCode = opera.getStateCode();
        if (StateCode.SUCCESS == stateCode) {
            return JSONObject.parseObject(opera.getMsg(), JSONObject.class);
        } else {
            logger.error("get station break statistic list error[mqtt]");
        }
        return null;
    }

    @Override
    public List<String> getAlarmLevel(JSONObject query) {
        MsgResult opera = mqttOpera.opera("getAlarmLevel",query.toJSONString());
        int stateCode = opera.getStateCode();
        if (StateCode.SUCCESS == stateCode) {
            List<Level> ts = JSON.parseArray(opera.getMsg(), Level.class);
            return ReflectionUtils.convertElementPropertyToList(ts, "levelName");
        } else {
            logger.error("get station break statistic list error[mqtt]");
        }
        return null;
    }

    @Override
    public Integer getAlarmCycle(JSONObject baseCondition) {
        MsgResult opera = mqttOpera.opera("getAlarmCycle",baseCondition.toJSONString());
        int stateCode = opera.getStateCode();
        if (StateCode.SUCCESS == stateCode) {
            JSONObject jsonObject = JSON.parseObject(opera.getMsg());
            Integer diffTime = jsonObject.getInteger("diffTime");
            return diffTime;
        } else {
            logger.error("get station break statistic list error[mqtt]");
        }
        return null;

    }


    @Override
    public List<FsuEntity> getFsuList(String stationId, JSONObject baseCondition) {
        baseCondition.put("stationId", stationId);
        MsgResult opera = mqttOpera.opera(GET_FSU_SCLOUD, baseCondition.toJSONString());
        int stateCode = opera.getStateCode();
        if (StateCode.SUCCESS == stateCode) {
            return JSONObject.parseArray(opera.getMsg(), FsuEntity.class);
        } else {
            logger.error("get fsu list error[mqtt]");
        }
        return null;
    }

    @Override
    public List<DeviceEntity> getDeviceList(List<String> fsuIds, JSONObject baseCondition) {
        BasicDeviceQuery basicDeviceQuery = new BasicDeviceQuery();
        basicDeviceQuery.setServerCode(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, baseCondition.getString("serverCode")));
        basicDeviceQuery.setEnterpriseCode(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, baseCondition.getString("enterpriseCode")));
        String deviceType = baseCondition.getString("deviceType");
        if (StringUtils.isNotBlank(deviceType))
            basicDeviceQuery.setType(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, deviceType));
        BasicParentQuery basicParentQuery = new BasicParentQuery();
        basicParentQuery.setSn(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_IN, fsuIds));
        basicDeviceQuery.set_parent(basicParentQuery);
        MsgResult opera = mqttOpera.opera(GET_CI_SCLOUD, JSON.toJSONString(basicDeviceQuery));
        int stateCode = opera.getStateCode();
        if (StateCode.SUCCESS == stateCode) {
            List<DeviceEntity> siteEntities = new ArrayList<>();
            CIResponseEntity responseEntity = JSONObject.parseObject(opera.getMsg(), CIResponseEntity.class);
            if (CommonConstant.SUCCESSFUL == responseEntity.getResult()) {
                int count = responseEntity.getCount();
                List<JSONObject> infos = responseEntity.getInfos();
                //TODO 分页判断暂时不考虑，滞后待大数据修改实现
                siteEntities = JSONArray.parseArray(JSONArray.toJSONString(infos), DeviceEntity.class);
                return siteEntities;
            } else {
                logger.error("get device list result error");
            }
        } else {
            logger.error("get device list error[mqtt]");
        }
        return null;
    }

    @Override
    public List<JSONObject> countAlarmByDeviceIds(List<String> deviceIds, int finalYear, int finalMonth, JSONObject baseCondition) {
        JSONObject alarmCountCondition = new JSONObject();
        alarmCountCondition.putAll(baseCondition);
        alarmCountCondition.put("deviceIds", deviceIds);
        alarmCountCondition.put("startBeginTime", DateUtil.getInstance().getFirstDayOfMonth(finalYear, finalMonth));
        alarmCountCondition.put("startEndTime", DateUtil.getInstance().getLastDayOfMonth(finalYear, finalMonth));
        MsgResult opera = mqttOpera.opera("getAlarmCountByDeviceIdList", alarmCountCondition.toJSONString(), 2, 3600L * 2, TimeUnit.SECONDS);
        String alarmCountListMsg = opera.getMsg();
        int stateCode = opera.getStateCode();
        if (StateCode.SUCCESS == stateCode) {
            return JSONObject.parseArray(alarmCountListMsg, JSONObject.class);
        } else {
            logger.error("get alarm count list error[mqtt]");
        }
        return null;
    }

    @Override
    public List<JSONObject> countHomeAlarmByDeviceIds(List<String> deviceIds, Date start, Date end, JSONObject baseCondition) {
        JSONObject alarmCountCondition = new JSONObject();
        alarmCountCondition.putAll(baseCondition);
        alarmCountCondition.put("deviceIds", deviceIds);
        alarmCountCondition.put("startBeginTime", start);
        alarmCountCondition.put("startEndTime", end);
        MsgResult opera = mqttOpera.opera("getAlarmCountByDeviceIdList", alarmCountCondition.toJSONString(), 2, 3600L * 2, TimeUnit.SECONDS);
        String alarmCountListMsg = opera.getMsg();
        int stateCode = opera.getStateCode();
        if (StateCode.SUCCESS == stateCode) {
            return JSONObject.parseArray(alarmCountListMsg, JSONObject.class);
        } else {
            logger.error("get alarm count list error[mqtt]");
        }
        return null;
    }

}
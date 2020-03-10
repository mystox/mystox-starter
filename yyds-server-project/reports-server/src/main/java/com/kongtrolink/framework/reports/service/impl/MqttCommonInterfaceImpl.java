package com.kongtrolink.framework.reports.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.entity.StateCode;
import com.kongtrolink.framework.reports.entity.query.*;
import com.kongtrolink.framework.reports.service.MqttCommonInterface;
import com.kongtrolink.framework.service.MqttOpera;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    public List<SiteEntity> getSiteList(String enterpriseCode, String serverCode) {
        BasicSiteQuery basicSiteQuery = new BasicSiteQuery();
        basicSiteQuery.setServerCode(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, serverCode));
        basicSiteQuery.setEnterpriseCode(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, enterpriseCode));
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
    public List<FsuEntity> getFsuList(String stationId, String enterpriseCode, String serverCode) {
        JSONObject fsuQuery = new JSONObject();
        fsuQuery.put("stationId", stationId);
        fsuQuery.put("enterpriseCode", enterpriseCode);
        fsuQuery.put("serverCode", serverCode);

        MsgResult opera = mqttOpera.opera(GET_FSU_SCLOUD, fsuQuery.toJSONString());
        int stateCode = opera.getStateCode();
        if (StateCode.SUCCESS == stateCode) {
            return JSONObject.parseArray(opera.getMsg(), FsuEntity.class);
        } else {
            logger.error("get fsu list error[mqtt]");
        }
        return null;
    }

    @Override
    public List<DeviceEntity> getDeviceList(List<String> fsuIds, String enterpriseCode, String serverCode) {
//        Bas basicDeviceQuery = new BasicSiteQuery();
//        basicDeviceQuery.setServerCode(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, serverCode));
//        basicDeviceQuery.setEnterpriseCode(new BasicCommonQuery(CommonConstant.SEARCH_TYPE_EXACT, enterpriseCode));
//        basicDeviceQuery.set
//        MsgResult opera = mqttOpera.opera(GET_CI_SCLOUD, JSON.toJSONString(basicDeviceQuery));
//        int stateCode = opera.getStateCode();
//        if (StateCode.SUCCESS == stateCode) {
//            return JSONObject.parseArray(opera.getMsg(), FsuEntity.class);
//        } else {
//            logger.error("get fsu list error[mqtt]");
//        }
        return null;
    }

    @Override
    public List<JSONObject> countAlarmByDeviceIds(List<String> deviceIds) {
        MsgResult getAlarmCountByDeviceIdList = mqttOpera.opera("getAlarmCountByDeviceIdListLastMonth", JSONArray.toJSONString(deviceIds));

        String alarmCountListMsg = getAlarmCountByDeviceIdList.getMsg();
        return null;
    }

}
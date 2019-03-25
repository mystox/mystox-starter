package com.kongtrolink.framework.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.ControllerInstance;
import com.kongtrolink.framework.core.entity.Fsu;
import com.kongtrolink.framework.core.entity.PktType;
import com.kongtrolink.framework.core.entity.YwclMessage;
import com.kongtrolink.framework.dao.FsuDao;
import com.kongtrolink.framework.dao.FsuDevicesDao;
import com.kongtrolink.framework.dao.OperatorHistoryDao;
import com.kongtrolink.framework.model.OperatHistory;
import com.kongtrolink.framework.mqtt.base.MqttRequestHelper;
import com.kongtrolink.framework.service.FsuService;
import com.kongtrolink.framework.util.LocationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * \* @Author: mystox
 * \* Date: 2018/12/1 10:46
 * \* Description:
 * \
 */
@Service
public class FsuServiceImpl implements FsuService
{
    @Value("${mqtt.sub.ywcl.topicId}")
    private String omcTopic;
    @Value("${mqtt.timeout}")
    private int mqttTimeout;
    @Autowired
    FsuDao fsuDao;
    @Autowired
    FsuDevicesDao fsuDevicesDao;
    @Autowired
    MqttRequestHelper mqttRequestHelper;

    @Autowired
    OperatorHistoryDao operatorHistoryDao;

    @Override
    public JSONObject setFsu(Map<String, Object> requestBody)
    {
//        if (fsu == null) fsu = new Fsu();
        if (requestBody == null) return null;
        String fsuId = (String) requestBody.get("fsuId");
//        Fsu fsu = fsuDao.findByFsuId(fsuId);
        String fsuCode = (String) requestBody.get("fsu_id");

        if (fsuCode != null)
        {
            String code = fsuCode;
            String tierCode = StringUtils.substring(code, 0, 6);
            if (StringUtils.isNotBlank(tierCode) && StringUtils.length(tierCode) >= 2)
            {
                StringBuilder sb = new StringBuilder();
                Map<String, String> tierMap = ControllerInstance.getInstance().getTierMap();
                String firstTier = StringUtils.substring(tierCode, 0, 2);
                sb.append(tierMap.get(firstTier));
                if (StringUtils.length(tierCode) >= 4)
                {
                    String secondTier = StringUtils.substring(tierCode, 0, 4);
                    String secondTierName = tierMap.get(secondTier);
                    if (StringUtils.isNotBlank(secondTierName))
                    {
                        sb.append("-");
                        sb.append(tierMap.get(secondTier));
                        if (StringUtils.length(tierCode) >= 6)
                        {

                            String tierName = tierMap.get(tierCode);
                            if (StringUtils.isNotBlank(tierName))
                            {
                                sb.append("-");
                                sb.append(tierName);
                            }
                        }
                    }
                }
                requestBody.put("tierName", sb.toString());
            }
        }
//        BeanUtils.copyProperties(requestBody, fsu);
//        fsuDao.saveFsu(fsu);

        YwclMessage ywclMessage = new YwclMessage(PktType.SET_STATION, System.currentTimeMillis(), fsuId);
        ywclMessage.setData(JSON.toJSONString(requestBody));
        JSONObject result = mqttRequestHelper.syncRequestData(ywclMessage, omcTopic, JSONObject.class, mqttTimeout);
        return result;
    }


    @Override
    public JSONObject upgrade(Map<String, Object> requestBody, String fsuId)
    {
        if (requestBody == null) return null;
        YwclMessage ywclMessage = new YwclMessage(PktType.UPDATE, System.currentTimeMillis(), fsuId);
        ywclMessage.setData(JSON.toJSONString(requestBody));
        JSONObject result = mqttRequestHelper.syncRequestData(ywclMessage, omcTopic, JSONObject.class, 300000);
        return result;
    }

    @Override
    public JSONObject compiler(Map<String, Object> requestBody)
    {
        if (requestBody == null) return null;
        String url = (String) requestBody.get("url");
        YwclMessage ywclMessage = new YwclMessage(PktType.COMPILER, System.currentTimeMillis(), "mqttTimeout");
        ywclMessage.setData(url);
        JSONObject result = mqttRequestHelper.syncRequestData(ywclMessage, omcTopic, JSONObject.class, mqttTimeout);
        return result;
        //TODO 编译
    }

    @Override
    public JSONObject getDeviceList(Map<String, Object> requestBody, String fsuId)
    {
        if (requestBody == null) return null;
        YwclMessage ywclMessage = new YwclMessage(PktType.GET_DEVICES, System.currentTimeMillis(), fsuId);
        ywclMessage.setData(JSON.toJSONString(requestBody));
        JSONObject result = mqttRequestHelper.syncRequestData(ywclMessage, omcTopic, JSONObject.class, mqttTimeout);
        return result;
    }

    @Override
    public JSONObject deleteDevice(Map<String, Object> requestBody)
    {
        if (requestBody == null) return null;
        YwclMessage ywclMessage = new YwclMessage(PktType.DEL_DEVICE, System.currentTimeMillis(), "123213132312");
        ywclMessage.setData(JSON.toJSONString(requestBody));
        JSONObject result = mqttRequestHelper.syncRequestData(ywclMessage, omcTopic, JSONObject.class, 300000);
        return result;
    }

    @Override
    public JSONObject addDevice(Map<String, Object> requestBody, String fsuId)
    {
        if (requestBody == null) return null;
        YwclMessage ywclMessage = new YwclMessage(PktType.SET_DEVICES, System.currentTimeMillis(), fsuId);
        ywclMessage.setData(JSON.toJSONString(requestBody));
        JSONObject result = mqttRequestHelper.syncRequestData(ywclMessage, omcTopic, JSONObject.class, mqttTimeout);
        return result;
    }

    @Override
    public JSONObject getFsuStatus(Map<String, Object> requestBody, String fsuId)
    {
        YwclMessage ywclMessage = new YwclMessage(PktType.GET_DEVICE_STATUS, System.currentTimeMillis(), fsuId);
        ywclMessage.setData(JSON.toJSONString(requestBody));
        JSONObject result = mqttRequestHelper.syncRequestData(ywclMessage, omcTopic, JSONObject.class, mqttTimeout);
        return result;
    }

    @Override
    public List<OperatHistory> getOperationHistory(Map<String, Object> requestBody, String fsuId)
    {
        return operatorHistoryDao.findByCondition(requestBody, fsuId);

    }

    @Override
    public Map<String, Integer> getFsuDeviceCountMap(List<String> fsuIds)
    {
        return fsuDevicesDao.getFsuDeviceCount(fsuIds);
    }

    @Override
    public JSONObject getOperationHistoryByMqtt(Map<String, Object> requestBody, String fsuId)
    {
        YwclMessage ywclMessage = new YwclMessage(PktType.GET_OP_LOG, System.currentTimeMillis(), fsuId);
        ywclMessage.setData(JSON.toJSONString(requestBody));
        JSONObject result = mqttRequestHelper.syncRequestData(ywclMessage, omcTopic, JSONObject.class, mqttTimeout);
        return result;
    }

    @Override
    public JSONObject logoutFsu(Map<String, Object> requestBody, String fsuId)
    {
        YwclMessage ywclMessage = new YwclMessage(PktType.LOGOUT, System.currentTimeMillis(), fsuId);
        ywclMessage.setData(JSON.toJSONString(requestBody));
        JSONObject result = mqttRequestHelper.syncRequestData(ywclMessage, omcTopic, JSONObject.class, mqttTimeout);
        return result;
    }

    @Override
    public List<Fsu> getFsuListByCoordinate(Map fsuMap)
    {
        String coordinate = (String) fsuMap.get("coordinate");
        if (StringUtils.isBlank(coordinate))
            return null;
        String[] coordinateLocation = coordinate.split(",");

        List<Fsu> fsuList = listFsu(null);

        List<Fsu> result = new ArrayList<>();
        for (Fsu fsu : fsuList)
        {
            String fsuCoordinate = fsu.getCoordinate();
            if (StringUtils.isNotBlank(fsuCoordinate))
            {
                String[] fsuCoordinateArr = fsuCoordinate.split(",");

                double distance = LocationUtils.getDistance(fsuCoordinateArr[0], fsuCoordinateArr[1], coordinateLocation[0], coordinateLocation[1]);
                if (distance < 2000)
                {
                    result.add(fsu);
                }
            }
        }
        return result;
    }

    @Override
    public List<Fsu> listFsu(Map<String, Object> requestBody)
    {
        if (requestBody == null) return fsuDao.getAllFsu();
        List<Fsu> fsuList = fsuDao.findByCondition(requestBody);
        return fsuList;

    }

    @Override
    public List<Fsu> searchFsu(Map<String, Object> requestBody)
    {
        if (requestBody == null) return null;

        return fsuDao.findByCondition(requestBody);
    }

    @Override
    public Fsu getFsu(Map<String, Object> requestBody)
    {
        if (requestBody == null) return null;
        String fsuId = (String) requestBody.get("fsuId");
        return fsuDao.findByFsuId(fsuId);
    }
}
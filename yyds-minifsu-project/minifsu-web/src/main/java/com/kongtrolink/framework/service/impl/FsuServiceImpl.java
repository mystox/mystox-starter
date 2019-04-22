package com.kongtrolink.framework.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.Fsu;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.core.entity.PktType;
import com.kongtrolink.framework.dao.FsuDao;
import com.kongtrolink.framework.dao.FsuDevicesDao;
import com.kongtrolink.framework.dao.OperatorHistoryDao;
import com.kongtrolink.framework.execute.module.RpcModule;
import com.kongtrolink.framework.model.OperatHistory;
import com.kongtrolink.framework.service.FsuService;
import com.kongtrolink.framework.util.LocationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class FsuServiceImpl implements FsuService {

    @Autowired
    RpcModule rpcModule;
    @Autowired
    FsuDao fsuDao;
    @Autowired
    FsuDevicesDao fsuDevicesDao;

    @Autowired
    OperatorHistoryDao operatorHistoryDao;

    @Override
    public JSONObject setFsu(Map<String, Object> requestBody) {
        if (requestBody == null) return null;
        String sn = (String) requestBody.get("sn");

        /*if (fsuCode != null)
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
        }*/
//        BeanUtils.copyProperties(requestBody, fsu);
//        fsuDao.saveFsu(fsu);

        ModuleMsg moduleMsg = new ModuleMsg(PktType.SET_STATION, sn);
        moduleMsg.setPayload((JSONObject) JSONObject.toJSON(requestBody));
        JSONObject result = rpcModule.syncRequestData(moduleMsg, JSONObject.class);
        return result;
    }


    @Override
    public JSONObject upgrade(Map<String, Object> requestBody, String sn) {
        if (requestBody == null) return null;
        ModuleMsg moduleMsg = new ModuleMsg(PktType.UPGRADE, sn);
        moduleMsg.setPayload((JSONObject) JSON.toJSON(requestBody));
        JSONObject result = rpcModule.syncRequestData(moduleMsg, JSONObject.class, 300000L);
        return result;
    }

    @Override
    public JSONObject compiler(Map<String, Object> requestBody, String sn) {
        if (requestBody == null) return null;
        ModuleMsg moduleMsg = new ModuleMsg(PktType.COMPILER, sn);
        moduleMsg.setPayload((JSONObject) JSONObject.toJSON(requestBody));
        JSONObject result = rpcModule.syncRequestData(moduleMsg, JSONObject.class);
        return result;
    }

    @Override
    public JSONArray getDeviceList(Map<String, Object> requestBody, String sn) {
        if (requestBody == null) return null;
        ModuleMsg moduleMsg = new ModuleMsg(PktType.GET_DEVICES, sn);
        moduleMsg.setPayload((JSONObject) JSONObject.toJSON(requestBody));
        JSONArray result = rpcModule.syncRequestData(moduleMsg, JSONArray.class);
        return result;
    }


    @Override
    public JSONObject getFsuStatus(Map<String, Object> requestBody, String sn) {
        if (requestBody == null) return null;
        ModuleMsg moduleMsg = new ModuleMsg(PktType.GET_DEVICE_STATUS, sn);
        moduleMsg.setPayload((JSONObject) JSONObject.toJSON(requestBody));
        JSONObject result = rpcModule.syncRequestData(moduleMsg, JSONObject.class);
        return result;
    }

    @Override
    public List<OperatHistory> getOperationHistory(Map<String, Object> requestBody, String fsuId) {
        return operatorHistoryDao.findByCondition(requestBody, fsuId);

    }

    @Override
    public Map<String, Integer> getFsuDeviceCountMap(List<String> fsuIds) {
        return fsuDevicesDao.getFsuDeviceCount(fsuIds);
    }

    @Override
    public JSONObject getOperationHistoryByMqtt(Map<String, Object> requestBody, String sn) {
        if (requestBody == null) return null;
        ModuleMsg moduleMsg = new ModuleMsg(PktType.GET_OP_LOG, sn);
        moduleMsg.setPayload((JSONObject) JSONObject.toJSON(requestBody));
        JSONObject result = rpcModule.syncRequestData(moduleMsg, JSONObject.class);
        return result;
    }

    @Override
    public JSONObject logoutFsu(Map<String, Object> requestBody, String sn) {
        if (requestBody == null) return null;
        ModuleMsg moduleMsg = new ModuleMsg(PktType.LOGOUT, sn);
        moduleMsg.setPayload((JSONObject) JSONObject.toJSON(requestBody));
        JSONObject result = rpcModule.syncRequestData(moduleMsg, JSONObject.class);
        return result;
    }

    @Override
    public JSONObject saveTerminal(JSONArray snList) {
        if (snList == null) return null;
        ModuleMsg moduleMsg = new ModuleMsg(PktType.TERMINAL_SAVE);
        moduleMsg.setArrayPayload(snList);
        JSONObject result = rpcModule.syncRequestData(moduleMsg, JSONObject.class);
        return result;
    }

    @Override
    public JSONObject terminalReboot(Map<String, Object> requestBody, String sn) {
        if (requestBody == null) return null;
        ModuleMsg moduleMsg = new ModuleMsg(PktType.TERMINAL_REBOOT, sn);
        moduleMsg.setPayload((JSONObject) JSONObject.toJSON(requestBody));
        JSONObject result = rpcModule.syncRequestData(moduleMsg, JSONObject.class);
        return result;
    }

    @Override
    public JSONObject setGprs(Map<String, Object> requestBody, String sn) {
        if (requestBody == null) return null;
        ModuleMsg moduleMsg = new ModuleMsg(PktType.TERMINAL_REBOOT, sn);
        moduleMsg.setPayload((JSONObject) JSONObject.toJSON(requestBody));
        JSONObject result = rpcModule.syncRequestData(moduleMsg, JSONObject.class);
        return result;

    }

    @Override
    public JSONObject saveSignalModelList(JSONArray signalModelList) {

        if (signalModelList == null) return null;
        ModuleMsg moduleMsg = new ModuleMsg(PktType.SIGNAL_MODEL_IMPORT);
        moduleMsg.setArrayPayload(signalModelList);
        JSONObject result = rpcModule.syncRequestData(moduleMsg, JSONObject.class);
        return result;
    }

    @Override
    public JSONObject saveAlarmModelList(JSONArray alarmSignalList) {
        if (alarmSignalList == null) return null;
        ModuleMsg moduleMsg = new ModuleMsg(PktType.ALARM_MODEL_IMPORT);
        moduleMsg.setArrayPayload(alarmSignalList);
        JSONObject result = rpcModule.syncRequestData(moduleMsg, JSONObject.class);
        return result;
    }

    @Override
    public List<Fsu> getFsuListByCoordinate(Map fsuMap) {
        String coordinate = (String) fsuMap.get("coordinate");
        if (StringUtils.isBlank(coordinate))
            return null;
        String[] coordinateLocation = coordinate.split(",");

//        List<Fsu> fsuList = listFsu(null);
        List<Fsu> fsuList = new ArrayList<>();

        List<Fsu> result = new ArrayList<>();
        for (Fsu fsu : fsuList) {
            String fsuCoordinate = fsu.getCoordinate();
            if (StringUtils.isNotBlank(fsuCoordinate)) {
                String[] fsuCoordinateArr = fsuCoordinate.split(",");

                double distance = LocationUtils.getDistance(fsuCoordinateArr[0], fsuCoordinateArr[1], coordinateLocation[0], coordinateLocation[1]);
                if (distance < 2000) {
                    result.add(fsu);
                }
            }
        }
        return result;
    }

    @Override
    public JSONArray listFsu(Map<String, Object> requestBody) {
        if (requestBody == null) return null;
        ModuleMsg moduleMsg = new ModuleMsg(PktType.GET_FSU);
        moduleMsg.setPayload((JSONObject) JSONObject.toJSON(requestBody));
        JSONArray result = rpcModule.syncRequestData(moduleMsg, JSONArray.class);
        return result;

    }

    @Override
    public List<Fsu> searchFsu(Map<String, Object> requestBody) {
        if (requestBody == null) return null;

        return fsuDao.findByCondition(requestBody);
    }

    @Override
    public Fsu getFsu(Map<String, Object> requestBody) {
        if (requestBody == null) return null;
        String fsuId = (String) requestBody.get("fsuId");
        return fsuDao.findByFsuId(fsuId);
    }
}
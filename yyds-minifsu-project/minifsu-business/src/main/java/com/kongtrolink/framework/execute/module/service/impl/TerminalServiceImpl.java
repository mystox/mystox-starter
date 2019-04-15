package com.kongtrolink.framework.execute.module.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.Fsu;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.core.entity.OperatHistory;
import com.kongtrolink.framework.core.entity.PktType;
import com.kongtrolink.framework.execute.module.RpcModule;
import com.kongtrolink.framework.execute.module.dao.DeviceDao;
import com.kongtrolink.framework.execute.module.dao.FsuDao;
import com.kongtrolink.framework.execute.module.dao.TerminalDao;
import com.kongtrolink.framework.execute.module.model.Device;
import com.kongtrolink.framework.execute.module.model.Terminal;
import com.kongtrolink.framework.execute.module.model.TerminalProperties;
import com.kongtrolink.framework.execute.module.service.TerminalService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by mystoxlol on 2019/3/25, 10:34.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class TerminalServiceImpl implements TerminalService {
    @Autowired
    FsuDao fsuDao;

    @Autowired
    TerminalDao terminalDao;
    @Autowired
    DeviceDao deviceDao;
    @Autowired
    RpcModule rpcModule;
    @Value("${rpc.controller.hostname}")
    private String controllerHost;
    @Value("${rpc.controller.port}")
    private int controllerPort;

    @Override
    public JSONObject setFsu(Map<String, Object> requestBody) {
        return null;
    }

    @Override
    public Fsu getFsu(ModuleMsg requestBody) {
        return null;
    }


    @Override
    public JSONObject upgrade(Map<String, Object> requestBody, String fsuId) {
        return null;
    }

    @Override
    public JSONObject compiler(Map<String, Object> requestBody) {
        return null;
    }

    @Override
    public JSONArray getDeviceList(ModuleMsg moduleMsg) {
        String sn = moduleMsg.getSN();
        List<Device> devicesBySn = deviceDao.findDevicesBySnAndValid(sn);
        return (JSONArray) JSONObject.toJSON(devicesBySn);
    }


    @Override
    public JSONObject deleteDevice(Map<String, Object> requestBody) {
        return null;
    }

    @Override
    public JSONObject addDevice(Map<String, Object> requestBody, String fsuId) {
        return null;
    }

    @Override
    public List<Fsu> getFsuListByCoordinate(Map fsuMap) {
        return null;
    }

    @Override
    public JSONArray listFsu(ModuleMsg requestBody) {

        JSONObject jsonObject = requestBody.getPayload();
        List<Terminal> terminals = terminalDao.findTerminal(jsonObject);

        JSONArray result = new JSONArray();
        for (Terminal terminal : terminals) {
            String terminalId = terminal.getId();
            TerminalProperties terminalPropertiesByTerminalId = terminalDao.findTerminalPropertiesByTerminalId(terminalId);
            JSONObject terminalJSON = (JSONObject) JSONObject.toJSON(terminal);
            terminalJSON.putAll((JSONObject) JSONObject.toJSON(terminalPropertiesByTerminalId));
            result.add(terminalJSON);
        }
        return result;
    }


    @Override
    public List<Fsu> searchFsu(Map<String, Object> requestBody) {
        return null;
    }

    @Override
    public JSONObject getFsuStatus(Map<String, Object> requestBody, String fsuId) {
        return null;
    }

    @Override
    public List<OperatHistory> getOperationHistory(Map<String, Object> requestBody, String fsuId) {
        return null;
    }

    @Override
    public Map<String, Integer> getFsuDeviceCountMap(List<String> fsuIds) {
        return null;
    }

    @Override
    public JSONObject getOperationHistoryByMqtt(Map<String, Object> requestBody, String fsuId) {
        return null;
    }

    @Override
    public JSONObject logoutFsu(Map<String, Object> requestBody, String fsuId) {
        return null;
    }

    @Override
    public JSONObject saveTerminal(ModuleMsg moduleMsg) {
        JSONArray terminalArray = moduleMsg.getArrayPayload();

        List<Terminal> terminals = JSONArray.parseArray(terminalArray.toJSONString(), Terminal.class);


        List<String> duplicateSn = new ArrayList<>();

        for (Terminal terminal : terminals) {
            String sn = terminal.getSN();
            if (terminalDao.existsBySn(sn)) {
                duplicateSn.add(sn);
                continue;
            }
        }

        JSONObject result = new JSONObject();
        if (duplicateSn.size() < 1) {
            terminalDao.saveTerminalBatch(terminals);
            result.put("result", 1);
            return result;
        } else {
            result.put("result", 0);
            result.put("duplicateSns", duplicateSn);
        }


        return result;
    }

    @Override
    public JSONObject setTerminal(ModuleMsg moduleMsg) {
        JSONObject jsonObject = moduleMsg.getPayload();
        String sn = moduleMsg.getSN();
        Terminal terminal = terminalDao.findTerminalBySn(sn);
        Object heartCycle = jsonObject.get("heartCycle");
        if (heartCycle!=null) terminal.setHeartCycle((Integer) heartCycle);
        Object businessRhythm = jsonObject.get("businessRhythm");
        if (businessRhythm!=null) terminal.setBusinessRhythm((Integer) businessRhythm);
        Object alarmRhythm = jsonObject.get("alarmRhythm");
        if (alarmRhythm!=null) terminal.setAlarmRhythm((Integer) alarmRhythm);
        Object runStatusRhythm = jsonObject.get("runStatusRhythm");
        if (runStatusRhythm!=null) terminal.setRunStatusRhythm((Integer) runStatusRhythm);
        Object coordinate = jsonObject.get("coordinate");
        if (runStatusRhythm!=null) terminal.setCoordinate((String) coordinate);

        String fsuId = (String) jsonObject.get("fsuId");
        if (StringUtils.isNotBlank(fsuId)) {
            // 向网关发送业注册报文{"SN","00000",DEVICE_LIST} 即向业务平台事务处理发送注册信息
            try {
                JSONObject snFsu = new JSONObject();
                snFsu.put("sn",sn );
                snFsu.put("fsuId", fsuId);
                moduleMsg.setPktType(PktType.FSU_BIND);
                moduleMsg.setPayload(snFsu);
                rpcModule.postMsg(moduleMsg.getMsgId(), new InetSocketAddress(controllerHost, controllerPort), JSONObject.toJSONString(moduleMsg));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        JSONObject result = new JSONObject();
        result.put("result", 1);
        return result;
    }
}

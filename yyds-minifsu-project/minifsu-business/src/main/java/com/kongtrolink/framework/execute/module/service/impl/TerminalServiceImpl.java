package com.kongtrolink.framework.execute.module.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.Fsu;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.core.entity.OperatHistory;
import com.kongtrolink.framework.execute.module.dao.DeviceDao;
import com.kongtrolink.framework.execute.module.dao.FsuDao;
import com.kongtrolink.framework.execute.module.dao.TerminalDao;
import com.kongtrolink.framework.execute.module.model.Device;
import com.kongtrolink.framework.execute.module.model.Terminal;
import com.kongtrolink.framework.execute.module.model.TerminalProperties;
import com.kongtrolink.framework.execute.module.service.TerminalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}

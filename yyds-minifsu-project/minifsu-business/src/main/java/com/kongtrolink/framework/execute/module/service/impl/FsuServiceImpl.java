package com.kongtrolink.framework.execute.module.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.Fsu;
import com.kongtrolink.framework.core.entity.OperatHistory;
import com.kongtrolink.framework.execute.module.dao.FsuDao;
import com.kongtrolink.framework.execute.module.service.FsuService;
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
public class FsuServiceImpl implements FsuService
{
    @Autowired
    FsuDao fsuDao;
    @Override
    public JSONObject setFsu(Map<String, Object> requestBody)
    {
        return null;
    }

    @Override
    public Fsu getFsu(Map<String, Object> requestBody)
    {
        if (requestBody == null) return null;
        String fsuId = (String) requestBody.get("fsuId");
        return fsuDao.findByFsuId(fsuId);
    }

    @Override
    public JSONObject upgrade(Map<String, Object> requestBody, String fsuId)
    {
        return null;
    }

    @Override
    public JSONObject compiler(Map<String, Object> requestBody)
    {
        return null;
    }

    @Override
    public JSONObject getDeviceList(Map<String, Object> requestBody, String fsuId)
    {
        return null;
    }

    @Override
    public JSONObject deleteDevice(Map<String, Object> requestBody)
    {
        return null;
    }

    @Override
    public JSONObject addDevice(Map<String, Object> requestBody, String fsuId)
    {
        return null;
    }

    @Override
    public List<Fsu> getFsuListByCoordinate(Map fsuMap)
    {
        return null;
    }

    @Override
    public List<Fsu> listFsu(Map<String, Object> requestBody)
    {
        return null;
    }

    @Override
    public List<Fsu> searchFsu(Map<String, Object> requestBody)
    {
        return null;
    }

    @Override
    public JSONObject getFsuStatus(Map<String, Object> requestBody, String fsuId)
    {
        return null;
    }

    @Override
    public List<OperatHistory> getOperationHistory(Map<String, Object> requestBody, String fsuId)
    {
        return null;
    }

    @Override
    public Map<String, Integer> getFsuDeviceCountMap(List<String> fsuIds)
    {
        return null;
    }

    @Override
    public JSONObject getOperationHistoryByMqtt(Map<String, Object> requestBody, String fsuId)
    {
        return null;
    }

    @Override
    public JSONObject logoutFsu(Map<String, Object> requestBody, String fsuId)
    {
        return null;
    }
}

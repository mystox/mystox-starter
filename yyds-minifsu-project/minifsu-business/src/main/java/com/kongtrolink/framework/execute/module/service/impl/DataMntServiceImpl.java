package com.kongtrolink.framework.execute.module.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.core.entity.RedisHashTable;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.execute.module.dao.DeviceDao;
import com.kongtrolink.framework.execute.module.service.DataMntService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/4/10, 18:57.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class DataMntServiceImpl implements DataMntService {

    @Autowired
    RedisUtils redisUtils;

    DeviceDao deviceDao;

    @Override
    public JSONObject getSignalList(ModuleMsg moduleMsg) {
        String sn = moduleMsg.getSN();
        JSONObject searchCondition = moduleMsg.getPayload();
        String devId = (String) searchCondition.get("deviceId");
        Integer resNo = (Integer) searchCondition.get("resNo");
        Integer type = (Integer) searchCondition.get("type");

        String dev = type + "-" + resNo;
        JSONObject mntData = redisUtils.getHash(RedisHashTable.SN_DATA_HASH,sn,JSONObject.class);
        JSONArray jsonArray = (JSONArray) mntData.get("data");
        for (Object devObject : jsonArray)
        {
            JSONObject devJson = (JSONObject) devObject;
            if (dev.equals(devJson.get("dev"))) {
                return devJson;
            }
        }

        return new JSONObject();
    }

}

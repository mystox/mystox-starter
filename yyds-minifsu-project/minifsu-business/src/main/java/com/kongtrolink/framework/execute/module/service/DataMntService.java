package com.kongtrolink.framework.execute.module.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.AlarmSignalConfig;
import com.kongtrolink.framework.core.entity.ModuleMsg;

import java.util.List;
import java.util.Map;

/**
 * Created by mystoxlol on 2019/4/10, 18:57.
 * company: kongtrolink
 * description:
 * update record:
 */
public interface DataMntService {
    JSONObject getSignalList(ModuleMsg moduleMsg);

    JSONObject setThreshold(ModuleMsg moduleMsg);

    JSONArray getThreshold(ModuleMsg moduleMsg);

    JSONObject saveRunStatus(ModuleMsg moduleMsg);


    JSONObject saveSignalModel(ModuleMsg moduleMsg);

    JSONObject setData(ModuleMsg moduleMsg);

    JSONObject parseData(ModuleMsg moduleMsg);

    JSONObject alarmConfigPush(String msgId, String sn,String devId,Map<String, List<AlarmSignalConfig>> alarmConfigKeyMap);
}

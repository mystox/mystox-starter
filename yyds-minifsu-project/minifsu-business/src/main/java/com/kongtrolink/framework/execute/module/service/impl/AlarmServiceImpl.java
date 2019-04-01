package com.kongtrolink.framework.execute.module.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.Alarm;
import com.kongtrolink.framework.core.entity.AlarmSignal;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.execute.module.dao.AlarmDao;
import com.kongtrolink.framework.execute.module.service.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by mystoxlol on 2019/4/1, 19:30.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class AlarmServiceImpl implements AlarmService {

    @Autowired
    AlarmDao alarmDao;

    /**
     * @param moduleMsg
     * @auther: liudd
     * @date: 2019/4/1 20:35
     * 功能描述:根据告警点id添加或修改告警
     */
    @Override
    public void AddOrUpdateByAlarmId(ModuleMsg moduleMsg) {
        JSONObject payload = moduleMsg.getPayload();
        List<Alarm> alarms = JSONArray.parseArray(payload.toJSONString(), Alarm.class);
        for(Alarm alarm : alarms){
            alarmDao.AddOrUpdateByAlarmId(alarm);
        }
    }
}

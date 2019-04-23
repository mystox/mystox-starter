package com.kongtrolink.framework.execute.module.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.Alarm;
import com.kongtrolink.framework.core.entity.AlarmSignalConfig;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.core.entity.RedisHashTable;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.execute.module.dao.AlarmDao;
import com.kongtrolink.framework.execute.module.dao.ConfigDao;
import com.kongtrolink.framework.execute.module.dao.DeviceDao;
import com.kongtrolink.framework.execute.module.model.AlarmSignalConfigModel;
import com.kongtrolink.framework.execute.module.model.Device;
import com.kongtrolink.framework.execute.module.service.AlarmService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

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

    RedisUtils redisUtils;
    @Autowired
    public void setRedisUtils(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
    }


    private DeviceDao deviceDao;
    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
    private ConfigDao configDao;

    @Autowired
    public void setConfigDao(ConfigDao configDao) {
        this.configDao = configDao;
    }

    /**
     * @param moduleMsg
     * @auther: liudd
     * @date: 2019/4/1 20:35
     * 功能描述:根据告警点id添加或修改告警
     */
    @Override
    public JSONObject save(ModuleMsg moduleMsg) {
        JSONObject payload = moduleMsg.getPayload();

        List<Alarm> alarms = JSONArray.parseArray(JSON.toJSONString(payload.get("list")), Alarm.class);
        alarmDao.save(alarms);
        JSONObject object = new JSONObject();
        object.put("result", 1);
        return object;
    }

    @Override
    public JSONObject saveAlarmModel(ModuleMsg moduleMsg) {
        JSONArray jsonArray = moduleMsg.getArrayPayload();

        List<AlarmSignalConfigModel> alarmSignalConfigModels = JSONArray.parseArray(jsonArray.toJSONString(), AlarmSignalConfigModel.class);
        for (AlarmSignalConfigModel alarmSignalConfigModel : alarmSignalConfigModels) {
            AlarmSignalConfigModel alarmSignalConfigModel1 = configDao.findAlarmSignalModelByDevTypeAndAlarmId(alarmSignalConfigModel.getDevType(), alarmSignalConfigModel.getAlarmId());
            if (alarmSignalConfigModel1 != null) {
                BeanUtils.copyProperties(alarmSignalConfigModel, alarmSignalConfigModel1,"id");
                configDao.saveAlarmSignalConfigModel(alarmSignalConfigModel1);
            } else
                configDao.saveAlarmSignalConfigModel(alarmSignalConfigModel);
        }
        JSONObject result = new JSONObject();
        result.put("result", 1);
        return result;
    }

    @Override
    public JSONArray getAlarms(ModuleMsg moduleMsg) {
        String sn = moduleMsg.getSN();
        String hashTable = RedisHashTable.SN_ALARM_HASH + sn;
        Set<String> keys = redisUtils.getHkeys(hashTable,"*");
        JSONArray alarmList = new JSONArray();
        for (String key : keys) {
            Alarm alarm = redisUtils.getHash(hashTable, key, Alarm.class);
            String dev = key.split("_")[0];
            Device device = deviceDao.findDeviceByTypeResNoPort(sn,Integer.valueOf(dev.split("-")[0]), Integer.valueOf(dev.split("-")[1]),null);
            JSONObject alarmJson = (JSONObject) JSONObject.toJSON(alarm);
            AlarmSignalConfig alarmSignalConfig = configDao.findAlarmSignalConfigByDeviceIdAndAlarmId(device.getId(),alarm.getAlarmId());
            alarmJson.put("name", alarmSignalConfig == null ? "未知告警" : alarmSignalConfig.getAlarmDesc());
            alarmList.add(alarmJson);
        }
        return (JSONArray) JSONArray.toJSON(alarmList);
    }
}

package com.kongtrolink.framework.execute.module.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.AlarmSignalConfig;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.core.entity.RedisHashTable;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.execute.module.dao.ConfigDao;
import com.kongtrolink.framework.execute.module.dao.DeviceDao;
import com.kongtrolink.framework.execute.module.model.Device;
import com.kongtrolink.framework.execute.module.service.DataMntService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

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


    @Autowired
    DeviceDao deviceDao;


    @Autowired
    ConfigDao configDao;

    @Override
    public JSONObject getSignalList(ModuleMsg moduleMsg) {
        String sn = moduleMsg.getSN();
        JSONObject searchCondition = moduleMsg.getPayload();
        String devId = (String) searchCondition.get("deviceId");
        Integer resNo = (Integer) searchCondition.get("resNo");
        Integer type = (Integer) searchCondition.get("type");

        String dev = type + "-" + resNo;
        Set<String> mntData = redisUtils.getHkeys(RedisHashTable.SN_DATA_HASH+sn,dev + "_*");

        for (String key : mntData) {
            JSONObject coData = new JSONObject();
            Float value = redisUtils.getHash(RedisHashTable.SN_DATA_HASH+sn,key,Float.class);
            String coId = key.replaceFirst(key, dev + "_");
            coData.put("coId", coId);
            coData.put("value", value);
            //TODO 翻译数据点
            String name = tranceDataId(coId);

            coData.put("name", "");
        }
//        JSONArray jsonArray = (JSONArray) mntData.add("data");
        JSONArray jsonArray = new JSONArray();
        for (Object devObject : jsonArray) {
            JSONObject devJson = (JSONObject) devObject;
            if (dev.equals(devJson.get("dev"))) {
                //todo 信号点属性翻译
                JSONObject info = (JSONObject) devJson.get("info");
                info.keySet();



                return devJson;
            }
        }

        return new JSONObject();
    }

    /**
     * 转换数据点id类型名称
     * @param coId
     * @return
     */
    private String tranceDataId(String coId) {

        return "1";
    }

    @Override
    public JSONObject setThreshold(ModuleMsg moduleMsg) {
        String sn = moduleMsg.getSN();
        JSONObject alarmSignal = moduleMsg.getPayload();
        String deviceId = (String) alarmSignal.get("deviceId");
        String coId = (String) alarmSignal.get("coId");
        String configId = (String) alarmSignal.get("configId");
        Double threshold = alarmSignal.get("threshold") == null ? null : Double.parseDouble( alarmSignal.get("threshold")+"");
        Float hystersis = alarmSignal.get("hystersis") == null ? null :  Float.parseFloat(alarmSignal.get("hystersis")+"");
        Integer relativeval = alarmSignal.get("relativeval") == null ? null : (Integer) alarmSignal.get("relativeval");
        Integer level = alarmSignal.get("level") == null ? null : (Integer) alarmSignal.get("level");
        Integer delay = alarmSignal.get("delay") == null ? null : (Integer) alarmSignal.get("delay");

        Device device = deviceDao.findDeviceById(deviceId);
        int devType = device.getType();
        int resNo = device.getResNo();
        String dev = devType + "-" + resNo;

        String alarmConfigKey = sn + "_" + dev + "_" + coId;

        //设置内存值
        JSONArray redisSignalObj = redisUtils.getHash(RedisHashTable.SN_DEV_ID_ALARM_SIGNAL_HASH, alarmConfigKey, JSONArray.class);
        List<AlarmSignalConfig> alarmSignals = JSONArray.parseArray(redisSignalObj.toString(), AlarmSignalConfig.class);

        for (AlarmSignalConfig alarmSignalConfig : alarmSignals) {
            String coId1 = alarmSignalConfig.getCoId();
            if (coId1.equals(coId)) {
                alarmSignalConfig.setThreshold(threshold);
            }

        }

        redisUtils.setHash(RedisHashTable.SN_DEV_ID_ALARM_SIGNAL_HASH, alarmConfigKey, alarmSignals);
        //设置数据库值
        AlarmSignalConfig alarmSignalConfig = configDao.findAlarmSignalConfigById(configId);
        if (threshold != null)
            alarmSignalConfig.setThreshold(threshold);
         if (relativeval != null)
            alarmSignalConfig.setRecoverDelay(relativeval);
         if (hystersis != null)
            alarmSignalConfig.setHystersis(hystersis);
         if (level != null)
            alarmSignalConfig.setLevel(level);
         if (delay != null)
            alarmSignalConfig.setDelay(delay);

        configDao.saveAlarmSignalConfig(alarmSignalConfig);

        JSONObject result = new JSONObject();
        result.put("result", 1);
        return result;
    }

    @Override
    public JSONArray getThreshold(ModuleMsg moduleMsg) {
        String sn = moduleMsg.getSN();
        JSONObject payload = moduleMsg.getPayload();
        String dev = (String) payload.get("dev");
        String[] devArr = dev.split("-");
        Integer devType = Integer.parseInt(devArr[0]);
        Integer resNo = Integer.parseInt(devArr[1]);
        Device device = deviceDao.findDeviceByTypeResNoPort(sn,devType, resNo, null); //根据信号点的devType获取deviceId
        String coId = (String) payload.get("coId");
        List<AlarmSignalConfig> alarmSignalConfigList = configDao.findAlarmSignalConfigByDeviceIdAndCoId(device.getId(),coId); //根据deviceId和数据点id获取信号点配置列表
        return (JSONArray) JSONArray.toJSON(alarmSignalConfigList);
    }

}

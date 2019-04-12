package com.kongtrolink.framework.execute.module.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.AlarmSignal;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.core.entity.RedisHashTable;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.execute.module.dao.DeviceDao;
import com.kongtrolink.framework.execute.module.model.Device;
import com.kongtrolink.framework.execute.module.service.DataMntService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public JSONObject setThreshold(ModuleMsg moduleMsg) {
        String sn = moduleMsg.getSN();

        JSONObject alarmSignal = new JSONObject();
        String deviceId = (String) alarmSignal.get("deviceId");
        String coId = (String) alarmSignal.get("coId");

        Double threshold = alarmSignal.get("threshold") == null ? null : (Double) alarmSignal.get("threshold");
        Double relativeval = alarmSignal.get("relativeval") == null ? null : (Double) alarmSignal.get("relativeval");
        Double hystersis = alarmSignal.get("hystersis") == null ? null : (Double) alarmSignal.get("hystersis");
        Integer level = alarmSignal.get("level") == null ? null : (Integer) alarmSignal.get("level");
        Integer delay = alarmSignal.get("delay") == null ? null : (Integer) alarmSignal.get("delay");



        Device device = deviceDao.findDeviceById(deviceId);



        int devType = device.getType();
        int resNo = device.getResNo();
        String dev = devType + "-" + resNo;

        String alarmConfigKey = sn + "_" + dev + "_" + coId;



        //设置内存值
        JSONArray redisSignalObj = redisUtils.getHash(RedisHashTable.SN_DEV_ID_ALARM_SIGNAL_HASH, alarmConfigKey,JSONArray.class);
        List<AlarmSignal> alarmSignals = JSONArray.parseArray(redisSignalObj.toString(), AlarmSignal.class);

      /*  for (AlarmSignal alarmSignal : alarmSignals)
        {
            String coId1 = alarmSignal.getCoId();
            if (coId1.equals(coId)) {
//                alarmSignal.setThreshold();
            }

        }
*/
//        redisUtils.setHash(RedisHashTable.SN_DEV_ID_ALARM_SIGNAL_HASH,);


        //设置数据库值



        return null;
    }

}

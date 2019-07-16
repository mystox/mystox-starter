package com.kongtrolink.framework.service;

import com.kongtrolink.framework.core.entity.CoreConstant;
import com.kongtrolink.framework.core.entity.RedisHashTable;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.jsonType.JsonDevice;
import com.kongtrolink.framework.jsonType.JsonFsu;
import com.kongtrolink.framework.jsonType.JsonSignal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: liudd
 * @Date: 2019/4/12 15:16
 * @Description:实时数据解析service:将实时数据解析为sn_dev_colId, value的map，方便后续处理和直接更新到redis中
 */
@Service
public class TimeDataAnalysisService {

    @Autowired
    RedisUtils redisUtils;
    @Autowired
    AlarmAnalysisService alarmAnalysisService;

    /**
     * @auther: liudd
     * @date: 2019/4/12 16:32
     * 功能描述:解析实时数据
     */
    public Map<String, Integer> analysisData(JsonFsu fsu){
        Map<String, Integer> dev_colId_valMap = new HashMap<>();
        List<JsonDevice> data = fsu.getData();
        if(null == data){
            return dev_colId_valMap;
        }
        for(JsonDevice device : data){
            StringBuilder devKey = new StringBuilder(device.getDev()).append(CoreConstant.LINE_CUT_OFF);
            HashMap<String, Long> info = device.getInfo();
            if(null == info){
                return dev_colId_valMap;
            }
            for(String key  : info.keySet()){
                String dev_colId = devKey + key;
                long aFloat = info.get(key);
                int intVal = (int)aFloat;
                dev_colId_valMap.put(dev_colId, intVal);
                //更新实时数据
//                redisUtils.hset(sn_data_hash, dev_colId, intVal);
            }
        }
        return dev_colId_valMap;
    }

    /**
     * @auther: liudd
     * @date: 2019/5/24 10:08
     * 功能描述:更新实时数据到redis
     */
    public void updateData(JsonFsu fsu, Map<String, Integer> dev_colId_valMap){
        String sn_data_hash = RedisHashTable.SN_DATA_HASH + fsu.getSN();
        for(String key : dev_colId_valMap.keySet()){
            redisUtils.hset(sn_data_hash, key, dev_colId_valMap.get(key));
        }
    }

    /**
     * @auther: liudd
     * @date: 2019/5/24 10:27
     * 功能描述:DI点告警情况变化后，需要拼接到fsu中，推送给你铁塔
     */
    public JsonFsu jointFsu(JsonFsu fsu, Map<String, Integer> DI_dev_colId_valMap){
        if(null == DI_dev_colId_valMap || DI_dev_colId_valMap.size()==0){
            return fsu;
        }
        List<JsonDevice> data = fsu.getData();
        if(null == data){
            data = new ArrayList<>();
            fsu.setData(data);
        }
        for(String key : DI_dev_colId_valMap.keySet()){
            String[] split = key.split(CoreConstant.LINE_CUT_OFF);
            String dev = split[0];
            String alarmId = split[1];
            int value = DI_dev_colId_valMap.get(key);
            JsonDevice device = fsu.getJsonDeviceByDev(dev);
            if(null == device){
                device = new JsonDevice();
                data.add(device);
            }
            HashMap<String, Long> info = device.getInfo();
            if(null == info){
                info = new HashMap<>();
                device.setInfo(info);
            }
            info.put(alarmId, new Long(value));
        }
        return fsu;
    }
}

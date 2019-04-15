package com.kongtrolink.framework.service;

import com.kongtrolink.framework.core.entity.CoreConstant;
import com.kongtrolink.framework.core.entity.RedisHashTable;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.jsonType.JsonDevice;
import com.kongtrolink.framework.jsonType.JsonFsu;
import com.kongtrolink.framework.jsonType.JsonSignal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
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
    public Map<String, Float> analysisData(JsonFsu fsu){
        String sn_data_hash = RedisHashTable.SN_DATA_HASH + fsu.getSN();
        Map<String, Float> dev_colId_valMap = new HashMap<>();
        for(JsonDevice device : fsu.getData()){
            StringBuilder devKey = new StringBuilder(fsu.getSN()).append(CoreConstant.LINE_CUT_OFF).append(device.getDev()).append(CoreConstant.LINE_CUT_OFF);
            HashMap<String, Float> info = device.getInfo();
            for(String key  : info.keySet()){
                String sn_dev_colId = devKey + key;
                dev_colId_valMap.put(sn_dev_colId, info.get(key));
                //更新实时数据
                redisUtils.hset(sn_data_hash, sn_dev_colId, info.get(key));
            }
        }
        return dev_colId_valMap;
    }
}

package com.kongtrolink.framework.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.Alarm;
import com.kongtrolink.framework.core.entity.RedisHashTable;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.jsonType.JsonDevice;
import com.kongtrolink.framework.jsonType.JsonFsu;
import com.kongtrolink.framework.jsonType.JsonSignal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Auther: liudd
 * @Date: 2019/3/29 11:30
 * @Description:
 */
@Service
public class DataRegisterService {
    @Autowired
    RedisUtils redisUtils;
    private String sn__alarm_hash = RedisHashTable.SN_ALARM_HASH;

    public String register(String msgId, JSONObject payload){
        String sn = payload.get("SN").toString();
        //根据SN删除redis中相应实时告警
        Object hget = redisUtils.hget(sn__alarm_hash, sn);
        if(null != hget){
            JsonFsu fsu = JSON.parseObject(hget.toString(), JsonFsu.class);
            Iterator<JsonDevice> deviceIterator = fsu.getData().iterator();
            while (deviceIterator.hasNext()){
                JsonDevice device = deviceIterator.next();
                Iterator<JsonSignal> signalIterator = device.getSignalList().iterator();
                while(signalIterator.hasNext()){
                    JsonSignal jsonSignal = signalIterator.next();
                    Map<String, Alarm> alarmMap = jsonSignal.getAlarmMap();
                    Iterator<Map.Entry<String, Alarm>> iterator = alarmMap.entrySet().iterator();
                    while (iterator.hasNext()){
                        Map.Entry<String, Alarm> next = iterator.next();
                        Alarm alarm = next.getValue();
                        byte link = (byte)(alarm.getLink() << 1);
                        alarm.setLink(link);
                        if(link == 8){
                            iterator.remove();
                        }
                    }
                    if(jsonSignal.getAlarmMap().isEmpty()){
                        signalIterator.remove();
                    }
                }
                if(device.getSignalList().isEmpty()){
                    deviceIterator.remove();
                }
            }
            if(fsu.getData().isEmpty()){
                redisUtils.hdel(sn__alarm_hash, sn);
            }else{
                redisUtils.hset(sn__alarm_hash, sn, JSON.toJSONString(fsu));
            }
        }
        return "{}";
    }
}

package com.kongtrolink.framework.service;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: liudd
 * @Date: 2019/3/29 11:30
 * @Description:
 */
@Service
public class DataRegisterService {
    @Autowired
    RedisUtils redisUtils;
    private String sn__alarm_hash = "";

    public String register(String msgId, JSONObject payload){
        String sn = payload.get("SN").toString();
        //根据SN删除redis中相应实时告警
        redisUtils.hdel(sn__alarm_hash, sn);
        return "{}";
    }
}

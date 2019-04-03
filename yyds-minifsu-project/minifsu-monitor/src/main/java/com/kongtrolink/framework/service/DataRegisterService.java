package com.kongtrolink.framework.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.Alarm;
import com.kongtrolink.framework.core.entity.RedisHashTable;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.execute.module.RpcModule;
import com.kongtrolink.framework.jsonType.JsonDevice;
import com.kongtrolink.framework.jsonType.JsonFsu;
import com.kongtrolink.framework.jsonType.JsonSignal;
import com.kongtrolink.framework.task.SaveAalarmTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Iterator;
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
    @Autowired
    RpcModule rpcModule;
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Value("${rpc.controller.hostname}")
    private String controllerName;
    @Value("${rpc.controller.port}")
    private int controllerPort;

    /**
     * @auther: liudd
     * @date: 2019/4/3 14:22
     * 功能描述:告警注册与消除告警功能。本过程与实时数据上报过程无关，所以在另一个线程中处理
     * 1，想铁塔发送告警信息
     *      1，发送成功，修改各个告警状态，如果已消除上报成功，则移除该告警，并发送给事物处理中心，保存该告警为历史告警
     *      2，发送失败，不做处理
     * 2，将实时告警保存到数据库
     */
    public String register(String msgId, JsonFsu fsu, Date curDate){
        if(null == fsu || fsu.getData().isEmpty()){
            return "";
        }
        if(!fsu.getData().isEmpty()){
            fsu.setPktType(null);
            fsu.setDtm(null);
//            redisUtils.hset(sn__alarm_hash, sn, JSON.toJSONString(fsu));
            taskExecutor.execute(new SaveAalarmTask(controllerName, controllerPort, rpcModule, fsu, redisUtils));
        }
        return "";
    }
}

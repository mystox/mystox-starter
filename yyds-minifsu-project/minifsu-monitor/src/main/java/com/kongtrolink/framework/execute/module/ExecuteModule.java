package com.kongtrolink.framework.execute.module;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.Communication;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.core.entity.RedisHashTable;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.protobuf.protorpc.RpcNotifyImpl;
import com.kongtrolink.framework.core.service.ModuleInterface;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.jsonType.JsonFsu;
import com.kongtrolink.framework.service.DataRegisterService;
import com.kongtrolink.framework.service.DataReportService;
import com.kongtrolink.framework.task.SaveAalarmTask;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * Created by mystoxlol on 2019/2/25, 19:25.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class ExecuteModule extends RpcNotifyImpl implements ModuleInterface {

    @Autowired
    DataReportService reportService;
    @Autowired
    DataRegisterService registerService;
    @Autowired
    RedisUtils redisUtils;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private String communication_hash = RedisHashTable.COMMUNICATION_HASH;
    private  String sn_data_hash = RedisHashTable.SN_DATA_HASH;
//    private String sn__alarm_hash = RedisHashTable.SN_ALARM_HASH;

    /**
     * @auther: liudd
     * @date: 2019/4/3 10:32
     * 功能描述:
     * 1,初始化线程池之类的基础任务服务
     * 2,加载告警组合，关联，过滤，延迟等配置信息放到redis中
     */
    @Override
    public boolean init(){
        logger.info("workerExecute-execute module init");
        return true;
    }

    /**
     * @auther: liudd
     * @date: 2019/3/26 13:09
     * 功能描述:实时数据处理模块
     * 1，保存实时数据
     * 2，解析告警
     * 3，上报告警
     * 4，消除告警
     */
    @Override
    protected RpcNotifyProto.RpcMessage execute(String msgId, String payload){
        Date curDate = new Date();
        RpcNotifyProto.MessageType response = RpcNotifyProto.MessageType.RESPONSE;
        ModuleMsg moduleMsg = JSON.parseObject(payload, ModuleMsg.class);
        JSONObject infoPayload = moduleMsg.getPayload();
        if(!checkCommunication(infoPayload)){
            return createResp(response, "{'pktType':4,'result':0}", StringUtils.isBlank(msgId)? "" : msgId);
        }
        JsonFsu fsu = JSONObject.parseObject(infoPayload.toJSONString(), JsonFsu.class);
        String sn = fsu.getSN();
        //保存实时数据
        redisUtils.hset(sn_data_hash, sn, infoPayload);
        //变化数据上报
        String result = reportService.report(msgId, fsu, curDate);
        //告警注册与消除
        registerService.register(msgId, fsu, curDate);
        return createResp(response, result, StringUtils.isBlank(msgId)? "" : msgId);
    }

    /**
     * @auther: liudd
     * @date: 2019/4/3 10:42
     * 功能描述:判断通讯信息状态
     */
    private boolean checkCommunication(JSONObject infoPayload){
        if(null == infoPayload){
            return false;
        }
        String sn = (String)infoPayload.get("SN");
        if(StringUtils.isBlank(sn)){
            return false;
        }
        //判定redis中是否有该FSU的通讯信息
        Object communicationObj = redisUtils.get(communication_hash + ":" + sn);
        if(null == communicationObj){
            return  false;
        }
        Communication communication = JSON.parseObject(communicationObj.toString(), Communication.class);
        if(communication.getStatus() == 0){     //终端未注册成功，返回注册失败消息
            return  false;
        }
        return true;
    }

    /**
     * @auther: liudd
     * @date: 2019/4/3 10:32
     * 功能描述:创建消息体
     */
    private RpcNotifyProto.RpcMessage createResp(RpcNotifyProto.MessageType responseType, String result, String msgId){
        return RpcNotifyProto.RpcMessage.newBuilder()
                .setType(responseType).setPayload(result)
                .setPayloadType(RpcNotifyProto.PayloadType.JSON)
                .setMsgId(StringUtils.isBlank(msgId) ? "" : msgId).build();
    }
}

package com.kongtrolink.framework.execute.module;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.*;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.protobuf.protorpc.RpcNotifyImpl;
import com.kongtrolink.framework.core.service.ModuleInterface;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.jsonType.JsonFsu;
import com.kongtrolink.framework.service.AlarmAnalysisService;
import com.kongtrolink.framework.service.DataRegisterService;
import com.kongtrolink.framework.service.TimeDataAnalysisService;
import com.kongtrolink.framework.task.SaveLogTask;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import java.net.InetSocketAddress;
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
    DataRegisterService registerService;
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    RpcModule rpcModule;
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    TimeDataAnalysisService timeDateService;
    @Autowired
    AlarmAnalysisService analysisService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private String communication_hash = RedisHashTable.COMMUNICATION_HASH;


    @Value("${server.name}")
    private String serviceName;
    @Value("${server.bindIp}")
    private String hostIp;
    @Value("${rpc.controller.hostname}")
    private String controllerName;
    @Value("${rpc.controller.port}")
    private int controllerPort;

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
        String result = "{'pktType':4,'result':1}";
        Date curDate = new Date();
        RpcNotifyProto.MessageType response = RpcNotifyProto.MessageType.RESPONSE;
        ModuleMsg moduleMsg = JSON.parseObject(payload, ModuleMsg.class);
        JSONObject infoPayload = moduleMsg.getPayload();
        JsonFsu fsu = JSONObject.parseObject(infoPayload.toJSONString(), JsonFsu.class);
        if(!checkCommunication(infoPayload)){
            saveLog(msgId, fsu, curDate, moduleMsg.getPktType());
            return createResp(response, "{'pktType':4,'result':2}", StringUtils.isBlank(msgId)? "" : msgId);
        }

        //解析保存实时数据和告警
        Map<String, Float> dev_colId_valMap = timeDateService.analysisData(fsu);
        //解析告警
        Map<String, JSONObject>  alarmMap = analysisService.analysisAlarm(fsu, dev_colId_valMap, curDate);
        //告警注册与消除
        if(!alarmMap.isEmpty()) {
            registerService.register(msgId, fsu, alarmMap, curDate);
        }
        return createResp(response, result, StringUtils.isBlank(msgId)? "" : msgId);
    }

    private void saveLog(String msgId, JsonFsu fsu, Date curDate, String pktType){
        //发送日志
        Log log = new Log();
        log.setErrorCode(StateCode.UNREGISTY);
        log.setServiceName(serviceName);
        log.setHostName(hostIp);
        log.setMsgId(msgId);
        log.setSN(fsu.getSN());
        log.setTime(curDate);
        log.setMsgType(pktType);
        ModuleMsg msg = new ModuleMsg();
        msg.setPayload(JSONObject.parseObject(JSON.toJSONString(log)));
        msg.setPktType(PktType.LOG_SAVE);
        SaveLogTask task = new SaveLogTask(msgId, new InetSocketAddress(controllerName, controllerPort),
                JSON.toJSONString(msg), rpcModule);
        taskExecutor.execute(task);
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
        if(communication.getStatus() != RegisterCodeState.ONLINE){     //终端未注册成功，返回注册失败消息
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

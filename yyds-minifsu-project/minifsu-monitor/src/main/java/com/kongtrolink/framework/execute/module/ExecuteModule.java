package com.kongtrolink.framework.execute.module;

import com.alibaba.fastjson.JSON;
import com.kongtrolink.framework.core.entity.Communication;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.core.entity.PktType;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.protobuf.protorpc.RpcNotifyImpl;
import com.kongtrolink.framework.core.service.ModuleInterface;
import com.kongtrolink.framework.jsonType.JsonFsu;
import com.kongtrolink.framework.service.DataRegisterService;
import com.kongtrolink.framework.service.DataReportService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    DataReportService reportService;
    @Autowired
    DataRegisterService registerService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean init(){
        logger.info("workerExecute-execute module init");
        //初始化线程池之类的基础任务服务
        //加载告警组合，关联，过滤，延迟等配置信息放到redis中
        //可能还需要加载redis宕机后，数据库中的实时告警信息，如果需要判定信号点AI,DI类型，则需要再告警点中存储
        return true;
    }

    /**
     * @auther: liudd
     * @date: 2019/3/26 13:09
     * 功能描述:实时数据处理模块，暂时不使用线程池
     *      1，直接将实时数据json串更新到redis中作为实时数据，供门户端获取站点
     *      2，解析实时数据，翻译告警数据并更新到redis中
     */
    @Override
    protected RpcNotifyProto.RpcMessage execute(String msgId, String payload){
        Date curDate = new Date();
        //1,将实时数据更新到reids中
        //2，解析
        //发送心跳
//        sendHeartBeat(communication);
        RpcNotifyProto.MessageType response = RpcNotifyProto.MessageType.RESPONSE;
        String result = "";
        ModuleMsg moduleMsg = JSON.parseObject(payload, ModuleMsg.class);
        String pktType = moduleMsg.getPktType();
        if(pktType.equals(PktType.DATA_REPORT)){
            //变化数据上报
            result = reportService.report(msgId, moduleMsg.getPayload(), curDate);
        }else if(pktType.equals(PktType.DATA_REGISTER)){
            result = registerService.register(msgId, moduleMsg.getPayload());
        }

        //发送告警信息给外部
//        sendAlarmInfo(fsu);
        return createResp(response, result, StringUtils.isBlank(msgId)? "" : msgId);
    }






    /**
     * @auther: liudd
     * @date: 2019/3/28 16:38-
     * 功能描述:发送心跳
     */
    private void sendHeartBeat(Communication communication){

    }

    /**
     * @auther: liudd
     * @date: 2019/3/28 20:02
     * 功能描述:发送告警信息
     */
    private void sendAlarmInfo(JsonFsu fsu){
        if(!fsu.getData().isEmpty()){

        }
    }

    private RpcNotifyProto.RpcMessage createResp(RpcNotifyProto.MessageType responseType, String result, String msgId){
        return RpcNotifyProto.RpcMessage.newBuilder()
                .setType(responseType).setPayload(result)
                .setPayloadType(RpcNotifyProto.PayloadType.JSON)
                .setMsgId(StringUtils.isBlank(msgId) ? "" : msgId).build();
    }
}

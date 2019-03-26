package com.kongtrolink.framework.execute.module;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.PktType;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.protobuf.protorpc.RpcNotifyImpl;
import com.kongtrolink.framework.core.service.ModuleInterface;
import com.kongtrolink.framework.execute.module.model.TerminalPayload;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/2/25, 19:25.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class ExecuteModule extends RpcNotifyImpl implements ModuleInterface
{


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    private ThreadPoolTaskExecutor controllerExecutor;

    @Override
    public boolean init()
    {
        logger.info("workerExecute-execute module init");
        //初始化线程池之类的基础任务服务
        return true;
    }

    /**
     * 默认业务程序执行体
     * @param
     * @return
     */

    @Override
    protected RpcNotifyProto.RpcMessage execute(String msgId, String payload)
    {
        String response = "execute result ";
        JSONObject payLoadObject = JSONObject.parseObject(payload);
        String pktType = (String) payLoadObject.get("pktType");
        if (StringUtils.isNotBlank(pktType))//终端处理流程
        {
            if(pktType.equals(PktType.CONNECT)) //终端报文请求
            {
                //解析消息报文
                TerminalPayload terminalPayload = payLoadObject.toJavaObject(TerminalPayload.class);
                msgId = terminalPayload.getMsgId();
                String uuid = terminalPayload.getMsgId();
                String gip = terminalPayload.getGip();
                if (StringUtils.isNotBlank(msgId))
                {
                    JSONObject msgPayload = terminalPayload.getPayload();
                    String SN = (String) msgPayload.get("SN");
                    //根据SN从communication_hash获取记录
                    //不存在该 SN 记录
                    String value = (String) redisTemplate.opsForValue().get(SN);


                    //将路由信息存入redis 数据格式为:{SN:{uuid:uuid,GWip:gip,STU:0}}

                }




            }
        }

        //todo




        return RpcNotifyProto.RpcMessage.newBuilder()
                .setType(RpcNotifyProto.MessageType.RESPONSE)
                .setPayload(response)
                .setMsgId(StringUtils.isBlank(msgId)?"":msgId)
                .build();
    }








}

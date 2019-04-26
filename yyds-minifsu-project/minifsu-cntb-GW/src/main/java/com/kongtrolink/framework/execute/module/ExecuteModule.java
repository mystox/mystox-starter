package com.kongtrolink.framework.execute.module;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.protobuf.protorpc.RpcNotifyImpl;
import com.kongtrolink.framework.core.service.ModuleInterface;
import com.kongtrolink.framework.util.FSUServiceClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
        JSONObject response = new JSONObject();
        boolean result = false;

        ModuleMsg moduleMsg = JSONObject.parseObject(payload, ModuleMsg.class);

        JSONObject infoPayload = moduleMsg.getPayload();

        if (moduleMsg.getPktType().equals("service_gw")) {
            try {
                String reqMsg = infoPayload.getString("msg");
                String ip = infoPayload.getString("ip");
                int port = infoPayload.getInteger("port");
                String resMsg = FSUServiceClientUtil.sendReq(reqMsg, ip, port);
                response.put("msg", resMsg);
                result = (resMsg != null);
            } catch (Exception e) {
                logger.error("向铁塔发送请求异常：" + JSONObject.toJSONString(e));
            }
        }

        //todo
        response.put("result", result ? 1 : 0);

        return RpcNotifyProto.RpcMessage.newBuilder()
                .setType(RpcNotifyProto.MessageType.RESPONSE)
                .setPayloadType(RpcNotifyProto.PayloadType.JSON)
                .setPayload(response.toJSONString())
                .setMsgId(StringUtils.isBlank(msgId)?"":msgId)
                .build();
    }


}

package com.kongtrolink.framework.execute.module;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.core.entity.PktType;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.protobuf.protorpc.RpcNotifyImpl;
import com.kongtrolink.framework.core.service.ModuleInterface;
import com.kongtrolink.framework.service.TowerService;
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

    @Autowired
    private TowerService towerService;

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
        switch (moduleMsg.getPktType()) {
            case PktType.FSU_BIND:
                result = towerService.FsuBind(infoPayload);
                break;
        }

        //todo
        response.put("result", result ? 1 : 0);
//        try
//        {
//            Thread.sleep(new Random().nextInt(10000));
//        } catch (InterruptedException e)
//        {
//            e.printStackTrace();
//        }

        return RpcNotifyProto.RpcMessage.newBuilder()
                .setType(RpcNotifyProto.MessageType.RESPONSE)
                .setPayloadType(RpcNotifyProto.PayloadType.JSON)
                .setPayload(response.toJSONString())
                .setMsgId(StringUtils.isBlank(msgId)?"":msgId)
                .build();
    }


}

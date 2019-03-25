package com.kongtrolink.framework.execute.module;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.Fsu;
import com.kongtrolink.framework.core.entity.PktType;
import com.kongtrolink.framework.core.entity.YwclMessage;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.protobuf.protorpc.RpcNotifyImpl;
import com.kongtrolink.framework.core.service.ModuleInterface;
import com.kongtrolink.framework.execute.module.service.FsuService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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
    FsuService fsuService;


    @Override
    public boolean init()
    {
        logger.info("workerExecute-execute module init");
        //初始化线程池之类的基础任务服务
        return true;
    }

    /**
     * 默认业务程序执行体
     *
     * @param
     * @return
     */

    @Override
    protected RpcNotifyProto.RpcMessage execute(String msgId, String payload)
    {
        //todo
        String result = "";

        YwclMessage ywclMessage = JSONObject.parseObject(payload, YwclMessage.class);
        String pktType = ywclMessage.getPkt_type();
        //根据请求类型 执行相应业务
        if (StringUtils.isNotBlank(pktType))
        {
        switch (pktType)
        {
            case PktType.GET_FSU:
                String fsuId = ywclMessage.getFsu_id();
                JSONObject object = ywclMessage.getData();
                object.put("fsuId", fsuId);
                Map searchCondition = new HashMap();
                Fsu fsu = fsuService.getFsu(object);
                if (fsu!=null)
                    result = JSONObject.toJSONString(fsu);
                break;
             case PktType.CHECK_FSU:
                //todo
                break;

        }


        }




        result = "{\n" +
                "        \"alarms\": [\n" +
                "            {\n" +
                "                \"device_name\": \"123\",\n" +
                "                \"device_id\": \"123\",\n" +
                "                \"alarm_time\": \"20181102 10:01:01\",\n" +
                "                \"reported\": 1,\n" +
                "                \"last_report_time\": \"20181102 10:01:01\",\n" +
                "                \"signal_name\": \"test\",\n" +
                "                \"alarm_lvl\": 1,\n" +
                "                \"alarm_desc\": \"test\",\n" +
                "                \"signal_id\": \"123\",\n" +
                "                \"alarm_flag\": 1,\n" +
                "                \"serial_no\": 123,\n" +
                "                \"event_value\": 1\n" +
                "            }\n" +
                "        ],\n" +
                "        \"count\": 1\n" +
                "    }";



        logger.info("执行业务处理程序....................");
        return RpcNotifyProto.RpcMessage.newBuilder()
                .setType(RpcNotifyProto.MessageType.RESPONSE)
                .setPayload(result)
                .setMsgId(StringUtils.isBlank(msgId) ? "" : msgId)
                .build();
    }


}

package com.kongtrolink.framework.execute.module;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.*;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.protobuf.protorpc.RpcNotifyImpl;
import com.kongtrolink.framework.core.service.ModuleInterface;
import com.kongtrolink.framework.execute.module.service.FsuService;
import com.kongtrolink.framework.execute.module.service.RegistryService;
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
public class ExecuteModule extends RpcNotifyImpl implements ModuleInterface {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ThreadPoolTaskExecutor controllerExecutor;
    @Autowired
    FsuService fsuService;

    @Autowired
    RegistryService registryService;


    @Override
    public boolean init() {
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
    protected RpcNotifyProto.RpcMessage execute(String msgId, String payload) {
        //todo
        String result = "";
        ModuleMsg moduleMsg = JSONObject.parseObject(payload, ModuleMsg.class);
        String pktType = moduleMsg.getPktType();
        if (TerminalPktType.REGISTRY.getValue().equals(pktType)) { // 注册fsu
            //注册服务
            JSONObject jsonObject = registryService.registerSN(moduleMsg);
            result = jsonObject.toJSONString();

        } else if (TerminalPktType.TERMINAL_REPORT.getValue().equals(pktType))
        { // 设备上报// 终端信息上报设备上报
            //注册服务
            JSONObject jsonObject = registryService.registerTerminal(moduleMsg);
            result = jsonObject.toJSONString();
        } else if (TerminalPktType.DEV_LIST.getValue().equals(pktType))
        { // 设备上报// 终端信息上报设备上报
            //注册服务
            JSONObject jsonObject = registryService.registerDevices(moduleMsg);
            result = jsonObject.toJSONString();
        }


        YwclMessage ywclMessage = JSONObject.parseObject(payload, YwclMessage.class);
//        String pktType = ywclMessage.getPkt_type();
        //根据请求类型 执行相应业务
        if (StringUtils.isNotBlank(pktType)) {
            switch (pktType) {
                case PktType.GET_FSU:
                    String fsuId = ywclMessage.getFsu_id();
                    JSONObject object = ywclMessage.getData();
                    object.put("fsuId", fsuId);
                    Map searchCondition = new HashMap();
                    Fsu fsu = fsuService.getFsu(object);
                    if (fsu != null)
                        result = JSONObject.toJSONString(fsu);
                    break;
                case PktType.CHECK_FSU:
                    //todo
                    break;

            }


        }


        logger.info("执行业务处理程序....................");
        return RpcNotifyProto.RpcMessage.newBuilder()
                .setType(RpcNotifyProto.MessageType.RESPONSE)
                .setPayload(result)
                .setMsgId(StringUtils.isBlank(msgId) ? "" : msgId)
                .build();
    }


}

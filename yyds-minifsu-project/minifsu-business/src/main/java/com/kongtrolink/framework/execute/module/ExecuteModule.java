package com.kongtrolink.framework.execute.module;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.core.entity.PktType;
import com.kongtrolink.framework.core.entity.TerminalPktType;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.protobuf.protorpc.RpcNotifyImpl;
import com.kongtrolink.framework.core.service.ModuleInterface;
import com.kongtrolink.framework.execute.module.service.AlarmService;
import com.kongtrolink.framework.execute.module.service.FsuService;
import com.kongtrolink.framework.execute.module.service.LogService;
import com.kongtrolink.framework.execute.module.service.RegistryService;
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
public class ExecuteModule extends RpcNotifyImpl implements ModuleInterface {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ThreadPoolTaskExecutor controllerExecutor;
    @Autowired
    FsuService fsuService;

    @Autowired
    RegistryService registryService;

    @Autowired
    AlarmService alarmService;
    @Autowired
    LogService logService;

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
        String result = "";
        ModuleMsg moduleMsg = JSONObject.parseObject(payload, ModuleMsg.class);
        String pktType = moduleMsg.getPktType();
        if (TerminalPktType.REGISTRY.getValue().equals(pktType)) { // 注册终端
            JSONObject jsonObject = registryService.registerSN(moduleMsg);
            result = jsonObject.toJSONString();
        } else if (TerminalPktType.TERMINAL_REPORT.getValue().equals(pktType)) { //  终端信息上报设备上报
            JSONObject jsonObject = registryService.registerTerminal(moduleMsg);
            result = jsonObject.toJSONString();
        } else if (TerminalPktType.DEV_LIST.getValue().equals(pktType)) { // 设备上报
            JSONObject jsonObject = registryService.registerDevices(moduleMsg);
            result = jsonObject.toJSONString();
        } else if (PktType.CLEANUP.equals(pktType)) { // 注销
            JSONObject jsonObject = registryService.saveCleanupLog(moduleMsg);
            result = jsonObject.toJSONString();
        } else if (PktType.ALARM_SAVE.equals(pktType)) { //告警保存
            JSONObject jsonObject = alarmService.save(moduleMsg);
            result = jsonObject.toJSONString();
        } else if (PktType.LOG_SAVE.equals(pktType)) { //日志保存
            JSONObject jsonObject = logService.saveLog(moduleMsg);
            result = jsonObject.toJSONString();
        }


       /* YwclMessage ywclMessage = JSONObject.parseObject(payload, YwclMessage.class);
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
                    break;

            }


        }
*/

        return RpcNotifyProto.RpcMessage.newBuilder()
                .setType(RpcNotifyProto.MessageType.RESPONSE)
                .setPayloadType(RpcNotifyProto.PayloadType.JSON)
                .setPayload(result)
                .setMsgId(StringUtils.isBlank(msgId) ? "" : msgId)
                .build();
    }


}

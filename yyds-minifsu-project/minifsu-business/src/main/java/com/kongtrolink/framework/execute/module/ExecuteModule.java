package com.kongtrolink.framework.execute.module;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.core.entity.PktType;
import com.kongtrolink.framework.core.entity.TerminalPktType;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.protobuf.protorpc.RpcNotifyImpl;
import com.kongtrolink.framework.core.service.ModuleInterface;
import com.kongtrolink.framework.execute.module.service.*;
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

    private final ThreadPoolTaskExecutor businessExecutor;
    private TerminalService terminalService;

    private DataMntService dataMntService;

    @Autowired
    public void setDataMntService(DataMntService dataMntService) {
        this.dataMntService = dataMntService;
    }


    @Autowired
    public void setTerminalService(TerminalService terminalService) {
        this.terminalService = terminalService;
    }

    private RegistryService registryService;

    @Autowired
    public void setRegistryService(RegistryService registryService) {
        this.registryService = registryService;
    }

    private final AlarmService alarmService;
    private final LogService logService;

    private final FileService fileService;

    @Autowired
    public ExecuteModule(ThreadPoolTaskExecutor businessExecutor, AlarmService alarmService, LogService logService, FileService fileService) {
        this.businessExecutor = businessExecutor;
        this.alarmService = alarmService;
        this.logService = logService;
        this.fileService = fileService;
    }


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
        JSONObject r = new JSONObject();
        r.put("result", 0);
        String result = r.toJSONString();
        ModuleMsg moduleMsg = JSONObject.parseObject(payload, ModuleMsg.class);
        String pktType = moduleMsg.getPktType();
        if (TerminalPktType.REGISTRY.getValue().equals(pktType)) { // 注册终端
            JSONObject jsonObject = registryService.registerSN(moduleMsg);
            result = jsonObject.toJSONString();
        } else if (TerminalPktType.TERMINAL_REPORT.getValue().equals(pktType)) { //  终端信息上报设备上报
            JSONObject jsonObject = registryService.registerTerminal(moduleMsg);
            result = jsonObject.toJSONString();
        } else if (TerminalPktType.HEART.getValue().equals(pktType)) { //  终端信息上报设备上报
            JSONObject jsonObject = registryService.terminalHeart(moduleMsg); //心跳处理
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
        } else if (PktType.FILE_GET.equals(pktType)) {
            byte[] bytes = fileService.fileGet(moduleMsg);
            return RpcNotifyProto.RpcMessage.newBuilder()
                    .setType(RpcNotifyProto.MessageType.RESPONSE)
                    .setPayloadType(RpcNotifyProto.PayloadType.BYTE)
                    .setBytePayload(ByteString.copyFrom(bytes))
                    .setMsgId(StringUtils.isBlank(msgId) ? "" : msgId)
                    .build();
        } else if (PktType.GET_DEVICES.equals(pktType)) { //获取设备列表
            JSONArray jsonObject = terminalService.getDeviceList(moduleMsg);
            result = jsonObject.toJSONString();
        } else if (PktType.GET_FSU.equals(pktType)) { //获取sn列表
            JSONObject jsonObject = terminalService.listFsu(moduleMsg);
            result = jsonObject.toJSONString();
        } else if (PktType.GET_DATA.equals(pktType)) { //获取实时数据
            JSONObject jsonObject = dataMntService.getSignalList(moduleMsg);
            result = jsonObject.toJSONString();
        } else if (PktType.COMPILER.equals(pktType)) { //下载编译文件
            JSONObject jsonObject = fileService.getCompilerFile(moduleMsg);
            result = jsonObject.toJSONString();
        } else if (PktType.SET_ALARM_PARAM.equals(pktType)) { //设置告警点配置
            JSONObject jsonObject = dataMntService.setThreshold(moduleMsg);
            result = jsonObject.toJSONString();
        } else if (PktType.GET_ALARM_PARAM.equals(pktType)) { //获取告警点配置
            JSONArray jsonObject = dataMntService.getThreshold(moduleMsg);
            result = jsonObject.toJSONString();
        } else if (PktType.TERMINAL_SAVE.equals(pktType)) { //获取告警点配置
            JSONObject jsonObject = terminalService.saveTerminal(moduleMsg);
            result = jsonObject.toJSONString();
        } else if (PktType.SET_TERMINAL.equals(pktType)) { // 设置和绑定终端
            JSONObject jsonObject = terminalService.setTerminal(moduleMsg);
            result = jsonObject.toJSONString();
        } else if (PktType.TERMINAL_LOG_SAVE.equals(pktType)) { // 终端流
            JSONObject jsonObject = terminalService.terminalLogSave(moduleMsg);
            result = jsonObject.toJSONString();
        } else if (PktType.ALARM_MODEL_IMPORT.equals(pktType)) { // 终端流
            JSONObject jsonObject = alarmService.saveAlarmModel(moduleMsg);
            result = jsonObject.toJSONString();
        } else if (PktType.SIGNAL_MODEL_IMPORT.equals(pktType)) { // 终端流
            JSONObject jsonObject = dataMntService.saveSignalModel(moduleMsg);
            result = jsonObject.toJSONString();
        } else if (PktType.TERMINAL_STATUS.equals(pktType)) { // 终端流
            JSONObject jsonObject = terminalService.TerminalStatus(moduleMsg);
            result = jsonObject.toJSONString();
        } else if (PktType.GET_ALARMS.equals(pktType)) { // 获取告警列表
            JSONArray jsonObject = alarmService.getAlarms(moduleMsg);
            result = jsonObject.toJSONString();
        } else if (PktType.SET_DATA.equals(pktType)) { // 获取告警列表
            JSONObject jsonObject = dataMntService.setData(moduleMsg);
            result = jsonObject.toJSONString();
        } else if (PktType.RUN_STATUS.equals(pktType)) { // 终端的状态数据
            JSONObject jsonObject = dataMntService.saveRunStatus(moduleMsg);
            result = jsonObject.toJSONString();
        } else if (PktType.GET_RUNSTATE.equals(pktType)) { // 获取运行状态
            JSONObject jsonObject = terminalService.getRunStates(moduleMsg);
            result = jsonObject.toJSONString();
        } else if (PktType.GET_TERMINAL_LOG.equals(pktType)) { // 获取报文日志
            JSONObject jsonObject = terminalService.getTerminalPayloadLog(moduleMsg);
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

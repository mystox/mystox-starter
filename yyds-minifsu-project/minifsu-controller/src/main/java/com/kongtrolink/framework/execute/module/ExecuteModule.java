package com.kongtrolink.framework.execute.module;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.kongtrolink.framework.core.entity.*;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.protobuf.protorpc.RpcNotifyImpl;
import com.kongtrolink.framework.core.service.ModuleInterface;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.execute.module.model.TerminalMsg;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
    RedisUtils redisUtils;

    @Autowired
    RpcModule rpcModule;

    @Value("${server.name}")
    private String serverName;
    @Value("${server.bindIp}")
    private String hostname;
    @Value("${server.rpc.port}")
    private String port;
    @Value("${rpc.business.hostname}")
    private String businessHost;
    @Value("${rpc.business.port}")
    private int businessPort;


    @Value("${rpc.monitor.hostname}")
    private String monitorHost;
    @Value("${rpc.monitor.port}")
    private int monitorPort;
    @Value("${rpc.tower.hostname}")
    private String towerHost;
    @Value("${rpc.tower.port}")
    private int towerPort;


    @Value("${redis.communication.expired:1200}")
    private long communicationExpired;

    @Autowired
    private ThreadPoolTaskExecutor controllerExecutor;

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
        String response = "";
        JSONObject payloadObject = JSONObject.parseObject(payload);
        String pktType = (String) payloadObject.get("pktType");//服务间通讯类型
        if (StringUtils.isNotBlank(pktType)) {
            if (PktType.CONNECT.equals(pktType)) {                          //终端>>>>>>>>>服务
                Object result = receiveTerminalExecute(msgId, payloadObject);
                return responseMsg(result, msgId);
            } else if (PktType.UPGRADE.equals(pktType)
                    || PktType.SET_DATA_TERMINAL.equals(pktType)
                    || PktType.TERMINAL_REBOOT.equals(pktType)
                    ) {                                                     //服务>>>>>>>>终端
                logger.info(">>>>>>>>>>terminal==={}===", payloadObject.toJSONString());
                JSON result = sendTerminalExecute(msgId, payloadObject);
                return responseMsg(result, msgId);

            } else {                                                         //服务>>>>>>>>>>>>服务
                JSON result = moduleExecute(msgId, payloadObject);
                return responseMsg(result, msgId);
            }
        }
        JSONObject result = new JSONObject();
        result.put("result", 0);
        result.put("msgId", msgId);
        return responseMsg(result, msgId);
    }


    /**
     * 构造返回实体
     *
     * @param result 响应实体
     * @param msgId  消息id
     * @return 返回payload实例
     */
    public RpcNotifyProto.RpcMessage responseMsg(Object result, String msgId) {

        if (result == null) {
            JSONObject jsonResult = new JSONObject();
            jsonResult.put("result", 0);
            jsonResult.put("msgId", msgId);
            return RpcNotifyProto.RpcMessage.newBuilder()
                    .setType(RpcNotifyProto.MessageType.ERROR)
                    .setPayloadType(RpcNotifyProto.PayloadType.JSON)
                    .setPayload(jsonResult.toJSONString())
                    .setMsgId(StringUtils.isBlank(msgId) ? "" : msgId)
                    .build();
        }
        if (result instanceof ByteString) { //终端响应文件流...
            return RpcNotifyProto.RpcMessage.newBuilder()
                    .setType(RpcNotifyProto.MessageType.RESPONSE)
                    .setPayloadType(RpcNotifyProto.PayloadType.BYTE)
                    .setBytePayload((ByteString) result)
                    .setMsgId(StringUtils.isBlank(msgId) ? "" : msgId)
                    .build();
        }
        return RpcNotifyProto.RpcMessage.newBuilder()
                .setType(RpcNotifyProto.MessageType.RESPONSE)
                .setPayloadType(RpcNotifyProto.PayloadType.JSON)
                .setPayload(((JSON) result).toJSONString())
                .setMsgId(StringUtils.isBlank(msgId) ? "" : msgId)
                .build();

    }

    /**
     * 处理发送至终端的消息 服务>>>>>>>终端
     *
     * @return
     */
    private JSON sendTerminalExecute(String msgId, JSONObject payloadObject) {
        ModuleMsg moduleMsg = JSONObject.toJavaObject(payloadObject, ModuleMsg.class);
        JSONObject msgPayload = moduleMsg.getPayload();
        msgId = StringUtils.isBlank(msgId) ? moduleMsg.getMsgId() : msgId;
        String sn = moduleMsg.getSN();
        String pktType = moduleMsg.getPktType();
        //获取终端pktType
        int pktTypeInt = TerminalPktType.toKey(pktType);
        JSONObject result = new JSONObject();
        result.put("result", 0);
        if (pktTypeInt == -1) return result;
        msgPayload.put("pktType", pktTypeInt);
        //payload组装消息成终端消息格式{"msgId":"000009","pkgSum":1,"ts":1552043047,"payload":{"pktType":4,"result":1}}
        TerminalMsg requestMsg = new TerminalMsg();
        requestMsg.setPayload(msgPayload);
        requestMsg.setMsgId(msgId);
        //将消息放入payload
        moduleMsg.setPayload((JSONObject) JSONObject.toJSON(requestMsg));
        JSONObject snCommunication = null;
        try {
            //根据SN获取uuid和gip
            String key = RedisHashTable.COMMUNICATION_HASH + ":" + sn;
            snCommunication = redisUtils.get(key, JSONObject.class);
            if (snCommunication == null) {
                // 错误信息记录日志
                Log log = new Log();
                log.setMsgType(moduleMsg.getPktType());
                log.setSN(sn);
                log.setTime(new Date());
                log.setServiceName(serverName);
                log.setHostName(hostname);
                log.setMsgId(msgId);
                log.setErrorCode(StateCode.CONNECT_ERROR);
                moduleMsg.setPktType(PktType.LOG_SAVE);
                moduleMsg.setPayload((JSONObject) JSONObject.toJSON(log));
                sendPayLoad(msgId, JSONObject.toJSONString(moduleMsg), businessHost, businessPort);
                return result;
            }

            String uuid = (String) snCommunication.get("UUID");
            moduleMsg.setUuid(uuid);
            //发送至网关
            String netAddr = (String) snCommunication.get("GWip");
            String[] netAddrArr = netAddr.split(":");
            return sendPayLoad(msgId, JSONObject.toJSONString(moduleMsg), netAddrArr[0], Integer.parseInt(netAddrArr[1]));
        } catch (Exception e) {
            e.printStackTrace();
            // 错误信息记录日志
            Log log = new Log();
            log.setMsgType(moduleMsg.getPktType());
            log.setSN(sn);
            log.setTime(new Date());
            log.setServiceName(serverName);
            log.setHostName(hostname);
            log.setMsgId(msgId);
            log.setErrorCode(StateCode.REDIS_ERROR);
            moduleMsg.setPktType(PktType.LOG_SAVE);
            moduleMsg.setPayload((JSONObject) JSONObject.toJSON(log));
            sendPayLoad(msgId, JSONObject.toJSONString(moduleMsg), businessHost, businessPort);
            return result;
        }

    }

    /**
     * 服务间的消息 服务>>>>>>>服务
     *
     * @return
     */
    private JSON moduleExecute(String msgId, JSONObject payloadObject) {
        ModuleMsg moduleMsg = JSONObject.toJavaObject(payloadObject, ModuleMsg.class);
        String pktType = moduleMsg.getPktType();
        //>>>>>>>>>>>>>>>>>>>>>>>>>通往事务处理 >>>>>>>>>>>>>>>>>>>
        if (PktType.CLEANUP.equals(pktType)//注销
                || PktType.ALARM_SAVE.equals(pktType) //告警保存
                || PktType.LOG_SAVE.equals(pktType) //日志保存
                || PktType.GET_DEVICES.equals(pktType) //获取设备列表
                || PktType.GET_DATA.equals(pktType) //获取实时数据
                || PktType.SET_DATA.equals(pktType) //设置信号点值
                || PktType.SET_STATION.equals(pktType) //设置终端或绑定
                || PktType.SET_ALARM_PARAM.equals(pktType)
                || PktType.DATA_REGISTER.equals(pktType) //外报数据解析
                || PktType.GET_ALARM_PARAM.equals(pktType)
                || PktType.GET_ALARMS.equals(pktType) //web <--- 获取告警
                || PktType.COMPILER.equals(pktType)
                || PktType.TERMINAL_SAVE.equals(pktType)
                || PktType.ALARM_MODEL_IMPORT.equals(pktType)
                || PktType.SIGNAL_MODEL_IMPORT.equals(pktType)
                || PktType.TERMINAL_STATUS.equals(pktType)
                || PktType.SET_TERMINAL.equals(pktType)
                || PktType.GET_RUNSTATE.equals(pktType)
                || PktType.FSU_UNBIND.equals(pktType)
                || PktType.GET_TERMINAL_LOG.equals(pktType)
                || PktType.GET_FSU.equals(pktType)) {
            logger.info("[{}]>>>>>>>>>>business==={}===", msgId, payloadObject);
            return sendPayLoad(msgId, payloadObject.toJSONString(), businessHost, businessPort);
        }
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>通往外部服务 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        if (PktType.REGISTRY_CNTB.equals(pktType) //business ---> 注册终端
                || PktType.ALARM_REGISTER.equals(pktType) // monitor ---> 注册告警
                || PktType.HEART.equals(pktType) // business ---> 心跳
                || PktType.FSU_BIND.equals(pktType) // business ---> 绑定
                || PktType.DATA_STATUS.equals(pktType) //business ---> 运行状态上报
                || PktType.TERMINAL_UNBIND.equals(pktType) //business ---> 绑定
                || PktType.DATA_REPORT.equals(pktType) //monitor ---> 实时数据上报
                || PktType.DATA_CHANGE.equals(pktType) //monitor ---> 变化数据上报
                ) { // 铁塔事务的路由由BIP 决定 towHost/towerPort来源于redis.BIP
            ModuleMsg msg = payloadObject.toJavaObject(ModuleMsg.class);
            logger.info("[{}]>>>>>>>>>>thirdParty==={}===", msgId, msg);
            String addrStr = "";
            JSONObject payload = msg.getPayload();
            if (payload != null) { //绑定|解除绑定需要带BIP
                addrStr = payload.getString("BIP");
            }
            if (StringUtils.isBlank(addrStr)) {
                //获取通讯信息中外部服务ip
                String key = RedisHashTable.COMMUNICATION_HASH + ":" + msg.getSN();
                JSONObject value = redisUtils.get(key, JSONObject.class);
                if (value != null && (Integer) value.get("STATUS") == 2) {
                    addrStr = (String) value.get("BIP");
                }
            }

            if (StringUtils.isNotBlank(addrStr)) {
                String[] addrs = addrStr.split(";");
                for (String addr : addrs) {
                    if (StringUtils.isNotBlank(addr) && addr.contains(":")) {
                        String[] addrArr = addr.split(":");
                        JSONObject registerJson = moduleMsg.getPayload();
                        registerJson.put("innerIp", hostname);
                        registerJson.put("innerPort", port);
                        return sendPayLoad(msgId, JSONObject.toJSONString(msg), addrArr[0], Integer.parseInt(addrArr[1]));
                    } else {
                        logger.error("[{}]bip[{}] illegal...", msgId, addr);
                    }
                }
            } else {
                logger.warn("[{}]BIP{} is NULL...send to default ", msgId, addrStr);
            }
        }
        JSONObject responsePayload = new JSONObject();
        responsePayload.put("result", StateCode.FAILED);
        return responsePayload;

    }

    /**
     * 处理来自终端的消息 终端>>>>>>>服务
     *
     * @param payloadObject
     * @return
     */
    private Object receiveTerminalExecute(String msgId, JSONObject payloadObject) {
        //解析消息报文
        String terminalString = (String) payloadObject.get("payload");
        if (StringUtils.isBlank(terminalString)) return null;
        TerminalMsg terminalMsg = JSONObject.parseObject(terminalString, TerminalMsg.class);
        if (terminalMsg == null) return null;
        msgId = StringUtils.isBlank(msgId) ? terminalMsg.getMsgId() : msgId;
        String uuid = (String) payloadObject.get("uuid");
        String gip = (String) payloadObject.get("gip");
        JSONObject msgPayload = terminalMsg.getPayload();
        String SN = msgPayload.getString("SN");
        terminalPayloadSave(msgId, SN, terminalString); //终端接收的报文保存
        TerminalMsg terminalResp = new TerminalMsg(); //响应终端消息实体 payload
        terminalResp.setMsgId(msgId);
        if (msgPayload == null) {
            logger.error("[{}]payload is null...[{}]", msgId, payloadObject);
            JSONObject responsePayload = new JSONObject();
            responsePayload.put("result", StateCode.JSON_ILLEGAL);
            terminalResp.setPayload(responsePayload);
            return terminalResponse(msgId, SN, terminalResp);
        }

        ModuleMsg moduleMsg = new ModuleMsg(); //服务间消息实体
        moduleMsg.setMsgId(msgId);
        moduleMsg.setUuid(uuid);
        moduleMsg.setSN(SN);
        /******************************通讯信息刷新*****************************/
        try {
            communicationRefresh(SN, gip, uuid);
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject responsePayload = new JSONObject();
            responsePayload.put("result", StateCode.FAILED);
            responsePayload.put("msgId", msgId);
            // 错误信息记录日志
            Log log = new Log();
            log.setMsgType((String) payloadObject.get("pktType"));
            log.setSN(SN);
            log.setTime(new Date());
            log.setServiceName(serverName);
            log.setHostName(hostname);
            log.setMsgId(msgId);
            log.setErrorCode(StateCode.REDIS_ERROR);
            moduleMsg.setPktType(PktType.LOG_SAVE);
            moduleMsg.setPayload((JSONObject) JSONObject.toJSON(log));
            sendPayLoad(msgId, JSONObject.toJSONString(moduleMsg), businessHost, businessPort);
            terminalResp.setPayload(responsePayload);
            return terminalResponse(msgId, SN, terminalResp);
        }

        //终端payload报文解析
        JSONObject payload = terminalMsg.getPayload();
        int pktType = (int) payload.get("pktType");//终端报文类型 整型
        moduleMsg.setPayload(payload);
       /* if (TerminalPktType.HEART.getKey() == pktType) {
            JSONObject responsePayload = new JSONObject();
            responsePayload.put("result", StateCode.SUCCESS);
            responsePayload.put("pktType", pktType);
            terminalResp.setPayload(responsePayload);
            sendPayLoad(msgId, JSONObject.toJSONString(moduleMsg), towerHost, towerPort);
            return JSONObject.toJSON(terminalResp);
        }*/
        /***************************注册流程*********************************/
        if (TerminalPktType.REGISTRY.getKey() == pktType) {
            moduleMsg.setPktType(TerminalPktType.toValue(pktType));
            logger.info("[{}]terminal>>>>>>>>>>business==={}===", msgId, payloadObject);
            JSONObject responsePayload = (JSONObject) sendPayLoad(msgId, JSONObject.toJSONString(moduleMsg), businessHost, businessPort); //事务处理
            int result = (int) responsePayload.get("result");
            if (result == StateCode.FAILED) {
                //注册异常返回延迟时间
                if (responsePayload.get("delay") != null) {
                    responsePayload.put("delay", 120 + (long) (Math.random() * 120));
                }
            }
            responsePayload.put("pktType", pktType);
            terminalResp.setPayload(responsePayload);
            return terminalResponse(msgId, SN, terminalResp);
        }
        /************终端信息上报 pktType:2 设备信息上报 pktType:3 **********************/
        if (TerminalPktType.TERMINAL_REPORT.getKey() == pktType
                || TerminalPktType.DEV_LIST.getKey() == pktType
                || TerminalPktType.RUN_STATE.getKey() == pktType
                || TerminalPktType.HEART.getKey() == pktType //心跳
                ) {
            moduleMsg.setPktType(TerminalPktType.toValue(pktType));
            logger.info("[{}]terminal>>>>>>>>>>business==={}===", msgId, payloadObject);
            JSONObject responsePayload = (JSONObject) sendPayLoad(msgId, JSONObject.toJSONString(moduleMsg), businessHost, businessPort); //事务处理
            terminalResp.setPayload(responsePayload);
            responsePayload.put("pktType", pktType);
            return terminalResponse(msgId, SN, terminalResp);
        }
        /****************************数据变化上报DATA_CHANGE 实时数据上报DATA_REPORT*******************************/
        if (TerminalPktType.DATA_REPORT.getKey() == pktType || TerminalPktType.DATA_CHANGE.getKey() == pktType) {
            moduleMsg.setPktType(TerminalPktType.toValue(pktType));
            logger.info("[{}]terminal>>>>>>>>>>business==={}===", msgId, payloadObject);
            JSONObject responsePayload = (JSONObject) sendPayLoad(msgId, JSONObject.toJSONString(moduleMsg), monitorHost, monitorPort); //>>>>实时监控处理
            responsePayload.put("pktType", pktType);
            terminalResp.setPayload(responsePayload);
            return terminalResponse(msgId, SN, terminalResp);
        }
        /***************************** 请求文件流 ******************************************/
        if (TerminalPktType.FILE_GET.getKey() == pktType) {
            moduleMsg.setPktType(TerminalPktType.toValue(pktType));
            try {
                logger.info("[{}]terminal>>>>>>>>>>business==={}===", msgId, payloadObject);
                RpcNotifyProto.RpcMessage response = rpcModule.postMsg(msgId, new InetSocketAddress(businessHost, businessPort), JSONObject.toJSONString(moduleMsg));
                if (RpcNotifyProto.MessageType.ERROR.equals(response.getType()))//错误请求信息
                {
                    // 错误信息记录日志
                    Log log = new Log();
                    log.setMsgType((String) payloadObject.get("pktType"));
                    log.setSN(SN);
                    log.setTime(new Date());
                    log.setServiceName(serverName);
                    log.setHostName(hostname);
                    log.setMsgId(msgId);
                    log.setErrorCode(StateCode.FAILED);
                    moduleMsg.setPktType(PktType.LOG_SAVE);
                    moduleMsg.setPayload((JSONObject) JSONObject.toJSON(log));
                    sendPayLoad(msgId, JSONObject.toJSONString(moduleMsg), businessHost, businessPort);
                    return new Byte[]{0}; // 错误返回0
                } else {
                    if (RpcNotifyProto.PayloadType.BYTE == response.getPayloadType()) { //如果返回为文件流则获取流返回
                        return response.getBytePayload();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        JSONObject responsePayload = new JSONObject();
        responsePayload.put("msgId", msgId);
        responsePayload.put("result", StateCode.FAILED);
        // 错误信息记录日志
        Log log = new Log();
        log.setMsgType((String) payloadObject.get("pktType"));
        log.setSN(SN);
        log.setTime(new Date());
        log.setServiceName(serverName);
        log.setHostName(hostname);
        log.setMsgId(msgId);
        log.setErrorCode(StateCode.FAILED);
        terminalResp.setPayload(responsePayload);
        moduleMsg.setPktType(PktType.LOG_SAVE);
        moduleMsg.setPayload((JSONObject) JSONObject.toJSON(log));
        sendPayLoad(msgId, JSONObject.toJSONString(moduleMsg), businessHost, businessPort);
        return terminalResponse(msgId, SN, terminalResp);

    }

    /**
     * 终端日志保存
     *
     * @param msgId
     * @param sn
     * @param terminalResp
     * @return
     */
    Object terminalResponse(String msgId, String sn, TerminalMsg terminalResp) {
        terminalPayloadSave(msgId, sn, terminalResp); //----------------------->记录终端响应日志
        return JSONObject.toJSON(terminalResp);
    }

    /**
     * 保存终端双向报文日志 异步保存
     *
     * @param msgId
     * @param payload
     */
    private void terminalPayloadSave(String msgId, String SN, Object payload) {
        controllerExecutor.execute(() -> {
            ModuleMsg moduleMsg = null;
            if (payload != null && payload instanceof JSON) {//非空json结果保存
                moduleMsg = new ModuleMsg(PktType.TERMINAL_LOG_SAVE, SN, (JSONObject) payload);

            } else if (payload != null && payload instanceof String) {
                JSONObject jsonPayload = JSONObject.parseObject((String) payload);
                if (jsonPayload.get("uuid") != null) jsonPayload.remove("uuid");
                if (jsonPayload.get("gip") != null) jsonPayload.remove("gip");
                moduleMsg = new ModuleMsg(PktType.TERMINAL_LOG_SAVE, SN, jsonPayload);
            } else if (payload != null && payload instanceof TerminalMsg) {
                moduleMsg = (ModuleMsg) payload;
                moduleMsg.setPktType(PktType.TERMINAL_LOG_SAVE);
            } else {
                return;
            }
            try {
                moduleMsg.setMsgId(msgId);
                logger.debug("[{}]terminal_log>>>>>>>>>>business terminal log save ==={}===", msgId, JSONObject.toJSONString(moduleMsg));
                sendPayLoad(msgId, JSONObject.toJSONString(moduleMsg), businessHost, businessPort); //异步保存
            } catch (Exception e) {
                logger.debug("[{}]terminal_log>>>>>>>>>>business terminal log save error ==={}===", msgId, JSONObject.toJSONString(moduleMsg));
                e.printStackTrace();
            }
        });


    }

    /**
     * 通讯信息刷新
     *
     * @param sn
     * @param gip
     * @param uuid
     */
    private void communicationRefresh(String sn, String gip, String uuid) {
        //根据SN从communication_hash获取记录
        //不存在该 SN 记录
        String key = RedisHashTable.COMMUNICATION_HASH + ":" + sn;
        JSONObject value = redisUtils.get(key, JSONObject.class);
        Long expiredTime = 0L;
        if (value == null) {//添加通讯信息
            value = new JSONObject();
            value.put("GWip", gip);
            value.put("UUID", uuid);
            value.put("STATUS", 0);//设置未注册状态
            //触发重新注册
            redisUtils.set(key, value);
            //将路由信息存入redis 数据格式为:{SN:{uuid:uuid,GWip:gip,STATUS:0}}
        } else {
            String uuid2 = (String) value.get("UUID");//uuid发生变化 表示终端重连 则更新通讯路由信息
            if (!uuid.equals(uuid2)) {
                value.put("GWip", gip);
                value.put("UUID", uuid);
                redisUtils.set(key, value);
            }
            Object expired = value.get("expired");
            if (expired != null) expiredTime = ((Integer) expired).longValue();
        }

        // 设置过期时间
        if (expiredTime == 0)
            expiredTime = communicationExpired;
        redisUtils.expired(key, expiredTime, TimeUnit.SECONDS);
    }

    /**
     * 发送至事务处理的消息
     *
     * @param msgId
     * @param payload
     * @return
     */
    private JSON sendPayLoad(String msgId, String payload, String host, int port) {
        JSONObject result = new JSONObject();
        RpcNotifyProto.RpcMessage response = null;
        try {
            response = rpcModule.postMsg(msgId, new InetSocketAddress(host, port), payload);
            if (RpcNotifyProto.MessageType.ERROR.equals(response.getType()))//错误请求信息
            {
                result.put("result", 0);
            } else {
                return (JSON) JSON.parse(response.getPayload());
            }
        } catch (Throwable e) {
            logger.error("[{}] [{}]rpc request error...[{}]", msgId, host + ":" + port, e.toString());
            e.printStackTrace();
            result.put("result", 0);
        }
        return result;
    }


}

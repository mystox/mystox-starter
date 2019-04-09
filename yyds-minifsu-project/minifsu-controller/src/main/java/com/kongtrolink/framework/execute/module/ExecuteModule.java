package com.kongtrolink.framework.execute.module;

import com.alibaba.fastjson.JSONObject;
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

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Random;
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
            if (PktType.CONNECT.equals(pktType)) //终端>>>>>>>>>服务
            {
                JSONObject result = receiveTerminalExecute(msgId, payloadObject);
                response = result.toJSONString();
            } else if (PktType.SET_ALARM_PARAM.equals(pktType)) //服务>>>>>>>>终端 //
            {
                JSONObject result = sendTerminalExecute(msgId, payloadObject);
                response = result.toJSONString();
            } else //服务>>>>>>>>>>>>服务
            {
                JSONObject result = moduleExecute(msgId, payloadObject);
                response = result.toJSONString();
            }
        } else response = "{'result':0,'msgId':'" + msgId + "'}";

        return RpcNotifyProto.RpcMessage.newBuilder()
                .setType(RpcNotifyProto.MessageType.RESPONSE)
                .setPayloadType(RpcNotifyProto.PayloadType.JSON)
                .setPayload(response)
                .setMsgId(StringUtils.isBlank(msgId) ? "" : msgId)
                .build();
    }

    /**
     * 处理发送至终端的消息 服务>>>>>>>终端
     *
     * @return
     */
    private JSONObject sendTerminalExecute(String msgId, JSONObject payloadObject) {
        ModuleMsg moduleMsg = JSONObject.toJavaObject(payloadObject, ModuleMsg.class);
        JSONObject msgPayload = moduleMsg.getPayload();
        msgId = StringUtils.isBlank(msgId) ? moduleMsg.getMsgId() : msgId;
        String sn = (String) payloadObject.get("SN");
        //payload组装消息成终端消息格式{"msgId":"000009","pkgSum":1,"ts":1552043047,"payload":{"pktType":4,"result":1}}
        TerminalMsg requestMsg = new TerminalMsg();
        requestMsg.setPayload(msgPayload);
        requestMsg.setMsgId(msgId);
        //将消息放入payload
        moduleMsg.setPayload((JSONObject) JSONObject.toJSON(requestMsg));

        //根据SN获取uuid和gip
        JSONObject snCommunication = redisUtils.getHash(RedisHashTable.COMMUNICATION_HASH, sn, JSONObject.class);
        String uuid = (String) snCommunication.get("UUID");
        moduleMsg.setUuid(uuid);

        //发送至网关
        String netAddr = (String) snCommunication.get("GWip");
        String[] netAddrArr = netAddr.split(":");
        return sendPayLoad(msgId, JSONObject.toJSONString(moduleMsg), netAddrArr[0], Integer.parseInt(netAddrArr[1]));
    }

    /**
     * 服务间的消息 服务>>>>>>>服务
     *
     * @return
     */
    private JSONObject moduleExecute(String msgId, JSONObject payloadObject) {
        ModuleMsg moduleMsg = JSONObject.toJavaObject(payloadObject, ModuleMsg.class);
        String pktType = moduleMsg.getPktType();
        if (PktType.CLEANUP.equals(pktType) || PktType.ALARM_SAVE.equals(pktType) || PktType.LOG_SAVE.equals(pktType)) {//注销
            return sendPayLoad(msgId, payloadObject.toJSONString(), businessHost, businessPort);
        }

        //告警上报，模拟随机返回成功和失败
        if(PktType.ALARM_REGISTER.equals(pktType)){
            JSONObject responsePayload = new JSONObject();
            int result = new Random().nextBoolean() ? 1 : 0;
            responsePayload.put("result", result);
            return responsePayload;
        }

        if (PktType.REGISTRY_CNTB.equals(pktType)) { // 铁塔事务的路由由BIP 决定 towHost/towerPort来源于redis.BIP
            ModuleMsg msg = payloadObject.toJavaObject(ModuleMsg.class);
            String key = RedisHashTable.COMMUNICATION_HASH + ":" + msg.getSN();
            JSONObject value = redisUtils.get(key, JSONObject.class);
            if (value != null && (Integer) value.get("STATUS") == 2) {
                String addrStr = (String) value.get("BIP");
                String[] addrs = addrStr.split(";");
                for (String addr : addrs) {
                    if (StringUtils.isNotBlank(addr) && addr.contains(":")) {
                        String[] addrArr = addr.split(":");
                        return sendPayLoad(msgId, payloadObject.toJSONString(), addrArr[0], Integer.parseInt(addrArr[1]));
                    } else {
                        logger.error("bip[{}] illegal...");
                    }
                }
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
    private JSONObject receiveTerminalExecute(String msgId, JSONObject payloadObject) {
        //解析消息报文
        String terminalString = (String) payloadObject.get("payload");
        TerminalMsg terminalMsg = JSONObject.parseObject(terminalString, TerminalMsg.class);
        msgId = StringUtils.isBlank(msgId) ? terminalMsg.getMsgId() : msgId;
        String uuid = (String) payloadObject.get("uuid");
        String gip = (String) payloadObject.get("gip");
        JSONObject msgPayload = terminalMsg.getPayload();
        String SN = (String) msgPayload.get("SN");
        TerminalMsg terminalResp = new TerminalMsg(); //响应终端消息实体 payload
        terminalResp.setMsgId(msgId);
        ModuleMsg moduleMsg = new ModuleMsg(); //服务间消息实体
        moduleMsg.setMsgId(msgId);
        moduleMsg.setSN(SN);
        /******************************通讯信息刷新*****************************/
        try {
            if (StringUtils.isNotBlank(msgId)) {
                //根据SN从communication_hash获取记录
                //不存在该 SN 记录
                String key = RedisHashTable.COMMUNICATION_HASH + ":" + SN;
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
                    if (expired!=null) expiredTime = ((Integer)expired).longValue();
                }

                // 设置过期时间
                if (expiredTime == 0)
                    expiredTime = communicationExpired;
                redisUtils.expired(key, expiredTime, TimeUnit.SECONDS);
            }
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
            return (JSONObject) JSONObject.toJSON(terminalResp);
        }

        //终端payload报文解析
        JSONObject payload = terminalMsg.getPayload();
        int pktType = (int) payload.get("pktType");//终端报文类型 整型


        moduleMsg.setPayload(payload);
        /***************************注册流程*********************************/
        if (TerminalPktType.REGISTRY.getKey() == pktType) {
            moduleMsg.setPktType(TerminalPktType.toValue(pktType));
            JSONObject responsePayload = sendPayLoad(msgId, JSONObject.toJSONString(moduleMsg), businessHost, businessPort); //事务处理
            int result = (int) responsePayload.get("result");
            if (result == StateCode.FAILED) {
                //注册异常返回延迟时间
                if (responsePayload.get("delay") != null) {
                    responsePayload.put("delay", 120+(long)(Math.random()*120));
                }
            }
            responsePayload.put("pktType", pktType);
            terminalResp.setPayload(responsePayload);
            return (JSONObject) JSONObject.toJSON(terminalResp);
        }
        /************终端信息上报 pktType:2 设备信息上报 pktType:3 **********************/
        if (TerminalPktType.TERMINAL_REPORT.getKey() == pktType
                || TerminalPktType.DEV_LIST.getKey() == pktType
                || TerminalPktType.FILE_GET.getKey() == pktType) {
            moduleMsg.setPktType(TerminalPktType.toValue(pktType));
            JSONObject responsePayload = sendPayLoad(msgId, JSONObject.toJSONString(moduleMsg), businessHost, businessPort); //事务处理
            terminalResp.setPayload(responsePayload);
            responsePayload.put("pktType", pktType);
            return (JSONObject) JSONObject.toJSON(terminalResp);
        }
        /****************************数据变化上报*******************************/
        if (TerminalPktType.DATA_REPORT.getKey() == pktType || TerminalPktType.DATA_CHANGE.getKey() == pktType) {
            moduleMsg.setPktType(TerminalPktType.toValue(pktType));
            JSONObject responsePayload = sendPayLoad(msgId, JSONObject.toJSONString(moduleMsg), monitorHost, monitorPort); //>>>>实时监控处理
            responsePayload.put("pktType", pktType);
            terminalResp.setPayload(responsePayload);
            return (JSONObject) JSONObject.toJSON(terminalResp);
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
        return (JSONObject) JSONObject.toJSON(terminalResp);

    }

    /**
     * 发送至事务处理的消息
     *
     * @param msgId
     * @param payload
     * @return
     */
    private JSONObject sendPayLoad(String msgId, String payload, String host, int port) {
        JSONObject result = new JSONObject();
        RpcNotifyProto.RpcMessage response = null;
        try {
            response = rpcModule.postMsg(msgId, new InetSocketAddress(host, port), payload);
            if (RpcNotifyProto.MessageType.ERROR.equals(response.getType()))//错误请求信息
            {
                result.put("result", 0);
            } else {
                return JSONObject.parseObject(response.getPayload());
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("result", 0);
        }
        return result;
    }


}

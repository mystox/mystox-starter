package com.kongtrolink.gateway.nb.ctcc.execute;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.kongtrolink.framework.core.entity.NorthMessage;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.utils.Hex;
import com.kongtrolink.gateway.nb.ctcc.entity.MiniMqttMessageRequest;
import com.kongtrolink.gateway.nb.ctcc.entity.PackageInfo;
import com.kongtrolink.gateway.nb.ctcc.execute.module.RpcModule;
import com.kongtrolink.gateway.nb.ctcc.iot.service.NbIotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * xx
 * by Mag on 2019/3/22.
 */
@Service
public class SendTool {

    private static final Logger logger = LoggerFactory.getLogger(SendTool.class);

    private static Map<String, List<PackageInfo>> map = new HashMap<>();

    @Value("${send.synchronization.mark:true}")
    private boolean synMark;
    @Value("${rpc.controller.port}")
    private int toPort;

    @Value("${rpc.controller.hostname}")
    private String toHost;

    @Value("${server.name}")
    private String serverName;

    @Value("${server.bindIp}")
    private String serverIp;


    @Value("${server.rpc.port}")
    private String rpcPort;


    @Autowired
    RpcModule rpcModule;


    @Autowired
    NbIotService nbIotService;
    private InetSocketAddress addr = null;

    public RpcNotifyProto.RpcMessage sendMsgTest(String msgId, String message) {
        try {
            if (addr == null) {
                this.addr = new InetSocketAddress(toHost, toPort);
            }
            RpcNotifyProto.RpcMessage result = rpcModule.postMsg("", this.addr, message);
            logger.info("收到 rpc 返回: {} ", result.toString());
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public InetSocketAddress getAddr() {
        return addr;
    }

    public void setAddr(InetSocketAddress addr) {
        this.addr = addr;
    }

    public String getServerName() {
        return serverName;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void clearMap(String deviceId, String msgId) {
        String key = msgId + "#" + deviceId;
        this.map.remove(key);
    }

    public static Map<String, List<PackageInfo>> getMap() {
        return map;
    }

    /**
     * 向 适配平台发送 MQTT消息
     * 组装好
     */
    public void sendOmcMessage(Object pdu, String deviceId) {
        MiniMqttMessageRequest request = new MiniMqttMessageRequest();
        request.setPdu(pdu);
        request.setDeviceId(deviceId);
        String requestInfo = JSONObject.toJSONString(request);
        requestInfo = requestInfo.replaceAll("\\\\\\\\", "#&");//三个 斜杠 替换
        requestInfo = requestInfo.replaceAll("#&", "");
//        sendMsgTest(requestInfo);
    }


    /**
     * 向NB终端 发送json类型
     *
     * @param deviceId
     * @param s
     * @throws Exception
     */
    public void jsonResult(String deviceId, String s) throws Exception {
        nbIotService.postNbCommand(deviceId, "sendCmd", "sendCmd", s);
       /* BaseAck resources = nbIotService.getResources(imei);
        ResourceAck ack = EntityUtil.getEntity(resources, ResourceAck.class);
        ResourceItem resourceItem = ack.getItem().get(0);
        Integer objId = resourceItem.getObj_id();
        ResourceInstances resourceInstances = resourceItem.getInstances().get(0);
        Integer resId = resourceInstances.getResources()[0];
        Integer instId = resourceInstances.getInst_id();
        nbIotService.command(s, imei, objId, instId, resId);*/
    }

    /**
     * 向NB终端发送byte流
     *
     * @param deviceId
     * @param bytePayload
     * @throws Exception
     */
    public void returnByte(String deviceId, ByteString bytePayload) throws Exception {
        nbIotService.postNbCommand(deviceId, "sendCmd", "sendCmd", Hex.byte2HexStr(bytePayload.toByteArray()));
        /*BaseAck resources = nbIotService.getResources(imei);
        ResourceAck ack = EntityUtil.getEntity(resources, ResourceAck.class);
        ResourceItem resourceItem = ack.getItem().get(0);
        Integer objId = resourceItem.getObj_id();
        ResourceInstances resourceInstances = resourceItem.getInstances().get(0);
        Integer resId = resourceInstances.getResources()[0];
        Integer instId = resourceInstances.getInst_id();
        nbIotService.command(bytePayload.toString(), imei, objId, instId, resId);*/
    }


    public void sendRpcMessage(String value, String deviceId) {
        logger.info(" ----> 发送rpc消息字符串串:" + value);
        NorthMessage northMessage = new NorthMessage();
        northMessage.setUuid(deviceId);
        northMessage.setPayload(value);
        northMessage.setGip(serverIp + ":" + rpcPort);
        String msg = JSONObject.toJSONString(northMessage);
        RpcNotifyProto.RpcMessage result = sendMsgTest("", msg);

        if (result != null && isSynMark()) {//同步开关
            //同步接口返回执行结果
            //异步回包
            Thread report = new Thread(() -> {
                //同步接口返回执行结果
                try {
                    if (result.getPayloadType() == RpcNotifyProto.PayloadType.BYTE)//返回流结果
                    {
                        returnByte(deviceId, result.getBytePayload());
                    } else {
                        JSONObject json = JSON.parseObject(result.getPayload());
                        if (json == null) {
                            logger.error("payload is null...");
                            return;
                        } else {
                            jsonResult(deviceId, json.toJSONString());
                        }
                    }
                } catch (Exception e) {
                    logger.error("返回响应失败..." + e.toString());
                    e.printStackTrace();
                }
            });
            report.start();
            logger.info("rpc 发送成功...");
        } else {
            logger.error("rpc 发送失败");
        }

    }

    public boolean isSynMark() {
        return synMark;
    }

    public void setSynMark(boolean synMark) {
        this.synMark = synMark;
    }
}
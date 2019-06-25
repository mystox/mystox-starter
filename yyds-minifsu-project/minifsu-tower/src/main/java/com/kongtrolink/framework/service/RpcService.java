package com.kongtrolink.framework.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.core.entity.PktType;
import com.kongtrolink.framework.core.entity.StateCode;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.entity.CntbPktTypeTable;
import com.kongtrolink.framework.execute.module.RpcModule;
import com.kongtrolink.framework.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

@Service
public class RpcService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RpcModule rpcModule;

    @Autowired
    private LogUtils logUtils;

    @Value("${tower.gateway.hostname}")
    private String towerGatewayHostname;
    @Value("${tower.gateway.port}")
    private int towerGatewayPort;

    /**
     * 获取fsu信息
     * @param sn sn
     * @param ip ip
     * @param port port
     * @return fsu信息
     */
    public JSONObject getFsu(String sn, String ip, int port) {

        JSONObject result = null;
        ModuleMsg request = null;
        String response = "";

        try {
            InetSocketAddress addr = new InetSocketAddress(ip, port);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sn", sn);
            request = createRequestMsg(PktType.GET_FSU, sn, jsonObject);

            response = postMsg(request, addr);
            result = JSONObject.parseObject(response);
        } catch (Exception ex) {
            logUtils.saveLog("", sn, PktType.GET_FSU, StateCode.FAILED);
            logger.error("GET_FSU过程中出现异常:" + JSONObject.toJSONString(ex) +
                    "\r\nRequest:" + JSONObject.toJSONString(request) +
                    "\r\nResponse:" + response);
        }

        return result;
    }

    /**
     * 获取终端下的设备信息
     * @param sn sn
     * @param ip ip
     * @param port port
     * @return 设备信息
     */
    public JSONArray getDevices(String sn, String ip, int port) {

        JSONArray result = null;
        ModuleMsg request = null;
        String response = "";

        try {
            InetSocketAddress addr = new InetSocketAddress(ip, port);

            JSONObject jsonObject = new JSONObject();
            request = createRequestMsg(PktType.GET_DEVICES, sn, jsonObject);

            response = postMsg(request, addr);
            result = JSONObject.parseArray(response);
        } catch (Exception ex) {
            logUtils.saveLog("", sn, PktType.GET_DEVICES, StateCode.FAILED);
            logger.error("GET_DEVICES过程中出现异常:" + JSONObject.toJSONString(ex) +
                    "\r\nRequest:" + JSONObject.toJSONString(request) +
                    "\r\nResponse:" + response);
        }

        return result;
    }

    /**
     * 设置数据点
     * @param sn sn
     * @param ip ip
     * @param port port
     * @param dev 设备
     * @param signalId 信号点id
     * @param value 信号点值
     * @return 回复信息
     */
    public JSONObject setData(String sn, String ip, int port,
                              String dev, int signalId, float value) {

        JSONObject result = null;
        ModuleMsg request = null;
        String response = "";

        try {
            InetSocketAddress addr = new InetSocketAddress(ip, port);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("dev", dev);
            jsonObject.put("stePoint", signalId);
            jsonObject.put("steData", value);
            request = createRequestMsg(PktType.SET_DATA, sn, jsonObject);

            response = postMsg(request, addr);
            result = JSONObject.parseObject(response);
        } catch (Exception ex) {
            logUtils.saveLog("", sn, PktType.GET_FSU, StateCode.FAILED);
            logger.error("SET_DATA过程中出现异常:" + JSONObject.toJSONString(ex) +
                    "\r\nRequest:" + JSONObject.toJSONString(request) +
                    "\r\nResponse:" + response);
        }

        return result;
    }

    /**
     * 获取门限值信息
     * @param sn sn
     * @param ip ip
     * @param port port
     * @param devType 设备类型
     * @param devResNo 设备资源编号
     * @param devPort 设备端口
     * @param alarmIdList 需获取的门限点id列表
     * @return 门限值信息
     */
    public JSONArray getThreshold(String sn, String ip, int port,
                                  int devType, int devResNo, String devPort,
                                  List<String> alarmIdList) {

        JSONArray result = null;
        ModuleMsg request = null;
        String response = "";

        try {
            InetSocketAddress addr = new InetSocketAddress(ip, port);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", devType);
            jsonObject.put("resNo", devResNo);
            jsonObject.put("port", devPort);
            jsonObject.put("alarmId", alarmIdList);
            request = createRequestMsg(PktType.GET_ALARM_PARAM, sn, jsonObject);

            response = postMsg(request, addr);
            result = JSONObject.parseArray(response);
        } catch (Exception ex) {
            logUtils.saveLog("", sn, PktType.GET_FSU, StateCode.FAILED);
            logger.error("GET_ALARM_PARAM过程中出现异常:" + JSONObject.toJSONString(ex) +
                    "\r\nRequest:" + JSONObject.toJSONString(request) +
                    "\r\nResponse:" + response);
        }

        return result;
    }

    /**
     * 设置门限值
     * @param sn sn
     * @param ip ip
     * @param port port
     * @param dev 设备
     * @param alarmId 门限值id
     * @param value 门限值
     * @return 设置结果
     */
    public JSONObject setThreshold(String sn, String ip, int port,
                                   String dev, String alarmId, float value) {

        JSONObject result = null;
        ModuleMsg request = null;
        String response = "";

        try {
            InetSocketAddress addr = new InetSocketAddress(ip, port);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("dev", dev);
            jsonObject.put("alarmId", alarmId);
            jsonObject.put("threshold", value);
            request = createRequestMsg(PktType.SET_ALARM_PARAM, sn, jsonObject);

            response = postMsg(request, addr);
            result = JSONObject.parseObject(response);
        } catch (Exception ex) {
            logUtils.saveLog("", sn, PktType.GET_FSU, StateCode.FAILED);
            logger.error("SET_ALARM_PARAM过程中出现异常:" + JSONObject.toJSONString(ex) +
                    "\r\nRequest:" + JSONObject.toJSONString(request) +
                    "\r\nResponse:" + response);
        }

        return result;
    }

    /**
     * 通过铁塔网关向铁塔服务器发送请求
     * @param reqXmlMsg 请求报文
     * @param ip 铁塔服务器ip
     * @param port 铁塔服务器端口
     * @return 回复信息
     */
    public JSONObject requestCntb(String reqXmlMsg, String ip, int port) {
        JSONObject result = null;
        ModuleMsg request = null;
        String response = "";

        try {
            InetSocketAddress addr = new InetSocketAddress(towerGatewayHostname, towerGatewayPort);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ip", ip);
            jsonObject.put("port", port);
            jsonObject.put("msg", reqXmlMsg);
            request = createRequestMsg(CntbPktTypeTable.SERVICE_GW, "", jsonObject);

            response = postMsg(request, addr);
            result = JSONObject.parseObject(response);
        } catch (Exception ex) {
            logUtils.saveLog("", "", CntbPktTypeTable.SERVICE_GW, StateCode.FAILED);
            logger.error("SERVICE_GW过程中出现异常:" + JSONObject.toJSONString(ex) +
                    "\r\nRequest:" + JSONObject.toJSONString(request) +
                    "\r\nResponse:" + response);
        }

        return result;
    }

    /**
     * 创建请求报文
     * @param pktType 报文头类型
     * @param sn sn
     * @param payload 发送信息
     * @return 字符串报文
     */
    private ModuleMsg createRequestMsg(String pktType, String sn, JSONObject payload) {
        ModuleMsg moduleMsg = new ModuleMsg(pktType, sn);
        moduleMsg.setPayload(payload);
        return moduleMsg;
    }

    /**
     * 发送请求
     * @param request 请求报文
     * @param addr 请求地址
     * @return 回复信息
     */
    private String postMsg(ModuleMsg request, InetSocketAddress addr) {
        String result = null;

        RpcNotifyProto.RpcMessage rpcMessage = null;
        try {
            rpcMessage = rpcModule.postMsg("", addr, JSONObject.toJSONString(request));
            result = rpcMessage.getPayload();
        } catch (IOException e) {
            logUtils.saveLog(request.getMsgId(), request.getSN(), request.getPktType(), StateCode.CONNECT_ERROR);
            logger.error("sendRpcMsg: 发送Rpc请求失败,ip:" + addr.getHostName() + ",port:" + addr.getPort() + ",msg:" + request);
        }

        return result;
    }
}

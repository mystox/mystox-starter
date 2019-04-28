package com.kongtrolink.framework.execute.module.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.*;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.execute.module.RpcModule;
import com.kongtrolink.framework.execute.module.dao.DeviceDao;
import com.kongtrolink.framework.execute.module.dao.LogDao;
import com.kongtrolink.framework.execute.module.dao.TerminalDao;
import com.kongtrolink.framework.execute.module.model.Device;
import com.kongtrolink.framework.execute.module.model.*;
import com.kongtrolink.framework.execute.module.service.TerminalService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by mystoxlol on 2019/3/25, 10:34.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class TerminalServiceImpl implements TerminalService {
    Logger logger = LoggerFactory.getLogger(TerminalServiceImpl.class);

    @Value("${server.bindIp}")
    private String host;
    @Value("${server.rpc.port}")
    private String port;
    @Value("${server.name}")
    private String name;
    private final LogDao logDao;
    private final TerminalDao terminalDao;
    private final DeviceDao deviceDao;

    private final RedisUtils redisUtils;


    private RpcModule rpcModule;

    @Autowired
    public void setRpcModule(RpcModule rpcModule) {
        this.rpcModule = rpcModule;
    }

    @Value("${rpc.controller.hostname}")
    private String controllerHost;
    @Value("${rpc.controller.port}")
    private int controllerPort;

    @Autowired
    public TerminalServiceImpl(LogDao logDao, TerminalDao terminalDao, DeviceDao deviceDao, RedisUtils redisUtils) {
        this.logDao = logDao;
        this.terminalDao = terminalDao;
        this.deviceDao = deviceDao;
        this.redisUtils = redisUtils;
    }

    @Override
    public JSONObject setFsu(Map<String, Object> requestBody) {
        return null;
    }

    @Override
    public Fsu getFsu(ModuleMsg requestBody) {
        return null;
    }


    @Override
    public JSONObject upgrade(Map<String, Object> requestBody, String fsuId) {
        return null;
    }

    @Override
    public JSONObject compiler(Map<String, Object> requestBody) {
        return null;
    }

    @Override
    public JSONArray getDeviceList(ModuleMsg moduleMsg) {
        String sn = moduleMsg.getSN();
        List<Device> devicesBySn = deviceDao.findDevicesBySnAndValid(sn);
        for (Device device : devicesBySn) {
            DeviceType deviceType = deviceDao.getDeviceType(device.getType());
            device.setName(deviceType == null ? "" : deviceType.getName());
        }
        return (JSONArray) JSONObject.toJSON(devicesBySn);
    }


    @Override
    public JSONObject deleteDevice(Map<String, Object> requestBody) {
        return null;
    }

    @Override
    public JSONObject addDevice(Map<String, Object> requestBody, String fsuId) {
        return null;
    }

    @Override
    public List<Fsu> getFsuListByCoordinate(Map fsuMap) {
        return null;
    }

    @Override
    public JSONObject listFsu(ModuleMsg requestBody) {

        JSONObject jsonObject = requestBody.getPayload();
        List<Terminal> terminals = terminalDao.findTerminal(jsonObject);
        Long terminalCount = terminalDao.findTerminalCount(jsonObject);

        JSONObject result = new JSONObject();
        result.put("totalSize", terminalCount);
        JSONArray snList = new JSONArray();
        for (Terminal terminal : terminals) {
            String terminalId = terminal.getId();
            TerminalProperties terminalProperties = terminalDao.findTerminalPropertiesByTerminalId(terminalId);
            JSONObject terminalJSON = (JSONObject) JSONObject.toJSON(terminal);
            String key = RedisHashTable.COMMUNICATION_HASH + ":" + terminal.getSN();
            JSONObject value = redisUtils.get(key, JSONObject.class);
            if (value != null) {
                terminalJSON.put("status", value.get("STATUS"));
            } else {
                terminalJSON.put("status", 0);
            }
            if (terminalProperties != null)
                terminalJSON.putAll((JSONObject) JSONObject.toJSON(terminalProperties));
            snList.add(terminalJSON);
        }
        result.put("list", snList);
        return result;
    }


    @Override
    public List<Fsu> searchFsu(Map<String, Object> requestBody) {
        return null;
    }

    @Override
    public JSONObject getFsuStatus(Map<String, Object> requestBody, String fsuId) {
        return null;
    }

    @Override
    public List<OperatHistory> getOperationHistory(Map<String, Object> requestBody, String fsuId) {
        return null;
    }

    @Override
    public Map<String, Integer> getFsuDeviceCountMap(List<String> fsuIds) {
        return null;
    }

    @Override
    public JSONObject getOperationHistoryByMqtt(Map<String, Object> requestBody, String fsuId) {
        return null;
    }

    @Override
    public JSONObject logoutFsu(Map<String, Object> requestBody, String fsuId) {
        return null;
    }

    @Override
    public JSONObject saveTerminal(ModuleMsg moduleMsg) {
        JSONArray terminalArray = moduleMsg.getArrayPayload();

        List<Terminal> terminals = JSONArray.parseArray(terminalArray.toJSONString(), Terminal.class);


        List<String> duplicateSn = new ArrayList<>();//重复sn

        for (Terminal terminal : terminals) {
            String sn = terminal.getSN();
            if (terminalDao.existsBySn(sn)) {
                duplicateSn.add(sn);
                continue;
            }
        }

        JSONObject result = new JSONObject();
        if (duplicateSn.size() < 1) {
            terminalDao.saveTerminalBatch(terminals);
            result.put("result", 1);
            return result;
        } else {
            result.put("result", 0);
            result.put("duplicateSns", duplicateSn);
        }


        return result;
    }

    @Override
    public JSONObject setTerminal(ModuleMsg moduleMsg) {
        JSONObject jsonObject = moduleMsg.getPayload();
        String sn = moduleMsg.getSN();
        if (StringUtils.isBlank(sn)) {
            sn = jsonObject.getString("sN");
            if (StringUtils.isBlank(sn)) {
                JSONObject result = new JSONObject();
                result.put("result", 0);
                return result;
            }
        }
        Terminal terminal = terminalDao.findTerminalBySn(sn);
        Object heartCycle = jsonObject.get("heartCycle");
        if (heartCycle != null) terminal.setHeartCycle((Integer) heartCycle);
        Object businessRhythm = jsonObject.get("businessRhythm");
        if (businessRhythm != null) terminal.setBusinessRhythm((Integer) businessRhythm);
        Object alarmRhythm = jsonObject.get("alarmRhythm");
        if (alarmRhythm != null) terminal.setAlarmRhythm((Integer) alarmRhythm);
        Object runStatusRhythm = jsonObject.get("runStatusRhythm");
        if (runStatusRhythm != null) terminal.setRunStatusRhythm((Integer) runStatusRhythm);
        Object coordinate = jsonObject.get("coordinate");
        if (runStatusRhythm != null) terminal.setCoordinate((String) coordinate);
        String bid = jsonObject.getString("BID");
        if (StringUtils.isBlank(bid)) bid = "default";
        JSONObject result = new JSONObject();
        String fsuId = (String) jsonObject.get("fsuId");
        if (StringUtils.isNotBlank(fsuId)) { //存在 fsuid 进入绑定流程
            // 向网关发送业注册报文{"SN","00000",DEVICE_LIST} 即向业务平台事务处理发送注册信息
            try {
                JSONArray devList = redisUtils.getHash(RedisHashTable.SN_DEVICE_LIST_HASH, sn, JSONArray.class);
                jsonObject.put("devList", devList);
                //获取BIP
                Order orderByBid = terminalDao.findOrderByBid(bid);
                jsonObject.put("BIP", orderByBid.getBIP()); //默认发往default
                moduleMsg.setPktType(PktType.FSU_BIND);
                RpcNotifyProto.RpcMessage rpcMessage = rpcModule.postMsg(moduleMsg.getMsgId(), new InetSocketAddress(controllerHost, controllerPort), JSONObject.toJSONString(moduleMsg));
                String bindResult = rpcMessage.getPayload();
                JSONObject jsonObject1 = JSONObject.parseObject(bindResult);
                Integer resultInt = jsonObject1.getInteger("result");
                if (resultInt == 1) {
                    terminal.setBindMark(true);
                    terminal.setFsuId(fsuId);
                    terminalDao.saveTerminal(terminal);
                } else {
                    logger.error("[{}] sn [{}] bind terminal false...",moduleMsg.getMsgId(),sn);
                }
                result.put("result", resultInt);
                return result;
            } catch (IOException e) {
                logger.error("发送至外部业务绑定异常" + e.toString());
                //日志记录
                Log log = new Log();
                log.setErrorCode(3);
                log.setSN(sn);
                log.setMsgType(moduleMsg.getPktType());
                log.setMsgId(moduleMsg.getMsgId());
                log.setHostName(host);
                log.setServiceName(name);
                log.setTime(new Date(System.currentTimeMillis()));
                logDao.saveLog(log);
                e.printStackTrace();
            }
        }
        terminalDao.saveTerminal(terminal);
        result.put("result", 1);
        return result;
    }
    /**
     * 终端解绑
     * @param moduleMsg
     * @return
     */
    @Override
    public JSONObject unBind(ModuleMsg moduleMsg) {
        String sn = moduleMsg.getSN();
        JSONObject payload = moduleMsg.getPayload();
        String fsuId = payload.getString("fsuId");
        if (StringUtils.isNotBlank(fsuId)) {

        }
        JSONObject result = new JSONObject();
        moduleMsg.setPktType(PktType.FSU_BIND);
        RpcNotifyProto.RpcMessage rpcMessage = null;
        try {
            moduleMsg.setPktType(PktType.TERMINAL_UNBIND);
            rpcMessage = rpcModule.postMsg(moduleMsg.getMsgId(), new InetSocketAddress(controllerHost, controllerPort), JSONObject.toJSONString(moduleMsg));
            String bindResult = rpcMessage.getPayload();
            JSONObject jsonObject1 = JSONObject.parseObject(bindResult);
            Integer resultInt = jsonObject1.getInteger("result");
            Terminal terminal = terminalDao.findTerminalBySn(sn);
            if (resultInt == 1) {
                terminal.setBindMark(false);
                terminal.setFsuId("");
                terminalDao.saveTerminal(terminal);
                result.put("result", resultInt);
                return result;
            } else {
                logger.error("[{}] sn [{}] unbind terminal false...",moduleMsg.getMsgId(),sn);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        result.put("result", 1);
        return result;
    }
    @Override
    public JSONObject terminalLogSave(ModuleMsg moduleMsg) {
        JSONObject payload = moduleMsg.getPayload();
        JSONObject terminalPayload = (JSONObject) payload.get("payload");
        TerminalLog terminalLog;
        String sn = moduleMsg.getSN();
        if (terminalPayload != null) {
            Integer pktType = (Integer) terminalPayload.get("pktType");
            terminalLog = new TerminalLog(sn, pktType, new Date(), payload);
        } else {
            terminalLog = new TerminalLog(sn, null, new Date(), payload);
        }
        terminalDao.saveTerminalLog(terminalLog);
        JSONObject result = new JSONObject();
        result.put("result", 1);
        return result;
    }

    /**
     * 获取终端状态
     * @param moduleMsg
     * @return
     */
    @Override
    public JSONObject TerminalStatus(ModuleMsg moduleMsg) {
        String sn = moduleMsg.getSN();
        String key = RedisHashTable.COMMUNICATION_HASH + ":" + sn;
        JSONObject value = redisUtils.get(key, JSONObject.class);
        JSONObject result = new JSONObject();
        if (value != null) {
            return value;
        } else {
            Terminal terminal = terminalDao.findTerminalBySn(sn);
            if (terminal != null)
                result = (JSONObject) JSONObject.toJSON(terminal);
            result.put("STATUS", 0);
        }
        result.put("result", 0);
        return result;

    }

    @Override
    public JSONObject getRunStates(ModuleMsg moduleMsg) {
        String sn = moduleMsg.getSN();
        JSONObject search = moduleMsg.getPayload();
        List<RunState> terminalLogs = terminalDao.findRunStates(sn, search);
        Long totalSize = terminalDao.getRunStateCount(sn, search);
        JSONObject result = new JSONObject();
        result.put("totalSize", totalSize);
        result.put("list", terminalLogs);
        return result;
    }

    @Override
    public JSONObject getTerminalPayloadLog(ModuleMsg moduleMsg) {
        String sn = moduleMsg.getSN();
        JSONObject search = moduleMsg.getPayload();
        List<TerminalLog> terminalLogs = terminalDao.findTerminalLog(sn, search);
        Long totalSize = terminalDao.getTerminalLogCount(sn, search);
        JSONObject result = new JSONObject();
        result.put("totalSize", totalSize);
        result.put("list", terminalLogs);
        return result;
    }


}

package com.kongtrolink.framework.execute.module.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.*;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.execute.module.RpcModule;
import com.kongtrolink.framework.execute.module.dao.CompilerDao;
import com.kongtrolink.framework.execute.module.dao.DeviceDao;
import com.kongtrolink.framework.execute.module.dao.LogDao;
import com.kongtrolink.framework.execute.module.dao.TerminalDao;
import com.kongtrolink.framework.execute.module.model.Device;
import com.kongtrolink.framework.execute.module.model.*;
import com.kongtrolink.framework.execute.module.service.LogService;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    @Value("${compiler.fileVersionId}")
    private String fileVersionId;
    private final LogDao logDao;
    private final TerminalDao terminalDao;
    private final CompilerDao compilerDao;
    private final DeviceDao deviceDao;
    private final RedisUtils redisUtils;
    private RpcModule rpcModule;
    private LogService logService;

    @Autowired
    public void setLogService(LogService logService) {
        this.logService = logService;
    }

    @Autowired
    public void setRpcModule(RpcModule rpcModule) {
        this.rpcModule = rpcModule;
    }


    @Value("${rpc.controller.hostname}")
    private String controllerHost;
    @Value("${rpc.controller.port}")
    private int controllerPort;

    @Autowired
    public TerminalServiceImpl(LogDao logDao, TerminalDao terminalDao, DeviceDao deviceDao, RedisUtils redisUtils, CompilerDao compilerDao) {
        this.logDao = logDao;
        this.terminalDao = terminalDao;
        this.deviceDao = deviceDao;
        this.redisUtils = redisUtils;
        this.compilerDao = compilerDao;
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
    public JSONObject listFsu(ModuleMsg requestBody) {

        JSONObject jsonObject = requestBody.getPayload();
        List<Terminal> terminals = terminalDao.findTerminal(jsonObject);
        Long terminalCount = terminalDao.findTerminalCount(jsonObject);

        JSONObject result = new JSONObject();
        result.put("totalSize", terminalCount);
        JSONArray snList = new JSONArray();
        for (Terminal terminal : terminals) {
            String terminalId = terminal.getId();
            JSONObject terminalJSON = (JSONObject) JSONObject.toJSON(terminal);
            String model = terminal.getModel();
            terminalJSON.put("FSUType", model);
            String key = RedisHashTable.COMMUNICATION_HASH + ":" + terminal.getSN();
            JSONObject value = redisUtils.get(key, JSONObject.class);
            String wip = "";
            if (value != null) {
                terminalJSON.put("status", value.get("STATUS"));
            } else {
                terminalJSON.put("status", 0);
            }
                Order orderByBid = terminalDao.findOrderByBid(terminal.getBID());
                if (orderByBid!=null)
                {
                    wip = orderByBid.getWIP();
                }
            terminalJSON.put("wip", wip);
            TerminalProperties terminalProperties = terminalDao.findTerminalPropertiesByTerminalId(terminalId);
            if (terminalProperties != null)
                terminalJSON.putAll((JSONObject) JSONObject.toJSON(terminalProperties));
            snList.add(terminalJSON);
        }
        result.put("list", snList);
        return result;
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
                sn = jsonObject.getString("sn");
                if (StringUtils.isBlank(sn)) {
                    JSONObject result = new JSONObject();
                    result.put("result", 0);
                    return result;
                }
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

        Boolean enableHeart = jsonObject.getBoolean("enableHeart");
        if (enableHeart != null) {
            terminal.setEnableHeart(enableHeart);
        }

        String coordinate = jsonObject.getString("coordinate");
        if (StringUtils.isNotBlank(coordinate)) terminal.setCoordinate(coordinate);
        String name = jsonObject.getString("name"); //别名
        if (StringUtils.isNotBlank(name)) terminal.setName(name);
        String userId = jsonObject.getString("userId"); //绑定用户
        if (StringUtils.isNotBlank(userId)) terminal.setName(userId);
        String address = jsonObject.getString("address"); //坐标
        if (StringUtils.isNotBlank(coordinate)) terminal.setAddress(address);
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
                    //解绑其他sn
                    if (StringUtils.isNotBlank(fsuId)) {
                        List<Terminal> terminalByFsuId = terminalDao.findTerminalByFsuId(fsuId);
                        if (terminalByFsuId != null) {
                            for (Terminal t : terminalByFsuId) {
                                t.setBindMark(false);
                                t.setFsuId("");
                                terminalDao.saveTerminal(t);
                            }
                        }
                    }
                    //保存SN
                    terminal.setBindMark(true);
                    terminal.setFsuId(fsuId);
                } else {
                    logger.error("[{}] sn [{}] bind terminal false...", moduleMsg.getMsgId(), sn);
                }
                result.put("result", resultInt);
            } catch (IOException e) {
                saveLog(moduleMsg.getMsgId(), sn, moduleMsg.getPktType(), StateCode.CONNECT_ERROR);
                logger.error("发送至外部业务绑定异常" + e.toString());
                e.printStackTrace();
            }
        }

        terminalDao.saveTerminal(terminal);

        if (enableHeart != null) {
            //获取redis 信息
            String key = RedisHashTable.COMMUNICATION_HASH + ":" + sn;
            JSONObject value = redisUtils.get(key, JSONObject.class);
            value.put("STATUS", 0);
            redisUtils.set(key, value, value.getLong("expired"));
        }
        result.put("result", 1);
        return result;
    }

    /**
     * 终端解绑
     *
     * @param moduleMsg
     * @return
     */
    @Override
    public JSONObject unBind(ModuleMsg moduleMsg) {
        String sn = moduleMsg.getSN();
        JSONObject result = new JSONObject();
        moduleMsg.setPktType(PktType.FSU_BIND);
        RpcNotifyProto.RpcMessage rpcMessage = null;
        JSONObject payload = moduleMsg.getPayload();
        if (payload != null) {
            String fsuId = payload.getString("fsuId");
        }

        try {
            Terminal terminal = terminalDao.findTerminalBySn(sn);
            moduleMsg.setPktType(PktType.TERMINAL_UNBIND);
            String bid = terminal.getBID();
            //获取BIP
            Order orderByBid = terminalDao.findOrderByBid(bid);
            payload.put("BIP", orderByBid.getBIP()); //默认发往default
            rpcMessage = rpcModule.postMsg(moduleMsg.getMsgId(), new InetSocketAddress(controllerHost, controllerPort), JSONObject.toJSONString(moduleMsg));
            String bindResult = rpcMessage.getPayload();
            JSONObject jsonObject1 = JSONObject.parseObject(bindResult);
            Integer resultInt = jsonObject1.getInteger("result");
            if (resultInt == 1) {
                terminal.setBindMark(false);
                terminal.setFsuId("");
                terminalDao.saveTerminal(terminal);
                result.put("result", resultInt);
                return result;
            } else {
                logger.error("[{}] sn [{}] unbind terminal false...", moduleMsg.getMsgId(), sn);
                result.put("result", resultInt);
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        result.put("result", 1);
        return result;
    }

    @Override
    public JSONObject getCompilerConfig(ModuleMsg moduleMsg) {
        String sn = moduleMsg.getSN();
        JSONObject payload = moduleMsg.getPayload();
        String adapterVer = payload.getString("adapterVer");
        if (StringUtils.isBlank(adapterVer)) {
            Terminal terminalBySn = terminalDao.findTerminalBySn(sn);
            TerminalProperties terminalPropertiesByTerminalId = terminalDao.findTerminalPropertiesByTerminalId(terminalBySn.getId());
            adapterVer = terminalPropertiesByTerminalId.getAdapterVer();
        }
        Integer businessSceneId = compilerDao.getBusinessSceneId(adapterVer);
        JSONObject result = new JSONObject();

        if (businessSceneId == null)
        {
            String[] verSrr = adapterVer.split("\\.");
            if (verSrr.length == 4) {
                adapterVer = verSrr[0] + ".*";
            }
            if (verSrr.length == 7)
            {
                Pattern p = Pattern.compile("(\\d*\\.){5}");
                Matcher matcher = p.matcher(adapterVer);
                if (matcher.find())
                {
                    String group = matcher.group(0);
                    adapterVer = group + "*";
                }
            }
            businessSceneId = compilerDao.getBusinessSceneId(adapterVer);
        }
        if (businessSceneId == null) {
            result.put("result", 0);
            return result;
        }
        Integer productId = compilerDao.getProductId(sn.substring(0, 6));
        if (productId == null) {
            result.put("result", 1);
            return result;
        }
        result.put("businessSceneId", businessSceneId);
        result.put("productId", productId);
        result.put("fileVersionId", fileVersionId);
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
     *
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

    /**
     * 运行状态信息
     *
     * @param moduleMsg
     * @return
     */
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

    /**
     * 保存运行时错误日志
     *
     * @param msgId
     * @param sn
     * @param msgType
     * @param stateCode
     */
    void saveLog(String msgId, String sn, String msgType, int stateCode) {
        Log log = new Log(new Date(System.currentTimeMillis()),
                stateCode,
                sn, msgType, msgId, name, host);
        logService.saveLog(log);
    }


}

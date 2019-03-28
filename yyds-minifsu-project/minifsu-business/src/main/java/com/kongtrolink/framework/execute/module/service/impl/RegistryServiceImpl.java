package com.kongtrolink.framework.execute.module.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.Log;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.core.entity.RedisHashTable;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.execute.module.dao.LogDao;
import com.kongtrolink.framework.execute.module.dao.TerminalDao;
import com.kongtrolink.framework.execute.module.model.Order;
import com.kongtrolink.framework.execute.module.model.Terminal;
import com.kongtrolink.framework.execute.module.model.TerminalProperties;
import com.kongtrolink.framework.execute.module.service.RegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Date;

/**
 * Created by mystoxlol on 2019/3/27, 23:48.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class RegistryServiceImpl implements RegistryService {


    @Value("${register.delay:300}")
    private int delay;
    @Value("${server.bindIp}")
    private String host;
    @Value("${server.name}")
    private String name;
    @Autowired
    TerminalDao terminalDao;
    @Autowired
    LogDao logDao;
    @Autowired
    RedisUtils redisUtils;

    @Override
    public JSONObject registerSN(ModuleMsg moduleMsg) {

        String sn = moduleMsg.getSN();
        //查表t_terminal
        Terminal terminal = terminalDao.findTerminalBySn(sn);
        JSONObject result = new JSONObject();
        if (terminal != null && otherLogic()) {
            String bid = terminal.getBID();
            Order order = terminalDao.findOrderByBid(bid);
            String bip = order.getBIP();
            //获取redis 信息
            JSONObject value = redisUtils.getHash(RedisHashTable.COMMUNICATION_HASH, sn, JSONObject.class);
            //删除所有其他信息
            if (value != null) {
                redisUtils.deleteHash(RedisHashTable.SN_DEVICE_LIST_HASH, sn);
                redisUtils.deleteHash(RedisHashTable.SN_SIGNAL_DATA, sn);

                value.put("BIP", bip);
                value.put("STATUS", 1);
                redisUtils.setHash(RedisHashTable.COMMUNICATION_HASH, sn, value);

                result.put("result", 1);
                result.put("time", System.currentTimeMillis() / 1000);
                return result;
            }
        } else if (!otherLogic()) {
            result.put("result", 0);
            result.put("delay", delay);
        }
        //注册非法默认

        result.put("result", 0);
        result.put("delay", delay);

        //日志记录
        Log log = new Log();

        log.setErrorCode(1);
        log.setSN(sn);
        log.setMsgId(moduleMsg.getMsgId());
        log.setHostName(host);
        log.setServiceName(name);
        log.setTime(new Date(System.currentTimeMillis()));
        logDao.saveLog(log);
        return result;
    }

    @Override
    public JSONObject registerDevices(ModuleMsg moduleMsg) {
        String sn = moduleMsg.getSN();
        JSONObject deviceList = moduleMsg.getPayload();
        JSONObject result = new JSONObject();
        //获取redis 信息
        JSONObject value = redisUtils.getHash(RedisHashTable.COMMUNICATION_HASH, sn, JSONObject.class);
        if (value != null && (int) value.get("STATUS") == 1) {
            redisUtils.setHash(RedisHashTable.SN_DEVICE_LIST_HASH, sn, deviceList.get("devList"));
            result.put("result", 1);
            return result;
        }

        //日志记录
        Log log = new Log();
        log.setErrorCode(3);
        log.setSN(sn);
        log.setMsgId(moduleMsg.getMsgId());
        log.setHostName(host);
        log.setServiceName(name);
        log.setTime(new Date(System.currentTimeMillis()));
        logDao.saveLog(log);
        result.put("result", 0);
        return result;
    }


    @Override
    public JSONObject registerTerminal(ModuleMsg moduleMsg) {

        JSONObject payload = moduleMsg.getPayload();
        String sn = moduleMsg.getSN();
        Terminal terminal = terminalDao.findTerminalBySn(sn);
        String terminalId = terminal.getId();
        TerminalProperties terminalProperties = terminalDao.findTerminalPropertiesByTerminalId(terminalId);
        if (terminalProperties == null) {
            terminalProperties = new TerminalProperties();
            terminalProperties.setTerminalId(terminalId);
        }
        terminalProperties.setAccessMode((Integer) payload.get("accessMode"));
        terminalProperties.setCarrier((String) payload.get("carrier"));
        terminalProperties.setNwType((String) payload.get("nwType"));
        terminalProperties.setWmType((String) payload.get("wmType"));
        terminalProperties.setWmVendor((String) payload.get("wmVendor"));
        terminalProperties.setImei((String) payload.get("imsi"));
        terminalProperties.setImei((String) payload.get("imei"));
        terminalProperties.setEngineVer((String) payload.get("engineVer"));
        terminalProperties.setSignalStrength((String) payload.get("signalStrength"));
        terminalProperties.setAdapterVer((String) payload.get("adapterVer"));
        terminalDao.saveTerminal(terminalProperties);
        JSONObject result = new JSONObject();
        result.put("result", 1);
        return result;
    }

    private boolean otherLogic() {
        return true;
    }
}

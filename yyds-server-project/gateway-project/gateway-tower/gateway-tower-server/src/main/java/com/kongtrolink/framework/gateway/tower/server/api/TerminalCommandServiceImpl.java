package com.kongtrolink.framework.gateway.tower.server.api;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.gateway.iaiot.core.rec.RecServerBase;
import com.kongtrolink.framework.gateway.iaiot.core.send.AckBase;
import com.kongtrolink.framework.gateway.tower.core.entity.mqtt.receive.GetDataAckMessage;
import com.kongtrolink.framework.gateway.tower.core.entity.mqtt.receive.GetThresholdAckMessage;
import com.kongtrolink.framework.gateway.tower.core.entity.mqtt.receive.SetPointAckMessage;
import com.kongtrolink.framework.gateway.tower.core.entity.mqtt.receive.SetThresholdAckMessage;
import com.kongtrolink.framework.gateway.tower.server.service.NorthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by mystoxlol on 2019/9/9, 13:03.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class TerminalCommandServiceImpl implements TerminalCommandService {

    private static final Logger logger = LoggerFactory.getLogger(TerminalCommandServiceImpl.class);

    @Autowired
    private NorthService northService;

    /**
     * 资管主动下发 下发设备获取设备信息
     * @param message 消息体
     * @return 结果
     */
    @Override
    public String deviceGet(String message) {
        RecServerBase recServerBase = JSONObject.parseObject(message,RecServerBase.class);
        String sn = recServerBase.getSn();
        AckBase ackBase = new AckBase();
        String msgId = 1+""+new Date().getTime();
        ackBase.setMsgId(msgId);
        String s = JSONObject.toJSONString(ackBase);
        //根据设备ID获取设备type
        return "{}";
    }

    /**
     * SC向GW请求监控点数据
     *
     */
    @Override
    public String getData(String message) {
        GetDataAckMessage getDataAckMessage = northService.getData(message);
        return JSONObject.toJSONString(getDataAckMessage);
    }

    /**
     * SC向GW设置监控点数据
     */
    @Override
    public String setPoint(String message) {
        SetPointAckMessage setPointAckMessage = northService.setPoint(message);
        return JSONObject.toJSONString(setPointAckMessage);
    }

    /**
     * SC向GW请求门限值数据
     */
    @Override
    public String getThreshold(String message) {
        GetThresholdAckMessage getThresholdAckMessage = northService.getThreshold(message);
        return JSONObject.toJSONString(getThresholdAckMessage);
    }

    /**
     * SC向GW 设置门限值数据
     */
    @Override
    public String setThreshold(String message) {
        SetThresholdAckMessage setThresholdAckMessage = northService.setThreshold(message);
        return JSONObject.toJSONString(setThresholdAckMessage);
    }

    /**
     * SC向GW 获取FSU状态
     */
    @Override
    public String getFsuInfo(String message) {
        return null;
    }

    /**
     * SC向GW FSU重启
     */
    @Override
    public String reboot(String message) {
        return null;
    }

}

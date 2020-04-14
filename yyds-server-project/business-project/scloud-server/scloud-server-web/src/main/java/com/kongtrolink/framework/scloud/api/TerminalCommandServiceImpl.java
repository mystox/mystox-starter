package com.kongtrolink.framework.scloud.api;


import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.gateway.tower.core.entity.mqtt.receive.LoginAckMessage;
import com.kongtrolink.framework.gateway.tower.core.entity.mqtt.receive.LoginMessage;
import com.kongtrolink.framework.gateway.tower.core.entity.mqtt.receive.LoginOfflineMessage;
import com.kongtrolink.framework.scloud.constant.CommonConstant;
import com.kongtrolink.framework.scloud.dao.DeviceMongo;
import com.kongtrolink.framework.scloud.entity.DeviceEntity;
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

    @Autowired
    DeviceMongo deviceMongo;

    private static final Logger logger = LoggerFactory.getLogger(TerminalCommandServiceImpl.class);

    /**
     * 注册
     * @param message 消息体
     * @return 结果
     */
    @Override
    public String login(String message) {
        LoginMessage recServerBase = JSONObject.parseObject(message,LoginMessage.class);
        LoginAckMessage ack = new LoginAckMessage();
        ack.setResult(true);

        //更新FSU注册状态及相关属性
        if (recServerBase != null){
            DeviceEntity fsu = new DeviceEntity();
            fsu.setState(CommonConstant.ONLINE);
            fsu.setCode(recServerBase.getFsuId());
            fsu.setIp(recServerBase.getIp());
            fsu.setEnterpriseCode(recServerBase.getEnterpriseCode());
            fsu.setServerCode(recServerBase.getServerCode());
            fsu.setGatewayServerCode(recServerBase.getGatewayServerCode());
            deviceMongo.updateFsu(fsu);
        }

        return JSONObject.toJSONString(ack);
    }

    /**
     * SC向GW请求监控点数据
     *
     * @param message
     */
    @Override
    public String loginOffline(String message) {
        LoginOfflineMessage recServerBase = JSONObject.parseObject(message,LoginOfflineMessage.class);

        //更新FSU注册状态及相关属性
        if (recServerBase != null){
            DeviceEntity fsu = new DeviceEntity();
            fsu.setState(CommonConstant.OFFLINE);
            fsu.setCode(recServerBase.getFsuId());
            fsu.setEnterpriseCode(recServerBase.getEnterpriseCode());
            fsu.setServerCode(recServerBase.getServerCode());
            fsu.setGatewayServerCode(recServerBase.getGatewayServerCode());
            fsu.setOfflineTime(new Date().getTime());
            deviceMongo.updateFsu(fsu);
        }

        return "";//不需要返回
    }


}

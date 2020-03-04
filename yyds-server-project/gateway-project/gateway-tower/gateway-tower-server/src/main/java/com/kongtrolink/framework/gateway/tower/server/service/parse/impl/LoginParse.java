package com.kongtrolink.framework.gateway.tower.server.service.parse.impl;

import com.alibaba.fastjson.JSONObject;
import com.chinatowercom.scservice.InvokeResponse;
import com.kongtrolink.framework.common.util.MqttUtils;
import com.kongtrolink.framework.core.constant.ScloudBusinessOperate;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.gateway.tower.core.constant.GatewayTonerOperate;
import com.kongtrolink.framework.gateway.tower.core.constant.RedisKey;
import com.kongtrolink.framework.gateway.tower.core.entity.RedisFsuInfo;
import com.kongtrolink.framework.gateway.tower.core.entity.base.MessageResp;
import com.kongtrolink.framework.gateway.tower.core.entity.mqtt.receive.LoginAckMessage;
import com.kongtrolink.framework.gateway.tower.core.entity.mqtt.receive.LoginMessage;
import com.kongtrolink.framework.gateway.tower.core.entity.msg.Login;
import com.kongtrolink.framework.gateway.tower.core.entity.msg.LoginAck;
import com.kongtrolink.framework.gateway.tower.core.util.MessageUtil;
import com.kongtrolink.framework.gateway.tower.core.util.RedisKeyUtil;
import com.kongtrolink.framework.gateway.tower.server.service.parse.ParseHandler;
import com.kongtrolink.framework.gateway.tower.server.service.transverter.impl.AlarmTransverter;
import com.kongtrolink.framework.gateway.tower.server.service.transverter.impl.AssetTransverter;
import com.kongtrolink.framework.service.MqttOpera;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created by mystoxlol on 2019/10/16, 15:28.
 * company: kongtrolink
 * description: 注册协议转换器
 * update record:
 * 注册 - 平台注册返回结果 -> 资管上报/告警消除
 */
@Service("loginParse")
public class LoginParse extends ParseHandler {

    @Autowired
    private AssetTransverter assetTransverter;
    @Autowired
    private AlarmTransverter alarmTransverter;
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    MqttOpera mqttOpera;
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginParse.class);

    @Override
    public InvokeResponse execute(String payload) {
        try{
            Login login = (Login) MessageUtil.stringToMessage(payload, Login.class);
            String fsuShortCode = login.getFsuId();//报文中的FSUID 对应数据库中的code字段
            LOGGER.info("注册  >> fsuCode : {} " ,fsuShortCode);
            String uniqueCode = getEnterpriseCode();
            boolean flag = loginAck(login); //todo 上报业务平台返回注册结果
            LoginAck infoAck = new LoginAck();
            if(!flag){
                LOGGER.info("在数据库中没有查询到相关FSU fsuShortCode:{}  uniqueCode:{}  的FSU .... ",fsuShortCode,uniqueCode);
                infoAck.setScIp("");
                infoAck.setRightLevel("0");
                MessageResp respMessage = new MessageResp(infoAck);
                return baseInvokeResponse(respMessage);
            }
            //redis保存注册信息
            RedisFsuInfo redisFsuInfo = new RedisFsuInfo();
            redisFsuInfo.setShortCode(fsuShortCode);
            redisFsuInfo.setPort(getFsuPort());
            redisFsuInfo.setIp(login.getIp());
            redisFsuInfo.setGatewayServerCode(MqttUtils.preconditionServerCode(getServerName(),getServerVersion()));
            redisUtils.hset(RedisKeyUtil.getRedisKey(uniqueCode,RedisKey.FSU_INFO),fsuShortCode,JSONObject.toJSONString(redisFsuInfo));
            //判断是否有离线告警
            try{
                alarmTransverter.offlineAlarmEnd(fsuShortCode);
            }catch (Exception e){
                LOGGER.error("离线告警 消除 异常 !!");
                e.printStackTrace();
            }
            try {
                //上报资管信息
                assetTransverter.transferExecute(JSONObject.toJSONString(login.getDeviceList()),fsuShortCode);
            }catch (Exception e){
                LOGGER.error("资管上报异常 !!");
                e.printStackTrace();
            }
            //返回webService回复
            infoAck.setScIp(getServerHost());
            infoAck.setRightLevel("2");
            MessageResp respMessage = new MessageResp(infoAck);
            login.setUniqueCode(uniqueCode);
            return baseInvokeResponse(respMessage);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private boolean loginAck(Login login){
        try{
            LoginMessage loginMessage = new LoginMessage();
            loginMessage.setEnterpriseCode(getEnterpriseCode());
            loginMessage.setGatewayServerCode(MqttUtils.preconditionServerCode(getServerName(),getServerVersion()));
            loginMessage.setIp(login.getIp());
            loginMessage.setPassword(login.getPaSCWord());
            loginMessage.setServerCode(getBusinessCode());
            loginMessage.setUsername(login.getUserName());
            MsgResult msgResult = mqttOpera.opera(ScloudBusinessOperate.LOGIN,JSONObject.toJSONString(loginMessage));
            if(msgResult!=null && msgResult.getMsg()!=null){
                LoginAckMessage ackMessage = JSONObject.parseObject(msgResult.getMsg(),LoginAckMessage.class);
                return ackMessage.isResult();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public RedisFsuInfo getRedisFsuInfo(String fsuId){
        String uniqueCode = getEnterpriseCode();
        Object object = redisUtils.hget(RedisKeyUtil.getRedisKey(uniqueCode,RedisKey.FSU_INFO),fsuId);
        if(object==null){
            return null;
        }
        try {
            RedisFsuInfo value = JSONObject.parseObject(String.valueOf(object),RedisFsuInfo.class);
            return value;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}

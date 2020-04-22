package com.kongtrolink.framework.scloud.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.scloud.constant.BaseConstant;
import com.kongtrolink.framework.scloud.constant.CommonConstant;
import com.kongtrolink.framework.scloud.service.DeliverService;
import com.kongtrolink.framework.service.MqttOpera;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @Auther: liudd
 * @Date: 2020/4/21 17:33
 * @Description:
 */
@Service
public class DeliverServiceImpl implements DeliverService {
    @Autowired
    MqttOpera mqttOpera;
    @Value("${alarmModule.delDeliverUser:deliverRemoteDelUserId}")
    private String delDeliverUser;
    private static final Logger LOGGER = LoggerFactory.getLogger(DeliverServiceImpl.class);

    /**
     * @param uniqueCode
     * @param userId
     * @auther: liudd
     * @date: 2020/4/21 17:33
     * 功能描述:删除用户时，删除投递模板中绑定的用户
     */
    @Override
    public boolean delDeliverUser(String uniqueCode, String userId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enterpriseCode", uniqueCode);
        jsonObject.put("userId", userId);
        MsgResult msgResult = mqttOpera.opera(delDeliverUser, jsonObject.toJSONString());
        if (msgResult.getStateCode() == CommonConstant.SUCCESSFUL) {
            if (msgResult.getMsg() != null) {
                JSONObject resJson = JSONObject.parseObject(msgResult.getMsg(), JSONObject.class);
                return resJson.getBoolean("success");
            }else {
                LOGGER.error("向【告警模块】删除用户绑定投递模板失败,userId:{},msg为null,请求失败", userId);
            }
        } else {
            LOGGER.error("向【告警模块】删除用户绑定投递模板失败,userId:{} 通信失败", userId);
        }
        return false;
    }
}

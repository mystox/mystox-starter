package com.kongtrolink.framework.scloud.api;


import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.gateway.tower.core.entity.mqtt.receive.LoginMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/9/9, 13:03.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class TerminalCommandServiceImpl implements TerminalCommandService {

    private static final Logger logger = LoggerFactory.getLogger(TerminalCommandServiceImpl.class);

    /**
     * 注册
     * @param message 消息体
     * @return 结果
     */
    @Override
    public String login(String message) {
        LoginMessage recServerBase = JSONObject.parseObject(message,LoginMessage.class);

        return "{}";
    }


}

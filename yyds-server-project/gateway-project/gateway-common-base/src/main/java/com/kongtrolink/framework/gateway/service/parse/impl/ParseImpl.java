package com.kongtrolink.framework.gateway.service.parse.impl;

import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.gateway.entity.ParseProtocol;
import com.kongtrolink.framework.gateway.service.parse.ParseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/10/16, 15:04.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class ParseImpl extends ParseHandler {


    @Autowired
    RedisUtils redisUtils;


    @Override
    public void execute(String payload) {

        //todo 服务解析流程

        //根据消息类型获取协议转换器执行
        Object o = redisUtils.getString(payload);
        System.out.println(o);

        new ParseProtocol();
    }

}

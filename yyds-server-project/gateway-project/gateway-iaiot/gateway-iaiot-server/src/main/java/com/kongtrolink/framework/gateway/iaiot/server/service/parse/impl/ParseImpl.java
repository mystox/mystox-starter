package com.kongtrolink.framework.gateway.iaiot.server.service.parse.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.gateway.iaiot.server.entity.ParseProtocol;
import com.kongtrolink.framework.gateway.iaiot.server.service.parse.ParseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/10/16, 15:04.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class ParseImpl extends ParseHandler {

    private Logger logger = LoggerFactory.getLogger(ParseImpl.class);

    @Override
    protected ParseProtocol parsePayload(String payload) {
        return JSONObject.parseObject(payload,ParseProtocol.class);
    }



}

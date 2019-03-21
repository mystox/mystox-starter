package com.kongtrolink.framework.service;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by mystoxlol on 2019/2/27, 17:29.
 * company: kongtrolink
 * description:
 * update record:
 */
public interface MqttTestInterface
{

    JSONObject sendMsg(String requestBody);
}

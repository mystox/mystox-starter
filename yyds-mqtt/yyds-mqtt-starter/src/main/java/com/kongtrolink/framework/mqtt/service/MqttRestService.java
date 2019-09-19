package com.kongtrolink.framework.mqtt.service;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.JsonResult;

/**
 * Created by mystoxlol on 2019/9/11, 17:31.
 * company: kongtrolink
 * description:
 * update record:
 */
public interface MqttRestService {
    JsonResult registerSub(JSONObject subJson);

    JsonResult deleteSub(JSONObject body);
}

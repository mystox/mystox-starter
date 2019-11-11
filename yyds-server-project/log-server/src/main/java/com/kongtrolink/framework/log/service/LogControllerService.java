package com.kongtrolink.framework.log.service;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by mystoxlol on 2019/11/11, 11:21.
 * company: kongtrolink
 * description:
 * update record:
 */

public interface LogControllerService {
    public JSONObject getMqttLogList(JSONObject query);

}

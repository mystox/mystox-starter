package com.kongtrolink.framework.gateway.service.receiver.instance;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.gateway.service.receiver.ReceiveHandler;
import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/10/16, 9:40
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class BaseReceiver extends ReceiveHandler {


    @Override
    public void start() {
        System.out.println(JSONObject.toJSONString(this));
    }

    @Override
    public void stop() {

    }

    @Override
    public void restart() {

    }



}

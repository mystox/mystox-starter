package com.kongtrolink.framework.api;

import com.kongtrolink.framework.mqtt.entity.MqttMsg;
import com.kongtrolink.framework.stereotype.OperaCode;
import com.kongtrolink.framework.stereotype.Register;

/**
 * Created by mystoxlol on 2019/8/15, 13:31.
 * company: kongtrolink
 * description:
 * update record:
 */
@Register
public interface LocalService {
    @OperaCode(code = "sayHi")
    String hello(MqttMsg mqttMsg);
}

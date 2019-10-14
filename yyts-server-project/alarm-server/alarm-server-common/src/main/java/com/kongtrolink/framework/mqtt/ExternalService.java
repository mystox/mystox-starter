package com.kongtrolink.framework.mqtt;

import com.kongtrolink.framework.stereotype.OperaCode;
import com.kongtrolink.framework.stereotype.Register;

/**
 * @Auther: liudd
 * @Date: 2019/9/26 09:21
 * @Description:外部MQTT接口
 */
@Register
public interface ExternalService {

    @OperaCode
    void alarmReport(String payload);

    @OperaCode
    void alarmRecover(String payload);

    @OperaCode
    void updateaAuxilary(String payload);
}

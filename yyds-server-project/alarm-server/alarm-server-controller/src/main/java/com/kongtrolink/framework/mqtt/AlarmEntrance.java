package com.kongtrolink.framework.mqtt;

import com.kongtrolink.framework.stereotype.OperaCode;
import com.kongtrolink.framework.stereotype.Register;

/**
 * @Auther: liudd
 * @Date: 2019/10/12 15:09
 * @Description:告警后台入口
 */
@Register
public interface AlarmEntrance {

    @OperaCode
    void alarmHandle(String payload);
}

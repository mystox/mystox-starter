package com.kongtrolink.mqtt;

import com.kongtrolink.framework.stereotype.OperaCode;
import com.kongtrolink.framework.stereotype.Register;

/**
 * @Auther: liudd
 * @Date: 2019/10/21 18:55
 * @Description:
 */
@Register
public interface DeliverEntrance {

    @OperaCode
    String handleDeliver(String alarmListJsonStr);
}

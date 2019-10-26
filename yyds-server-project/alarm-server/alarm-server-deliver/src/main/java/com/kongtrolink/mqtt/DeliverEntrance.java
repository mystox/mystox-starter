package com.kongtrolink.mqtt;

import com.kongtrolink.framework.stereotype.OperaCode;
import com.kongtrolink.framework.stereotype.Register;

/**
 * @Auther: liudd
 * @Date: 2019/10/21 18:55
 * @Description:投递动作
 */
@Register
public interface DeliverEntrance {

    @OperaCode
    String handleDeliver(String alarmListJsonStr);
}

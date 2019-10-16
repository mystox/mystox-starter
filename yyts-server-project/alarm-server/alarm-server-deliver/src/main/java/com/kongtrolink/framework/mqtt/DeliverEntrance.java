package com.kongtrolink.framework.mqtt;

import com.kongtrolink.framework.stereotype.Register;

/**
 * @Auther: liudd
 * @Date: 2019/10/15 17:01
 * @Description:
 */
@Register
public interface DeliverEntrance {

    void handleDelive(String alarmStr);

}

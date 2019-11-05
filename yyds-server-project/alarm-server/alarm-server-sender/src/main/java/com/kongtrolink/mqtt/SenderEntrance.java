package com.kongtrolink.mqtt;

import com.kongtrolink.framework.stereotype.OperaCode;
import com.kongtrolink.framework.stereotype.Register;

/**
 * @Auther: liudd
 * @Date: 2019/10/25 16:25
 * @Description:
 */
@Register
public interface SenderEntrance {

    @OperaCode
    void handleSender(String informMsgStr);
}

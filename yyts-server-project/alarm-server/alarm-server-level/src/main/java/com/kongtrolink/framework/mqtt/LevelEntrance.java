package com.kongtrolink.framework.mqtt;

import com.kongtrolink.framework.stereotype.OperaCode;
import com.kongtrolink.framework.stereotype.Register;

/**
 * @Auther: liudd
 * @Date: 2019/10/12 13:58
 * @Description:
 */
@Register
public interface LevelEntrance {

    @OperaCode
    String handleLevel(String alarm);
}

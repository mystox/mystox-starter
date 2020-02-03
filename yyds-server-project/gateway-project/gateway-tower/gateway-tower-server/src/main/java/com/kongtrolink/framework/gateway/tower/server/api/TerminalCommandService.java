package com.kongtrolink.framework.gateway.tower.server.api;

import com.kongtrolink.framework.stereotype.OperaCode;
import com.kongtrolink.framework.stereotype.Register;

/**
 * Created by mystoxlol on 2019/9/9, 12:55.
 * company: kongtrolink
 * description:
 * update record:
 */
@Register
public interface TerminalCommandService {

    @OperaCode(code = "deviceGet")
    String deviceGet(String message);


    @OperaCode(code = "setData")
    String setData(String message);

    @OperaCode(code="getDeviceDataModel")
    String getDeviceDataModel(String message);

    @OperaCode(code="getAlarmParam")
    String getAlarmParam(String message);

    @OperaCode(code="setAlarmParam")
    String setAlarmParam(String message);

    @OperaCode(code="getDeviceAlarmModel")
    String getDeviceAlarmModel(String message);

}

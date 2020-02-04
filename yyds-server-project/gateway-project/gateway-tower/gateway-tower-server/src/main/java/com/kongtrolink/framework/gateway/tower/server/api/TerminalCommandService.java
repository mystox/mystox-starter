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

    /**
     *  SC向GW请求监控点数据
     */
    @OperaCode(code = "getData")
    String getData(String message);
    /**
     * SC向GW设置监控点数据
     */
    @OperaCode(code = "setPoint")
    String setPoint(String message);
    /**
     * SC向GW请求门限值数据
     */
    @OperaCode(code="getThreshold")
    String getThreshold(String message);

    /**
     * SC向GW 设置门限值数据
     */
    @OperaCode(code="setThreshold")
    String setThreshold(String message);

    /**
     * SC向GW 获取FSU状态
     */
    @OperaCode(code="getFsuInfo")
    String getFsuInfo(String message);
    /**
     * SC向GW FSU重启
     */
    @OperaCode(code="reboot")
    String reboot(String message);
}

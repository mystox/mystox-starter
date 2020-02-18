package com.kongtrolink.framework.gateway.tower.server.api;

import com.kongtrolink.framework.core.constant.ScloudBusinessOperate;
import com.kongtrolink.framework.gateway.tower.core.constant.GatewayTonerOperate;
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
    @OperaCode(code = ScloudBusinessOperate.GET_DATA)
    String getData(String message);
    /**
     * SC向GW设置监控点数据
     */
    @OperaCode(code = ScloudBusinessOperate.SET_POINT)
    String setPoint(String message);
    /**
     * SC向GW请求门限值数据
     */
    @OperaCode(code= ScloudBusinessOperate.GET_THRESHOLD)
    String getThreshold(String message);

    /**
     * SC向GW 设置门限值数据
     */
    @OperaCode(code= ScloudBusinessOperate.SET_THRESHOLD)
    String setThreshold(String message);

    /**
     * SC向GW 获取FSU状态
     */
    @OperaCode(code= ScloudBusinessOperate.GET_FSUINFO)
    String getFsuInfo(String message);
    /**
     * SC向GW FSU重启
     */
    @OperaCode(code= ScloudBusinessOperate.REBOOT)
    String reboot(String message);

    /**
     * 心跳程序判断离线
     */
    @OperaCode(code= GatewayTonerOperate.ALARM_OFFLINE)
    String fsuOfflineAlarm(String message);
}

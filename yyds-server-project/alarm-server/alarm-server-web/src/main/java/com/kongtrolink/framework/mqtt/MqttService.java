package com.kongtrolink.framework.mqtt;

import com.kongtrolink.framework.stereotype.OperaCode;
import com.kongtrolink.framework.stereotype.Register;

/**
 * @Auther: liudd
 * @Date: 2020/2/28 10:02
 * @Description:告警服务mqtt远程接口，用于其他模块对告警表增删改查
 */
@Register
public interface MqttService {

    /**
     * @auther: liudd
     * @date: 2020/2/28 10:04
     * 功能描述:告警列表，需要判定所属表
     */
    @OperaCode
    String alarmRemoteList(String jsonStr);

    /**
     * @auther: liudd
     * @date: 2020/3/2 13:25
     * 功能描述:远程告警确认或消除
     */
    @OperaCode
    String alarmRemoteOperate(String jsonStr);

    @OperaCode
    String levelRemoteLastUse(String jsonStr);

    @OperaCode
    String auxilaryRemoteGet(String jsonStr);

    @OperaCode
    String auxilaryRemoteDel(String jsonStr);
}

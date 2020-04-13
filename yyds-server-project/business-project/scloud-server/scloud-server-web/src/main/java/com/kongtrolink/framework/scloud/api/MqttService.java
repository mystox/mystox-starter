package com.kongtrolink.framework.scloud.api;


import com.kongtrolink.framework.stereotype.OperaCode;
import com.kongtrolink.framework.stereotype.Register;

/**
 * @Auther: liudd
 * @Date: 2020/3/20 13:18
 * @Description:告警产生上报国动平台，插入国动业务相关字段
 */
@Register
public interface MqttService {

    @OperaCode
    String sdgdScloudAlarmReport(String jsonStr);

    @OperaCode
    String sdgdScloudAlarmResolve(String jsonStr);

    @OperaCode
    String sdgdScloudAlarmHistory(String jsonStr);
}

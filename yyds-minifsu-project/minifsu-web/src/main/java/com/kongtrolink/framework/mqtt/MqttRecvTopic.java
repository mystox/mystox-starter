/** *****************************************************
 * Copyright (C) Kongtrolink techology Co.ltd - All Rights Reserved
 *
 * This file is part of Kongtrolink techology Co.Ltd property.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 ****************************************************** */
package com.kongtrolink.framework.mqtt;

/**
 * MQTT 接收的报文主题
 * 
 * @author Mosaico
 */
public enum MqttRecvTopic {
    
	REPORT_ALARM("PubAlarmSCloud"),         // 实时告警推送
    REPORT_ALARM_END("PubAlarmEndSCloud"),  // 告警消除推送
    RESP_REALTIME_DATA("RspCurrentValSCloud"),      // 请求实时数据响应报文
    RESP_SINGLE_THRESHOLD("RspThresholdSCloud"),    // 设置单点遥信门限响应报文
    RESP_SIGNAL_VALUE("RspMonitorSCloud"),          // 设置（遥调/遥控）信号值响应报文
    RESP_MULTI_BATTERY_THRESHOLD("RspMultiThresholdScloud"),    // 设置多点遥信门限响应报文【仅蓄电池模块】
    REPORT_BATTERY_EVENT("PubEventSaved"),          // 蓄电池充放电事件推送【仅蓄电池模块】
    REPORT_BATTERY_FAULT("FaultReportToScloud"),    // BBDS 故障诊断推送【仅蓄电池模块】
//    REPORT_FSU_LOGIN("UpdateFSULoginSCloud"),       // FSU 注册更新推送【仅C接口模块】
    REPORT_FSU_LOGOUT_ALARM("FSULogoutAlarm"),       // FSU 离线告警推送【TODO 后续更改至Monitor程序】
    ;
    
    private MqttRecvTopic(String topic) {
        this.topic = topic;
    }
    
    private String topic;
    
    public String getTopicString() {
        return topic;
    }
    
}

package com.kongtrolink.framework.gateway.tower.core.entity.mqtt.topic;

/**
 * 发送给SC的报文
 * Created by Eric on 2019/6/17.
 */
public enum EnumMqttSendSCTopics {

    REPORT_ALARM("PubAlarmSCloud"),         // 实时告警推送
    REPORT_ALARM_END("PubAlarmEndSCloud"),  // 告警消除推送
    FSU_OFFLINE("FSULogoutAlarm"),      //Cntb->SC FSU离线
    RESP_UPDATE_DEVICE("RspUpdateDeviceSCloud"),    // 通知更新数据结构

    RSP_GET_CURRENT_VAL("RspCurrentValSCloud"),      //Cntb->SC 订阅后返回发送报文，请求实时数据
    RSP_GET_SIGNAL_THRESHOLD("RspGetThresholdSCloud"),     //Cntb->SC 订阅后返回发送报文，请求获取门限值
    RSP_SET_SIGNAL_VAL("RspMonitorSCloud"),     //Cntb->SC 订阅后返回发送报文，设置监控点数据
    RSP_SET_SIGNAL_THRESHOLD("RspThresholdSCloud")      //Cntb->SC 订阅后返回发送报文，设置门限值
    ;

    private String topic;

    public String getTopic() {
        return topic;
    }

    EnumMqttSendSCTopics(String topic) {
        this.topic = topic;
    }
}

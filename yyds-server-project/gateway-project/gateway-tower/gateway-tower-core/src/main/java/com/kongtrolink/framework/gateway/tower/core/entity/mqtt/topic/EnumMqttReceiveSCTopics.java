package com.kongtrolink.framework.gateway.tower.core.entity.mqtt.topic;

/**
 * 接收到来自SC的报文
 * Created by Eric on 2019/6/17.
 */
public enum EnumMqttReceiveSCTopics {

    RECV_UPDATE_SIGNAL_MAP("UpdateSignalMapSVR"),    // 更新信号映射表
    RECV_GET_CURRENT_VAL("ReqCurrentValSVR"),     //SC请求获取监控点实时数据发送报文
    RECV_GET_DI_THRESHOLD("GetThresholdSVR"),    //SC请求获取遥信门限值发送报文
    RECV_SET_POINT_VAL("SetMonitorSVR"),       //SC设置监控点数据发送报文
    RECV_SET_DI_THRESHOLD("SetThresholdSVR")       //SC设置遥信门限值发送报文
    ;

    private String topic ;

    EnumMqttReceiveSCTopics(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }
}

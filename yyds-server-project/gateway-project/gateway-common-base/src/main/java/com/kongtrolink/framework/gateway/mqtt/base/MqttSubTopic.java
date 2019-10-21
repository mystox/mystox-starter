package com.kongtrolink.framework.gateway.mqtt.base;

/**
 * 订阅FSU消息
 * mqtt subscribe m-box
 */
public enum MqttSubTopic {
    Register("Register"),                                   // 注册
    Heartbeat("Heartbeat"), // 发送心跳
    PushDeviceAsset("PushDeviceAsset"),                       // 推送设备资产信息
    GetDeviceAssetAck("GetDeviceAssetAck"),                      //获取设备资产信息
    GetDeviceDataModelAck("GetDeviceDataModelAck"),                   //获取设备数据模型信息
    PushRealtimeData("PushRealtimeData"), //上报变化数据
    PushAlarm("PushAlarm"),                 //上报告警
    SetDataAck("SetDataAck"), //设置数据点数据
    GetAlarmParamAck("GetAlarmParamAck"), //获取告警参数
    SetAlarmParamAck("SetAlarmParamAck"), //设置告警参数
    GetDeviceAlarmModelAck("GetDeviceAlarmModelAck"),
    GatewaySystemSetDevType("GatewaySystemSetDevType"); // 刷新资管设备类型映射表

    MqttSubTopic(String topic) {
        this.topic = topic;
    }

    private String topic;

    public String getTopicName() {
        return topic;
    }
}

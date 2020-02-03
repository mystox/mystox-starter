package com.kongtrolink.framework.gateway.tower.server.mqtt.base;

/**
 * mqtt publish m-box
 */
public enum MqttPubTopic {
    RegisterAck("RegisterAck"),     //  注册
    HeartbeatAck("HeartbeatAck"), // 发送心跳
    PushDeviceAssetAck("PushDeviceAssetAck"),//推送设备资产信息,
    GetDeviceAsset("GetDeviceAsset"), //获取设备资产信息
    GetDeviceDataModel("GetDeviceDataModel"), //获取设备数据模型信息
    PushRealtimeDataAck("PushRealtimeDataAck"), //上报变化数据
    PushAlarmAck("PushAlarmAck"),                 //上报告警
    SetData("SetData"), //设置数据点数据
    GetAlarmParam("GetAlarmParam"), //获取告警参数
    SetAlarmParam("SetAlarmParam"), //设置告警参数
    GetDeviceAlarmModel("GetDeviceAlarmModel"); // 获取设备告警模型信息


    MqttPubTopic(String topic) {
        this.topic = topic;
    }

    private String topic;

    public String getTopicName() {
        return topic;
    }
}

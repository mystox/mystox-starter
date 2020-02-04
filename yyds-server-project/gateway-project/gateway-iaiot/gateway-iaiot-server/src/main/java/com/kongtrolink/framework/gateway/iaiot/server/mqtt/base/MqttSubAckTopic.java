package com.kongtrolink.framework.gateway.iaiot.server.mqtt.base;

/**
 * 订阅FSU消息
 * mqtt subscribe m-box
 */
public enum MqttSubAckTopic {

    GetDeviceAssetAck("GetDeviceAssetAck"),                      //获取设备资产信息
    GetDeviceDataModelAck("GetDeviceDataModelAck"),                   //获取设备数据模型信息
    SetDataAck("SetDataAck"), //设置数据点数据
    GetAlarmParamAck("GetAlarmParamAck"), //获取告警参数
    SetAlarmParamAck("SetAlarmParamAck"), //设置告警参数
    GetDeviceAlarmModelAck("GetDeviceAlarmModelAck");

    MqttSubAckTopic(String topic) {
        this.topic = topic;
    }

    private String topic;

    public String getTopicName() {
        return topic;
    }
}

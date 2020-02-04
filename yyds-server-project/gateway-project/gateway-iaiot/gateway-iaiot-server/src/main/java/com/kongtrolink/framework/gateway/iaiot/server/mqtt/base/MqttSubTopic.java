package com.kongtrolink.framework.gateway.iaiot.server.mqtt.base;

/**
 * 订阅FSU消息
 * mqtt subscribe m-box
 */
public enum MqttSubTopic {
    Register("Register"),                                   // 注册
    Heartbeat("Heartbeat"), // 发送心跳
    PushDeviceAsset("PushDeviceAsset"),                       // 推送设备资产信息
    PushRealtimeData("PushRealtimeData"), //上报变化数据
    PushAlarm("PushAlarm"),                 //上报告警
    GatewaySystemSetDevType("GatewaySystemSetDevType"); // 刷新资管设备类型映射表

    MqttSubTopic(String topic) {
        this.topic = topic;
    }

    private String topic;

    public String getTopicName() {
        return topic;
    }
}

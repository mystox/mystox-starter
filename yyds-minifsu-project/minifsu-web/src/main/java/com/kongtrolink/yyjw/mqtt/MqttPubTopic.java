/** *****************************************************
 * Copyright (C) Kongtrolink techology Co.ltd - All Rights Reserved
 *
 * This file is part of Kongtrolink techology Co.Ltd property.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 ****************************************************** */
package com.kongtrolink.yyjw.mqtt;

/**
 * MQTT 发送的报文主题
 * 
 * @author Mosaico
 */
public enum MqttPubTopic {
    
    SCLOUD_LOGOUT_WILL("SVRLogout"),    // SCloud 服务器离线遗言
    
    GET_REALTIME_DATA("ReqCurrentValSVR"),      // 请求实时数据
    SET_SIGNAL_VALUE("SetMonitorSVR"),          // 设置（遥调/遥控）信号值
    SET_SINGLE_THRESHOLD("SetThresholdSVR"),    // 设置单点遥信门限
    UPDATE_SIGNAL_MAP("UpdateSignalMapSVR"),    // 更新信号映射表
    UPDATE_POLLING_INTERVAL("UpdatePollingIntervalSVR"),    // 更新 FSU 轮询时间
    SET_MULTI_BATTERY_THRESHOLD("SetMultiThresholdSVR"),     // 设置多点遥信门限【仅蓄电池模块】
    ;
    
    private MqttPubTopic(String topic) {
        this.topic = topic;
    }
    
    private String topic;
    
    public String getTopicString() {
        return topic;
    }
    
}

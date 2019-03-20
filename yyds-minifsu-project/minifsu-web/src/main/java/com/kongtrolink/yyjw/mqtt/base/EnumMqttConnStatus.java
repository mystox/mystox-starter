/** *****************************************************
 * Copyright (C) Kongtrolink techology Co.ltd - All Rights Reserved
 *
 * This file is part of Kongtrolink techology Co.Ltd property.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 ****************************************************** */
package com.kongtrolink.yyjw.mqtt.base;

public enum EnumMqttConnStatus {
    
    UNCONNECT,      // 未连接
    CONNECTED,      // 已连接未订阅
    SUBSCRIBED     // 已订阅
    
}

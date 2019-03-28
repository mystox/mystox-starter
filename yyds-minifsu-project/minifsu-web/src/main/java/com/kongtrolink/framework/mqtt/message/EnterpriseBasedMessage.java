/**
 * *****************************************************
 * Copyright (C) Kongtrolink techology Co.ltd - All Rights Reserved
 *
 * This file is part of Kongtrolink techology Co.Ltd property.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************
 */
package com.kongtrolink.framework.mqtt.message;

import com.kongtrolink.framework.core.entity.MqttStandardMessage;

/**
 * 基于企业的 MQTT 通信报文
 * 
 * @author Mosaico
 */
public class EnterpriseBasedMessage implements MqttStandardMessage {
    
    protected String uniqueCode;

    public EnterpriseBasedMessage(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }
    
}

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
package com.kongtrolink.yyjw.mqtt;

public abstract class MessageArrivedCallback {
    
    private final String requestId;

    /**
     * 需要确保唯一
     * @param requestId
     */
    public MessageArrivedCallback(String requestId) {
        this.requestId = requestId;
    }

    /**
     * 当 sub 消息到达时回调
     * @param message
     */
    public abstract void onMessage(String message) throws Exception;

    public String getRequestId() {
        return requestId;
    }
}

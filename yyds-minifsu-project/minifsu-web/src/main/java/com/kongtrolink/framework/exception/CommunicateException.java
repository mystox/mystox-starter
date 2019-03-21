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
package com.kongtrolink.framework.exception;

/**
 *
 * @author Mosaico
 */
public class CommunicateException extends Exception {

    public static final String ERROR_MESSAGE = "设备通讯故障";
    
    public CommunicateException() {
        super(ERROR_MESSAGE);
    }
    
}

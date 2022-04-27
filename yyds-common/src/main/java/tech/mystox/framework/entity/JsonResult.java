/**
 * *****************************************************
 * Copyright (C) mystox techology Co.ltd - All Rights Reserved
 * <p>
 * This file is part of mystox techology Co.Ltd property.
 * <p>
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * *****************************************************
 */
package tech.mystox.framework.entity;


import java.io.Serializable;

public class JsonResult<T> extends BaseResult implements Serializable {

    private T data;

    public JsonResult() {
    }

    public JsonResult(T data) {
        this.data = data;
    }

    public JsonResult(T data, String info) {
        super(true, info);
        this.data = data;
    }

    public JsonResult(Boolean success, String info) {
        this.info = info;
        this.success = success;
    }
    public JsonResult(String info,Boolean success) {
        this.info = info;
        this.success = success;
    }

    public String getInfo() {
        return info;
    }


    public Boolean getSuccess() {
        return success;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Boolean isSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "JsonResult{" +
                "info='" + info + '\'' +
                ", success=" + success +
                ", data=" + data +
                '}';
    }

}

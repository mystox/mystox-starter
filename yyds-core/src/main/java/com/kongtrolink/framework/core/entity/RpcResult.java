package com.kongtrolink.framework.core.entity;

import java.io.Serializable;

/**
 * xx
 * by Mag on 2019/3/27.
 */
public class RpcResult implements Serializable {
    private static final long serialVersionUID = -5534262132985266560L;

    private int result;
    private String info;

    public RpcResult() {
    }

    public RpcResult(int result, String info) {
        this.result = result;
        this.info = info;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}

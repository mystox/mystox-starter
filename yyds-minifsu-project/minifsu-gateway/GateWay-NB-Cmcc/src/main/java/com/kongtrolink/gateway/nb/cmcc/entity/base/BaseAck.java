package com.kongtrolink.gateway.nb.cmcc.entity.base;

import java.io.Serializable;

/**
 * xx
 * by Mag on 2019/2/14.
 */
public class BaseAck implements Serializable {
    private static final long serialVersionUID = -1384703611454821149L;

    private int errno;
    private String error;
    private Object data;

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseAck{" +
                "errno=" + errno +
                ", error='" + error + '\'' +
                ", data=" + data +
                '}';
    }
}

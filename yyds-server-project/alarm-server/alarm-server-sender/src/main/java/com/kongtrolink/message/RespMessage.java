package com.kongtrolink.message;/**
 * Created by gipplelake on 2015/4/17.
 */

/**
 * @author dengqg
 */
public class RespMessage {
    private String message;
    private String info;
    private String result;
    private Integer statusCode;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(Object info) {
        this.info = info.toString();
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
}

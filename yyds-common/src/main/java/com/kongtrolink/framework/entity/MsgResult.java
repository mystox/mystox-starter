package com.kongtrolink.framework.entity;

/**
 * Created by mystoxlol on 2019/9/9, 1:10.
 * company: kongtrolink
 * description:
 * update record:
 */
public class MsgResult {
    private Integer stateCode;
    private String msg;

    public Integer getStateCode() {
        return stateCode;
    }

    public void setStateCode(Integer stateCode) {
        this.stateCode = stateCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public MsgResult(Integer stateCode, String msg) {
        this.msg = msg;
    }
}

package com.kongtrolink.framework.entity;

/**
 * Created by mystoxlol on 2019/9/9, 1:10.
 * company: kongtrolink
 * description:
 * update record:
 */
public class MsgResult {
    private int stateCode;
    private String msg;

    public int getStateCode() {
        return stateCode;
    }

    public void setStateCode(int stateCode) {
        this.stateCode = stateCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public MsgResult(int stateCode, String msg) {
        this.msg = msg;
        this.stateCode = stateCode;
    }
}

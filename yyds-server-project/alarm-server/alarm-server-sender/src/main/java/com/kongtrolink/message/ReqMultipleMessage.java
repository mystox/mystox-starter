package com.kongtrolink.message;/**
 * Created by gipplelake on 2015/4/15.
 */

import com.alibaba.fastjson.JSONObject;

/**
 * @author dengqg
 */
public class ReqMultipleMessage extends ReqBaseMessage {

    private JSONObject tos;

    public JSONObject getTos() {
        return tos;
    }

    public void setTos(JSONObject tos) {
        this.tos = tos;
    }
}

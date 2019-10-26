package com.kongtrolink.message;/**
 * Created by gipplelake on 2015/4/15.
 */

import com.alibaba.fastjson.JSONObject;

/**
 * @author dengqg
 */
public class ReqSingleMessage extends ReqBaseMessage {

    private String phone;
    private JSONObject vars;//替换变量的json串


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public JSONObject getVars() {
        return vars;
    }

    public void setVars(JSONObject vars) {
        this.vars = vars;
    }
}

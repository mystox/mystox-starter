package com.kongtrolink.framework.enttiy;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.base.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: liudd
 * @Date: 2019/9/24 10:31
 * @Description:附属属性。精确到企业-服务
 */
public class Auxilary {

    private String _id;
    private String enterpriseCode;
    private String serverCode;
    private Map<String, String> proMap = new HashMap<>();

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Map<String, String> getProMap() {
        return proMap;
    }

    public void setProMap(Map<String, String> proMap) {
        this.proMap = proMap;
    }

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    public static void main(String[] args){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", "id001");
        jsonObject.put("username", "liudd");
        jsonObject.put("name", "大东哥");
        jsonObject.put("pwd", "123456");
        System.out.println(jsonObject);
        String id = jsonObject.getString("id");
        jsonObject.remove("id");
        System.out.println(jsonObject);
        System.out.println("id:" + id);

    }
}

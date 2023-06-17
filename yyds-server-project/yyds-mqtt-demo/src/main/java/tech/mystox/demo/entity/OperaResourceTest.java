package tech.mystox.demo.entity;

import com.alibaba.fastjson2.JSONObject;

/**
 * \* @Author: mystox
 * \* Date: 2020/1/5 23:19
 * \* Description:
 * \
 */
public class OperaResourceTest {
    private String groupCode;
    private JSONObject jsonObject;

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}
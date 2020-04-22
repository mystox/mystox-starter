package com.kongtrolink.framework.enttiy;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/24 10:31
 * @Description:附属属性。精确到企业-服务
 */
public class Auxilary {

    private String _id;
    private String enterpriseCode;
    private String serverCode;
    private List<String> proStrList = new ArrayList<>();
    private List<String> proNameList = new ArrayList<>();

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public List<String> getProStrList() {
        return proStrList;
    }

    public void setProStrList(List<String> proStrList) {
        this.proStrList = proStrList;
    }

    public List<String> getProNameList() {
        return proNameList;
    }

    public void setProNameList(List<String> proNameList) {
        this.proNameList = proNameList;
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
}

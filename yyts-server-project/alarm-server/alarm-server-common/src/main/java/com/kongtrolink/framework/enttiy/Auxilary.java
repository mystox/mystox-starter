package com.kongtrolink.framework.enttiy;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/24 10:31
 * @Description:附属属性。精确到企业-服务
 */
public class Auxilary {

    private String _id;
    private String uniqueCode;
    private String service;
    private List<String> proStrList;
    private List<String> proNameList;
    private List<String> proTypeList;

    public List<String> getProNameList() {
        return proNameList;
    }

    public void setProNameList(List<String> proNameList) {
        this.proNameList = proNameList;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public List<String> getProStrList() {
        return proStrList;
    }

    public void setProStrList(List<String> proStrList) {
        this.proStrList = proStrList;
    }


    public List<String> getProTypeList() {
        return proTypeList;
    }

    public void setProTypeList(List<String> proTypeList) {
        this.proTypeList = proTypeList;
    }
}

package com.kongtrolink.framework.query;

import com.kongtrolink.framework.base.Paging;

/**
 * @Auther: liudd
 * @Date: 2019/9/24 10:36
 * @Description:
 */
public class AuxilaryQuery extends Paging{

    private String _id;
    private String uniqueCode;
    private String service;
    private String proStr;
    private String proName;
    private String proType;

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

    public String getProStr() {
        return proStr;
    }

    public void setProStr(String proStr) {
        this.proStr = proStr;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public String getProType() {
        return proType;
    }

    public void setProType(String proType) {
        this.proType = proType;
    }
}

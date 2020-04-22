package com.kongtrolink.framework.scloud.query;

/**
 * @Auther: liudd
 * @Date: 2019/9/24 10:36
 * @Description:
 */
public class AuxilaryQuery extends Paging{

    private String _id;
    private String enterpriseCode;
    private String serverCode;
    private String proStr;
    private String proName;
    private String proType;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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

package com.kongtrolink.framework.mqtt;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/10/15 10:49
 * @Description:获取CI请求实体
 */
public class CIRequestEntity {

    private String enterpriseCode;
    private String serverCode;
    private String type;
    private String id;
    private List<String> ids;
    private String sn;
    private List<String> sns;
    private List<String> addressCodes;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public List<String> getSns() {
        return sns;
    }

    public void setSns(List<String> sns) {
        this.sns = sns;
    }

    public List<String> getAddressCodes() {
        return addressCodes;
    }

    public void setAddressCodes(List<String> addressCodes) {
        this.addressCodes = addressCodes;
    }
}

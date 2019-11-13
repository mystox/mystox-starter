package com.kongtrolink.framework.enttiy;

/**
 * @Auther: liudd
 * @Date: 2019/11/12 09:11
 * @Description:历史告警存储信息
 * 由于历史告警数量巨大，单纯的使用分片不一定能支持。故采用分表策略，为了加快分页查询，参考分片思想，建立一张表，用于记录各个分表的起始记录
 */
public class HistoryAlarmStorageInfo {

    private String _id;
    private String enterpriseCode;
    private String serverCode;
    private int beginNum;
    private int endNum;

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

    public int getBeginNum() {
        return beginNum;
    }

    public void setBeginNum(int beginNum) {
        this.beginNum = beginNum;
    }

    public int getEndNum() {
        return endNum;
    }

    public void setEndNum(int endNum) {
        this.endNum = endNum;
    }
}

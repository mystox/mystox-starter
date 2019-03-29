package com.kongtrolink.framework.jsonType;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/3/29 13:29
 * @Description:
 */
public class JsonFsu {

    private Integer pktType;
    private Integer dtm;
    private String SN;
    private List<JsonDevice> data;

    public Integer getPktType() {
        return pktType;
    }

    public void setPktType(Integer pktType) {
        this.pktType = pktType;
    }

    public Integer getDtm() {
        return dtm;
    }

    public void setDtm(Integer dtm) {
        this.dtm = dtm;
    }

    public String getSN() {
        return SN;
    }

    public void setSN(String SN) {
        this.SN = SN;
    }

    public List<JsonDevice> getData() {
        return data;
    }

    public void setData(List<JsonDevice> data) {
        this.data = data;
    }
}

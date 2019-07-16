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

    /**
     * @auther: liudd
     * @date: 2019/5/24 13:52
     * 功能描述:根据dev获取列表中对应的JsonDevice
     */
    public JsonDevice getJsonDeviceByDev(String dev){
        if(null == data){
            return null;
        }
        for(JsonDevice device : data){
            if(dev.equals(device.getDev())){
                return device;
            }
        }
        return null;
    }

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

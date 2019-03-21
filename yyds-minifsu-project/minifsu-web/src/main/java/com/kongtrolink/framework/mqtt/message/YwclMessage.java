package com.kongtrolink.framework.mqtt.message;

import com.alibaba.fastjson.JSONObject;

/**
 * \* @Author: mystox
 * \* Date: 2018/11/30 13:29
 * \* Description:
 * \
 */
public class YwclMessage implements MqttStandardMessage {
    private String pkt_type;
    private Long time;
    private String fsu_serial_no;
    private JSONObject data;
    private String resp_topic;
    private String fsu_id;


    public YwclMessage(String pkt_type, Long time, String fsu_id)
    {
        this.pkt_type = pkt_type;
        this.time = time;
        this.fsu_id = fsu_id;
    }

    public YwclMessage(String pkt_type, Long time, String fsu_serial_no, JSONObject data, String resp_topic, String fsu_id)
    {
        this.pkt_type = pkt_type;
        this.time = time;
        this.fsu_serial_no = fsu_serial_no;
        this.data = data;
        this.resp_topic = resp_topic;
        this.fsu_id = fsu_id;
    }

    public String getFsu_id()
    {
        return fsu_id;
    }

    public void setFsu_id(String fsu_id)
    {
        this.fsu_id = fsu_id;
    }

    public String getResp_topic()
    {
        return resp_topic;
    }

    public void setResp_topic(String resp_topic)
    {
        this.resp_topic = resp_topic;
    }


    public String getPkt_type() {
        return pkt_type;
    }

    public void setPkt_type(String pkt_type) {
        this.pkt_type = pkt_type;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getFsu_serial_no() {
        return fsu_serial_no;
    }

    public void setFsu_serial_no(String fsu_serial_no) {
        this.fsu_serial_no = fsu_serial_no;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(String data) {
        this.data = JSONObject.parseObject(data);
    }
}
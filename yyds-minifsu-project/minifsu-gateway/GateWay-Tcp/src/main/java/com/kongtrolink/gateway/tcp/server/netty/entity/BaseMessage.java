package com.kongtrolink.gateway.tcp.server.netty.entity;

import java.io.Serializable;

/**
 * xx
 * by Mag on 2019/3/19.
 */
public class BaseMessage implements Serializable {

    private static final long serialVersionUID = -8151646689864544811L;
    /**
     * 报文类型
     * Reserved	0	禁止	保留
       CONNECT	1	客户端到服务端	客户端请求连接服务端
       CONNECT_ACK	2	服务端到客户端	客户端请求连接服务端的响应
       SEND	3	双向	发送消息
       SEND_ACK	4	双向	发送消息的确认
     */
    private int type;
    private String message;

    public  BaseMessage(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public BaseMessage() {
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

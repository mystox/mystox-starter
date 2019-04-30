package com.kongtrolink.gateway.tcp.server.netty.entity;

/**
 * 常用字典值
 * by Mag on 2019/3/18.
 */
public class GatewayConstant {
    /**
     *  Reserved	0	禁止	保留
        CONNECT	1	客户端到服务端	客户端请求连接服务端
        CONNECT_ACK	2	服务端到客户端	客户端请求连接服务端的响应
        SEND	3	双向	发送消息
        SEND_ACK	4	双向	发送消息的确认
     */
    public static final byte FixedHeader_Type_Reserved = 0;
    public static final byte FixedHeader_Type_CONNECT = 1;
    public static final byte FixedHeader_Type_CONNECT_ACK = 2;
    public static final byte FixedHeader_Type_SEND = 3;
    public static final byte FixedHeader_Type_SEND_ACK = 4;


}

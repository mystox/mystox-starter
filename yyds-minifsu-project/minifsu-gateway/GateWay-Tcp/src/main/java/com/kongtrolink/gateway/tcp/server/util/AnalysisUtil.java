package com.kongtrolink.gateway.tcp.server.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * 解析工具类
 * by Mag on 2019/3/18.
 */
public class AnalysisUtil {

    private static final Logger logger = LoggerFactory.getLogger(AnalysisUtil.class);

    public static void main(String[] args) {
        int[]  x = {0xA3,0xBB,0x06,0x5B,0x7B,0x22};
        int len = 0;
        for(int i = 0;i<x.length;i++){
            int num = x[i];
            //是否是 分包 true 分包
            boolean flag = getIndexFirst(num);
            //右移 7 + 位数
            int value = getIndexLastSeven(num) << (7*i);
            System.out.println(i+"==> first: " + flag +" value: "+ value);
            len = len  + value;
            if(!flag){
                System.out.println("完整字段长度为 :"+len);
                break;
            }
        }
        System.out.println(" ==================== ");
        int[] value = getByteLenInfo(2097152);
        for(int i:value){
            System.out.println(i);
        }

    }


    /**
     * 获取 其实位数 是否是1
     * @param i 传入参数
     * @return 是否是 完整
     *  Variable Header
     */
    public static boolean getIndexFirst(int i){
        return getBitIndex(i,7);
    }

    /**
     * 取得 长度
     */
    public static int getIndexLastSeven(int i){
        return i & 0x7F;
    }

    /**
     * 获取 整数 num 的第 i 位的值
     * @param num 整数
     * @param i 第i位
     * @return 如果 num 的第 i 位 是 1 时返回 true
     */
    public static boolean getBitIndex(int num, int i) {
        return ((num & (1 << i)) != 0);//true 表示第i位为1,否则为0
    }

    /**
     * Fixed Header 固定报文头，所有报文都包含
     * 报文类型
     * Reserved	0	禁止	保留
       CONNECT	1	客户端到服务端	客户端请求连接服务端
       CONNECT_ACK	2	服务端到客户端	客户端请求连接服务端的响应
       SEND	3	双向	发送消息
       SEND_ACK	4	双向	发送消息的确认
     */
    public static int getFixedHeaderType(byte i){
        return i >> 4;
    }

    /**
     *
     报文类型	报文类型标志	3	2	1	0
     Reserved	Reserved	0	0	0	0
     CONNECT	Reserved	0	0	0	0
     CONNECT_ACK	Reserved	0	0	0	0
     SEND	应用于当前版本TCP协议	DUP	QoS	QoS	0
     SEND_ACK	Reserved	0	0	0	0
     */
    public static int getFixedHeaderFlag(byte i){
        return i & 0x0F;
    }

    public static int getByteLen(String value){
        if(value ==null){
            return 0;
        }
        byte[]  buff = value.getBytes();
        return buff.length;
    }

    public static int getByteLen(byte[] value){
        if(value ==null){
            return 0;
        }
        return value.length;
    }

    /**
     * 根据返回包 返回长度字段
     * @param len 长度
     */
    public static int[] getByteLenInfo(int len){
        if(len < 128){
            int[] result = {len};
            return result;
        }else if(len > 127 && len <= 16383){
            //第二个字节 第一位 为 0 表示结束
            int by2 = len >> 7;
            //第一个字节为 第一位 为 1 表示有值
            int by1 = (len & 0x7F )| 0x80;
            int[] result = {by1,by2};
            return result;
        }else if(len > 16383 && len <= 2097151){
            //第三个字节 第一位 为 0 表示结束
            int by3 = len >> 14;
            //第二个字节 第一位 为 1 表示有值
            int by2 = ((len >> 7 ) & 0x7F ) | 0x80;
            //第一个字节为 第一位 为 1 表示有值
            int by1 = (len & 0x7F )| 0x80;
            int[] result = {by1,by2,by3};
            return result;
        }else if(len > 2097151 && len <= 268435455){
            //第四个字节 第一位 为 0 表示结束
            int by4 = len >> 21;
            //第三个字节 第一位 为 1 表示结束
            int by3 = ((len >> 14 ) & 0x7F ) | 0x80;
            //第二个字节 第一位 为 1 表示有值
            int by2 = ((len >> 7 ) & 0x7F ) | 0x80;
            //第一个字节为 第一位 为 1 表示有值
            int by1 = (len & 0x7F )| 0x80;
            int[] result = {by1,by2,by3,by4};
            return result;
        }
        return null;
    }

    public static ByteBuf getMessageBuffer(String message,byte header) {
        int byteLen = AnalysisUtil.getByteLen(message);
        int[] lenInfo = AnalysisUtil.getByteLenInfo(byteLen);
        if (lenInfo == null) {
            logger.info("解析 message:{} 长度:{} 失败 ", message, byteLen);
            return null;
        } else {
            int lenInfoLen = lenInfo.length;
            ByteBuf frame = Unpooled.buffer(1 + lenInfoLen + byteLen);
            frame.writeByte(header);
            for (int x : lenInfo) {
                frame.writeByte(x);
            }
            frame.writeCharSequence(message, StandardCharsets.UTF_8);
            return frame;
        }
    }
    public static ByteBuf getMessageBuffer(byte[] message,byte header) {
        int byteLen = AnalysisUtil.getByteLen(message);
        int[] lenInfo = AnalysisUtil.getByteLenInfo(byteLen);
        if (lenInfo == null) {
            logger.info("解析 message:{} 长度:{} 失败 ", message, byteLen);
            return null;
        } else {
            int lenInfoLen = lenInfo.length;
            ByteBuf frame = Unpooled.buffer(1 + lenInfoLen + byteLen);
            frame.writeByte(header);
            for (int x : lenInfo) {
                frame.writeByte(x);
            }
            frame.writeBytes(message);
            return frame;
        }
    }

}

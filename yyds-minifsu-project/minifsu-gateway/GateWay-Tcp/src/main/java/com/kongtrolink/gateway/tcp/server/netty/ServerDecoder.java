package com.kongtrolink.gateway.tcp.server.netty;

import com.kongtrolink.gateway.tcp.server.netty.entity.BaseMessage;
import com.kongtrolink.gateway.tcp.server.netty.entity.GatewayConstant;
import com.kongtrolink.gateway.tcp.server.util.AnalysisUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;


/**
 * Created by mag 
 * on 2017/07/14. 
 * C1 接口服务端解码处理
 */
public class ServerDecoder extends LengthFieldBasedFrameDecoder{
	
    private static final Logger logger = LoggerFactory.getLogger(ServerDecoder.class);

    public ServerDecoder() {
        /*
            LengthFieldBasedFrameDecoder(
                ByteOrder byteOrder     解码的大小端模式
                int maxFrameLength,     解码时，处理每个帧数据的最大长度 - 无最大值
                int lengthFieldOffset,  该帧数据中，存放该帧数据的长度的数据的起始位置
                int lengthFieldLength,  记录该帧数据长度的字段本身的长度
                int lengthAdjustment,   修改帧数据长度字段中定义的值，可以为负数
                int initialBytesToStrip 解析的时候需要跳过的字节数
                boolean failFast        为true，当frame长度超过maxFrameLength时立即报TooLongFrameException异常，为false，读取完整个帧再报异常
            )
        */
        super(ByteOrder.LITTLE_ENDIAN, Integer.MAX_VALUE, 4, 8, -8, 0, true);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
    	try{
    		byte header = in.readByte();
            /**
             * 取得 固定报文头 报文类型
             * Fixed Header 固定报文头，所有报文都包含
             * 报文类型
             * Reserved	0	禁止	保留
              CONNECT	1	客户端到服务端	客户端请求连接服务端
              CONNECT_ACK	2	服务端到客户端	客户端请求连接服务端的响应
              SEND	3	双向	发送消息
              SEND_ACK	4	双向	发送消息的确认
             */
            int  fixedHeaderType = AnalysisUtil.getFixedHeaderType(header);
            if(fixedHeaderType == GatewayConstant.FixedHeader_Type_Reserved){
                logger.info("Fixed Header 固定报文头 的 报文类型 为 Reserved 禁止 保留");
                return "";
            }else if(fixedHeaderType == GatewayConstant.FixedHeader_Type_CONNECT){
                logger.info("Fixed Header 固定报文头 的 报文类型 为 CONNECT  客户端请求连接服务端");
                byte len = in.readByte();
                byte connectHeader = in.readByte();
                byte connectFlag =  in.readByte();
                if(len == 2 && connectHeader == 1 && connectFlag==0){
                    return new BaseMessage(1,"CONNECT");
                }else{
                    logger.error("CONNECT解析失败:len:{}(应2),connectHeader:{}(应1),connectFlag:{}(应0)",len,connectHeader,connectFlag);
                    throw new Exception("解析失败");
                }
            }else if(fixedHeaderType == GatewayConstant.FixedHeader_Type_SEND
                    || fixedHeaderType == GatewayConstant.FixedHeader_Type_SEND_ACK){
                logger.info("Fixed Header 固定报文头 的 报文类型 为 Send/SEND_ACK 发送消息");
                Integer len = getMessagePayLoad(in,0,0);
                if(len==null){
                    logger.error("  Send 解析失败 ");
                    throw new Exception("解析失败");
                }else{
                    int readIndex = in.readerIndex();
                    int total = len+2;
                    int packet = in.writerIndex();
                    if(total > packet){
                        logger.error("  Send 接收包 不完整 包长度为:{} 实际内容长度为:{} ",total,packet);
                        return null;
                    }
                    String message = in.getCharSequence(readIndex,len, StandardCharsets.UTF_8).toString().trim();
                    in.clear();
                    return new BaseMessage(fixedHeaderType,message);
                }
            }else{
                logger.info("Fixed Header 固定报文头 的 报文类型 {} 无法识别", fixedHeaderType);
                throw new Exception("解析失败");
            }
    	}catch(Exception e){
            logger.error("解析失败:"+e.getMessage());

    		if(in!=null){
    			in.clear();//异常情况 清空 缓冲区 放弃此次读取
    		}
    	}
        return "";
    }

    /**
     * 获取字符串信息
     * @param in 缓存区
     * @param lenNum 第几个长度字节 从0开始
     * @return 字符串信息
     */
    private Integer getMessagePayLoad(ByteBuf in,int value ,int lenNum){
        int len = in.readByte();
        if(len<0){
            len = len & 0xff;
        }
        boolean flag = AnalysisUtil.getIndexFirst(len);
        //长度字节 只有4个 1个固定 3个变量，如果超过3个还没完结 则这个包是错误包
        if(lenNum>4){
            return null;
        }
        //左移 7 + 位数 注 位运算符 优先级低于加减
        int result = value + (AnalysisUtil.getIndexLastSeven(len) << (7*lenNum));
        if(flag){
            return getMessagePayLoad(in,result,(lenNum+1));
        }else{
            return result;
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close(); //解析异常 不进行关闭
    }
}

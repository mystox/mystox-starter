package com.kongtrolink.gateway.tcp.server.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.gateway.tcp.server.entity.NorthClearUpMessage;
import com.kongtrolink.gateway.tcp.server.entity.NorthMessage;
import com.kongtrolink.gateway.tcp.server.execute.SendTool;
import com.kongtrolink.gateway.tcp.server.netty.entity.BaseMessage;
import com.kongtrolink.gateway.tcp.server.util.AnalysisUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Created by mag on 2017/07/14.
 * C1 接口业务相关接口处理
 */
public class ServiceHandle extends ChannelInboundHandlerAdapter {

    private SendTool sendTool;

    private static final Logger logger = LoggerFactory.getLogger(ServiceHandle.class);

    //读取数据保存至数据库

    public ServiceHandle(SendTool sendTool) {
        this.sendTool = sendTool;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    /**
     * 服务器 关闭 之后触发事件
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("netty =>断开");
        String channelId = ctx.channel().id().toString();
        if (ChannelRepository.channelCache.containsKey(channelId)) {//如果 包含key 则是异常退出 进行重连
            ChannelRepository.channelCache.remove(channelId);
        }
        NorthClearUpMessage northMessage = new NorthClearUpMessage(channelId, sendTool.getServerIp(), sendTool.getServerName());
        String value = JSONObject.toJSONString(northMessage);
        sendTool.sendMsgTest(UUID.randomUUID().toString(),value);
        logger.info("netty 服务器断开 服务器正常关闭", ctx.channel().remoteAddress());
        super.channelInactive(ctx);//如果 是 主动 登出退出操作 则不进行重连

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        BaseMessage baseMessage = (BaseMessage) msg;
        String channelId = ctx.channel().id().toString();
        if (!ChannelRepository.channelCache.containsKey(channelId)) {
            ChannelRepository.channelCache.put(channelId, ctx);
        }
        if (1 == baseMessage.getType()) {
            ByteBuf frame = Unpooled.buffer(2);
            frame.writeByte(0x20);
            frame.writeByte(0);
            ctx.writeAndFlush(frame);
            logger.info(" ###  返回 CONNECT 报文");
        }
        if (3 == baseMessage.getType() || 4 == baseMessage.getType()) {
            logger.info(" ----> 收到 SEND 报文 解析成字符串:" + baseMessage.getMessage());
            String message = baseMessage.getMessage();
            NorthMessage northMessage = new NorthMessage();
            northMessage.setUuid(channelId);
            northMessage.setPayload(message);
            northMessage.setGip(sendTool.getServerIp() + ":" + sendTool.getRpcPort());
            String value = JSONObject.toJSONString(northMessage);
            RpcNotifyProto.RpcMessage result = sendTool.sendMsgTest("",value);

            if (result != null && sendTool.isSynMark()) {//同步开关
            JSONObject json = JSON.parseObject(result.getPayload());
                //同步接口返回执行结果
                if (result.getPayloadType() == RpcNotifyProto.PayloadType.BYTE)//返回流结果
                    returnByte(ctx,result.getBytePayload());
                else
                    jsonResult(ctx,json.toJSONString());
                logger.info("rpc 发送成功...");
            } else {
                logger.error("rpc 发送失败");
            }
        } else {
            logger.info("无法识别");
        }
    }

    private void jsonResult(ChannelHandlerContext ctx, String message) {

        ByteBuf frame = AnalysisUtil.getMessageBuffer(message,(byte)3);
        if(frame!=null){
            ctx.writeAndFlush(frame);
            logger.info("数据返回成功");
        }
    }

    private void returnByte(ChannelHandlerContext ctx, ByteString payload) {
        ByteBuf frame = AnalysisUtil.getMessageBuffer(payload.toByteArray(),(byte)3);
        if(frame!=null){
            ctx.writeAndFlush(frame);
            logger.info("数据返回成功");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof UnsupportedMessageTypeException) {
            logger.error("Channel {} {}, close it", ctx.channel().remoteAddress(), cause.getMessage());
            ctx.close();
        } else {
            super.exceptionCaught(ctx, cause);
        }
    }

}

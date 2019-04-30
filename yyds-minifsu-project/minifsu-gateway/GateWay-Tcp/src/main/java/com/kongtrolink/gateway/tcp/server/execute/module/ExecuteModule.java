package com.kongtrolink.gateway.tcp.server.execute.module;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.kongtrolink.framework.core.entity.RpcResult;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.protobuf.protorpc.RpcNotifyImpl;
import com.kongtrolink.framework.core.service.ModuleInterface;
import com.kongtrolink.gateway.tcp.server.netty.ChannelRepository;
import com.kongtrolink.gateway.tcp.server.util.AnalysisUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/2/25, 19:25.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class ExecuteModule extends RpcNotifyImpl implements ModuleInterface {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ThreadPoolTaskExecutor controllerExecutor;

    public boolean init() {
        logger.info("workerExecute-execute module init");
        //初始化线程池之类的基础任务服务
        return true;
    }

    /**
     * 默认业务程序执行体
     */
    @Override
    protected RpcNotifyProto.RpcMessage execute(String msgId, String payload) {
        JSONObject json = JSON.parseObject(payload);
        String uuid = json.getString("uuid");
        String message = json.getString("payload");
        RpcResult result = new RpcResult();

        try{
            if(!ChannelRepository.channelCache.containsKey(uuid)){
                result.setResult(0);
                result.setInfo("uuid:"+uuid+" 未找到对应连接");

            }else{
                ChannelHandlerContext ctx = ChannelRepository.channelCache.get(uuid);
                ByteBuf frame = AnalysisUtil.getMessageBuffer(message,(byte)3);
                if(frame==null){
                    result.setResult(0);
                    result.setInfo("网关 转码 解析 message:{}  失败");
                }else{
                    ctx.writeAndFlush(frame);
                    result.setResult(1);
                    result.setInfo("success");
                    logger.info(" 下发数据成功");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            result.setResult(0);
            result.setInfo("网关 转码 解析 message:{}  失败");
        }
        String back = JSONObject.toJSONString(result);
        return RpcNotifyProto.RpcMessage.newBuilder()
                .setType(RpcNotifyProto.MessageType.RESPONSE)
                .setPayloadType(RpcNotifyProto.PayloadType.JSON)
                .setPayload(back)
                .setMsgId(StringUtils.isBlank(msgId)?"":msgId)
                .build();
    }
    /**
     * 字节流请求的入口
     *
     * @param msgId
     * @param payload
     * @return
     */
    @Override
    protected RpcNotifyProto.RpcMessage bytesExecute(String msgId, String uuid, ByteString payload) {

        RpcResult result = new RpcResult();
        try{
            if(!ChannelRepository.channelCache.containsKey(uuid)){
                result.setResult(0);
                result.setInfo("uuid:"+uuid+" 未找到对应连接");

            }else{
                ChannelHandlerContext ctx = ChannelRepository.channelCache.get(uuid);
                ByteBuf frame = AnalysisUtil.getMessageBuffer(payload.toByteArray(),(byte)3);
                if(frame==null){
                    result.setResult(0);
                    result.setInfo("网关 转码 解析 message:{}  失败");
                }else{
                    ctx.writeAndFlush(frame);
                    result.setResult(1);
                    result.setInfo("success");
                    logger.info(" 下发数据成功");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            result.setResult(0);
            result.setInfo("网关 转码 解析 message:{}  失败");
        }
        String back = JSONObject.toJSONString(result);
        return RpcNotifyProto.RpcMessage.newBuilder()
                .setType(RpcNotifyProto.MessageType.RESPONSE)
                .setPayloadType(RpcNotifyProto.PayloadType.JSON)
                .setPayload(back)
                .setMsgId(StringUtils.isBlank(msgId)?"":msgId)
                .build();
    }

}

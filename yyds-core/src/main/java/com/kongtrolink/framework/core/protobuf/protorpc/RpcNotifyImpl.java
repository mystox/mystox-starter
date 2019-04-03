package com.kongtrolink.framework.core.protobuf.protorpc;

import com.google.protobuf.RpcController;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mystoxlol on 2019/1/23, 13:07.
 * company: kongtrolink
 * description:
 * update record:
 */
public abstract class RpcNotifyImpl implements RpcNotify, RpcNotifyProto.RpcNotify.BlockingInterface
{

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public RpcNotifyProto.RpcMessage notify(RpcController controller, RpcNotifyProto.RpcMessage rpcMessage)
    {
        RpcNotifyProto.RpcMessage result = messageExecute(rpcMessage);
        return result;
    }

    /**
     * 获取消息执行基本消息判断
     * @param rpcMessage
     * @return
     */
    protected RpcNotifyProto.RpcMessage messageExecute(RpcNotifyProto.RpcMessage rpcMessage)
    {
        logger.info("message execute msgId:[{}],payload:[{}]", rpcMessage.getMsgId(),rpcMessage.getPayload());
        String msgId = rpcMessage.getMsgId();
        String service = rpcMessage.getService();
        String method = rpcMessage.getMethod();
        String jsonPayLoad = rpcMessage.getPayload();
        RpcNotifyProto.PayloadType payloadType = rpcMessage.getPayloadType();
        //根据service & method 具体执行业务逻辑 如果没有指定service与方法, 则直接执行默认方法 service | method 指定但未找到配置,则放回错误
        RpcNotifyProto.RpcMessage result = null;
        if (StringUtils.isBlank(service))
        {
//            JSONObject object = JSONObject.parseObject(jsonPayLoad);
            result = execute(msgId, jsonPayLoad);//不同服务执行不一样的流程
        }
        if (StringUtils.isNotBlank(service) && StringUtils.isNotBlank(method))
        {
            //根据service和method 选择对应的类和方法执行业务逻辑
            result = serviceExecute(msgId, service, method, jsonPayLoad);//不同服务执行不一样的流程
        }
        logger.info("message result msgId:[{}],msgType:[{}],payload:[{}]", result.getMsgId(),result.getType(),result.getPayload());
        return result;
    }

    /**
     * 单一的proto请求通过实现execute处理(如告警处理,网关上报等 需要重载实现
     * @param msgId
     * @param payLoad
     * @return
     */
    protected RpcNotifyProto.RpcMessage execute(String msgId, String payLoad)
    {
        return RpcNotifyProto.RpcMessage.newBuilder()
                .setType(RpcNotifyProto.MessageType.RESPONSE)
                .setPayloadType(RpcNotifyProto.PayloadType.JSON)
                .setPayload("execute never implement...")
                .setMsgId(StringUtils.isBlank(msgId) ? "" : msgId)
                .build();
    }

    /**
     * 包含service&method 的请求通过serviceExecute处理 需要重载实现
     * @param msgId
     * @param service
     * @param method
     * @param payLoad
     * @return
     */
    protected RpcNotifyProto.RpcMessage serviceExecute(String msgId, String service, String method, String payLoad)
    {
        return RpcNotifyProto.RpcMessage.newBuilder()
                .setType(RpcNotifyProto.MessageType.RESPONSE)
                .setPayloadType(RpcNotifyProto.PayloadType.JSON)
                .setPayload("serviceExecute never implement...")
                .setMsgId(StringUtils.isBlank(msgId) ? "" : msgId)
                .build();
    }


//    protected abstract String messageExecute(String msgId, String service, String method, String payload);

    public static void main(String[] args)
    {
        RpcNotifyProto.RpcMessage rpcMessage = RpcNotifyProto.RpcMessage.newBuilder()
                .setType(RpcNotifyProto.MessageType.RESPONSE)
                .setPayloadType(RpcNotifyProto.PayloadType.BYTE)
                .setPayload("serviceExecute never implement...")
                .setMsgId("")
                .build();
        RpcNotifyProto.PayloadType payloadType = rpcMessage.getPayloadType();
//        System.out.println(payloadType == RpcNotifyProto.PayloadType.BYTE.getNumber());
    }

}

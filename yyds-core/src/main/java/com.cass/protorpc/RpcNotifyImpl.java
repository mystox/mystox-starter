package com.cass.protorpc;

import com.cass.rpc.protorpc.RpcNotifyProto;
import com.google.protobuf.RpcController;

/**
 * Created by mystoxlol on 2019/1/23, 13:07.
 * company: kongtrolink
 * description:
 * update record:
 */
public abstract class  RpcNotifyImpl implements RpcNotify, RpcNotifyProto.RpcNotify.BlockingInterface
{

    @Override
    public RpcNotifyProto.RpcMessage notify(RpcController controller, RpcNotifyProto.RpcMessage rpcMessage)
    {
        System.out.println(rpcMessage.toString());
        /*return RpcNotifyProto.RpcMessage.newBuilder().setStrResponse("abc")
                .setService("abc")
                .setId(11)
                .setType(RpcNotifyProto.MessageType.RESPONSE)
                .build();*/
        String result = messageExecute(rpcMessage.getContent());
        return RpcNotifyProto.RpcMessage.newBuilder().setContent(result)
                .build();
    }

    protected abstract String messageExecute(String content);


}

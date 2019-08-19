package com.kongtrolink.framework.core.protobuf.protorpc;

import com.google.protobuf.RpcController;
import com.kongtrolink.framework.core.rpc.ProtocolEnum;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import org.apache.hadoop.ipc.ProtocolInfo;

/**
 * Created by mystoxlol on 2019/1/23, 13:07.
 * company: kongtrolink
 * description:
 * update record:
 */
@ProtocolInfo(protocolName = "com.kongtrolink.framework.core.protobuf.protorpc.RpcNotify",
        protocolVersion = ProtocolEnum.VERSION)
public interface RpcNotify
{
    RpcNotifyProto.RpcMessage notify(RpcController controller, RpcNotifyProto.RpcMessage rpcMessage);
}

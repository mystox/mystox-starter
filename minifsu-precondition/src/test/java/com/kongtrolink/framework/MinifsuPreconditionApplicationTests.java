package com.kongtrolink.framework;

import com.kongtrolink.framework.core.protobuf.protorpc.RpcNotify;
import com.cass.rpc.protorpc.RpcNotifyProto;
import com.kongtrolink.framework.core.rpc.ProtocolEnum;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.ProtobufRpcEngine;
import org.apache.hadoop.ipc.RPC;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.InetSocketAddress;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MinifsuPreconditionApplicationTests
{

    @Test
    public void contextLoads()
    {
    }

    /**
     * rpcmain测试方法
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException
    {
        Configuration conf = new Configuration();
        String ADDRESS = "172.16.6.142";
        int port = 9981;

        ADDRESS = "127.0.0.1";
        port = 9998;
        port = 18888;
        RpcNotifyProto.RpcMessage message = RpcNotifyProto.RpcMessage.newBuilder()
//                .setContent(UUID.randomUUID().toString())
				.setContent("hello i'm clinet111mysto")
                .build();
        byte[] bytes = message.toByteArray();
        RPC.setProtocolEngine(conf, RpcNotify.class, ProtobufRpcEngine.class);
        RpcNotify proxy = RPC.getProxy(RpcNotify.class, ProtocolEnum.VERSION, new InetSocketAddress(ADDRESS, port),
                conf);
        RpcNotifyProto.RpcMessage result = proxy.notify(null, message);


//        for (int i=0;i<10; i++)
//            result = proxy.Notify(null,message);
        System.out.println("client result:" + result);
    }


}

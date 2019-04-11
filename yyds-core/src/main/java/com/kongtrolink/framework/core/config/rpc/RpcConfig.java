package com.kongtrolink.framework.core.config.rpc;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import org.apache.hadoop.ipc.RPC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Created by mystoxlol on 2019/1/19, 10:31.
 * company: kongtrolink
 * description:
 * update record:
 */
@Configuration
public class RpcConfig
{
    @Value("${server.bindIp:127.0.0.1}")
    private String bindAddress;
    @Value("${server.rpc.port:10000}")
    private Integer rpcPort;
    @Value("${rpc.client.timeout:30000}")
    private String rpcClientTimeout;

    @Bean
    RpcServer rpcServer(RPC.Builder builder) throws IOException
    {
        return new RpcServer(configuration(),builder,bindAddress,rpcPort);
    }

    @Bean
    RPC.Builder builder()
    {
        return new RPC.Builder(configuration());
    }

    @Bean
    org.apache.hadoop.conf.Configuration configuration()
    {
        org.apache.hadoop.conf.Configuration configuration = new org.apache.hadoop.conf.Configuration();
        configuration.set("ipc.client.rpc-timeout.ms", rpcClientTimeout);
        return configuration;
    }
    @Bean
    RpcClient rpcClient()
    {
        return new RpcClient(configuration());
    }


    RpcController rpcController()
    {
       return  new RpcController()
        {
            @Override
            public void reset()
            {

            }

            @Override
            public boolean failed()
            {
                return false;
            }

            @Override
            public String errorText()
            {
                return null;
            }

            @Override
            public void startCancel()
            {

            }

            @Override
            public void setFailed(String reason)
            {

            }

            @Override
            public boolean isCanceled()
            {
                return false;
            }

            @Override
            public void notifyOnCancel(RpcCallback<Object> callback)
            {

            }
        };
    }

}

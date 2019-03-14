package com.kongtrolink.framework.core.config.rpc;

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
    @Value("${server.bindIp}")
    private String bindAddress;
    @Value("${server.rpc.port}")
    private Integer rpcPort;

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
        return new org.apache.hadoop.conf.Configuration();
    }
    @Bean
    RpcClient rpcClient()
    {
        return new RpcClient(configuration());
    }


}

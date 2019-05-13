package com.kongtrolink.gateway.tcp.server.execute;

import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.gateway.tcp.server.execute.module.RpcModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * xx
 * by Mag on 2019/3/22.
 */
@Service
@RefreshScope
public class SendTool {

    private static final Logger logger = LoggerFactory.getLogger(SendTool.class);

    @Value("${send.synchronization.mark:true}")
    private boolean synMark;


    @Value("${rpc.controller.port}")
    private int toPort;

    @Value("${rpc.controller.hostname}")
    private String toHost;

    @Value("${server.name}")
    private String serverName;

    @Value("${server.bindIp}")
    private String serverIp;

    @Value("${server.rpc.port}")
    private String rpcPort;

    @Autowired
    RpcModule rpcModule;

    private InetSocketAddress addr = null;

    public RpcNotifyProto.RpcMessage sendMsgTest(String msgId,String message) {
        try{
            if(addr==null){
                this.addr = new InetSocketAddress(toHost,toPort);
            }
//            String msgId = UUID.randomUUID().toString();
            RpcNotifyProto.RpcMessage result = rpcModule.postMsg("", this.addr,message);
            logger.info("收到 rpc 返回: {} ",result.toString());
            return result;
        } catch (IOException e)
        {
            logger.error("rpc执行错误...");
            e.printStackTrace();
        }
        return null;
    }
    public InetSocketAddress getAddr() {
        return addr;
    }

    public void setAddr(InetSocketAddress addr) {
        this.addr = addr;
    }

    public String getServerName() {
        return serverName;
    }

    public String getServerIp() {
        return serverIp;
    }

    public String getRpcPort() {
        return rpcPort;
    }

    public void setRpcPort(String rpcPort) {
        this.rpcPort = rpcPort;
    }

    public boolean isSynMark() {
        return synMark;
    }

}

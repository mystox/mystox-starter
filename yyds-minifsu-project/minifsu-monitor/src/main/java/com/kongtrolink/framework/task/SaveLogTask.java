package com.kongtrolink.framework.task;

import com.kongtrolink.framework.execute.module.RpcModule;

import java.net.InetSocketAddress;

/**
 * @Auther: liudd
 * @Date: 2019/4/8 16:46
 * @Description:
 */
public class SaveLogTask implements Runnable{

    private String msgId;
    private InetSocketAddress address;
    private String payload;
    private RpcModule rpcModule;

    public SaveLogTask(String msgId, InetSocketAddress address, String payload, RpcModule rpcModule) {
        this.msgId = msgId;
        this.address = address;
        this.payload = payload;
        this.rpcModule = rpcModule;
    }

    public void run(){
        try {
            rpcModule.postMsg(msgId, address, payload);
        }catch (Exception e){
            System.out.println("日志记录错误");
        }
    }
}

package com.kongtrolink.framework.core.tcp;

/**
 * Created by mystoxlol on 2019/2/22, 10:15.
 * company: kongtrolink
 * description:
 * update record:
 */
public abstract class AbstractTCPService
{
    public void start(String tcpIp, int tcpPort)
    {
        //todo tcp服务初始化逻辑
    }

    public abstract String receiveCommand(String[] args);
}

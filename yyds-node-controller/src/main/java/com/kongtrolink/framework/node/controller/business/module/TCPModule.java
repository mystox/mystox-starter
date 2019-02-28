package com.kongtrolink.framework.node.controller.business.module;

import com.kongtrolink.framework.core.service.ModuleInterface;
import com.kongtrolink.framework.core.tcp.AbstractTCPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/2/22, 9:58.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class TCPModule extends AbstractTCPService implements ModuleInterface
{
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    CommandModule commandModule;

    @Value("${server.bindIp}")
    private String bindIp;
    @Value("${server.tcp.port}")
    private int tcpPort;


    /**
     * tcp服务监听获取命令处理方法
     *
     * @param args
     * @return
     */
    public String receiveCommand(String[] args)
    {
        //TODO 接收命令交CommandModule 处理
        return commandModule.apiExecute(args);
    }

    @Override
    public boolean init()
    {
        logger.info("apiService-tcp module init");
        super.start(bindIp, tcpPort);
        return true;
    }
}

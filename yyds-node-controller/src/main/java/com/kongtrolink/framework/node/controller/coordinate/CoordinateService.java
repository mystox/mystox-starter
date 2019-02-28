package com.kongtrolink.framework.node.controller.coordinate;

import com.kongtrolink.framework.core.entity.RegistryContext;
import com.kongtrolink.framework.core.service.CommandApiInterface;
import com.kongtrolink.framework.core.service.ServiceInterface;
import com.kongtrolink.framework.node.controller.coordinate.module.ControllerRegistryModule;
import com.kongtrolink.framework.node.controller.coordinate.module.LoaderBalancerModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/2/19, 13:54.
 * company: kongtrolink
 * description: 分布式协调控制器服务
 * update record:
 */
@Service
public class CoordinateService implements ServiceInterface, CommandApiInterface
{
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${rpc.server.name}")
    private String nodePath;

    @Value("${register.cluster.id}")
    private String clusterId;

    @Autowired
    ControllerRegistryModule controllerRegistryModule;

    @Autowired
    LoaderBalancerModule loaderBalancerModule;

    @Override
    public boolean startService()
    {
        logger.info("Coordinate service starting ... ...");
        //初始化注册
        if (initRegister())
            if (initBalancer())
                return true;
            else logger.error("coordinate-balancer init error...");
        else logger.error("coordinate-register init error...");
        //启动定时任务注册信息
        return false;
    }

    private boolean initRegister()
    {
        RegistryContext registryContext = new RegistryContext("/" + clusterId, null);


        return controllerRegistryModule.register(registryContext) //服务节点注册
                && controllerRegistryModule.init(); //注册更新初始化
    }

    private boolean initBalancer()
    {
        //负载均衡模块初始化
        return loaderBalancerModule.init();
    }

    @Override
    public boolean restartService()
    {
        return false;
    }

    @Override
    public String commandExecute(String order, String parameter)
    {
        return null;
    }
}

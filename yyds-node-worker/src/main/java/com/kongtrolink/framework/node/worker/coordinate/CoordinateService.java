package com.kongtrolink.framework.node.worker.coordinate;

import com.kongtrolink.framework.core.entity.RegistryContext;
import com.kongtrolink.framework.core.service.ServiceInterface;
import com.kongtrolink.framework.node.worker.coordinate.module.WorkerRegistryModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/2/22, 20:22.
 * company: kongtrolink
 * description: 协调注册控制器
 * update record:
 */
@Service
public class CoordinateService implements ServiceInterface
{

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Value("${server.name}")
    private String nodeName;
    @Autowired
    WorkerRegistryModule workerRegistryModule;
    @Override
    public boolean startService()
    {
        logger.info("Coordinate service start... ...");
        return initRegister();
    }

    private boolean initRegister()
    {

        //注册节点
        String nodePath = "/" + nodeName;
        RegistryContext registryContext = new RegistryContext(nodePath,null);
        return (workerRegistryModule.register(registryContext))
                && workerRegistryModule.init();

    }

    @Override
    public boolean restartService()
    {
        return false;
    }
}

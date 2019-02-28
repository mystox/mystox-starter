package com.kongtrolink.framework.node.controller.coordinate.module;

import com.kongtrolink.framework.core.entity.RegistryContext;
import com.kongtrolink.framework.core.entity.WorkerNodeData;
import com.kongtrolink.framework.core.node.register.DefaultServiceRegistry;
import com.kongtrolink.framework.node.controller.entity.ControllerNodeData;
import com.kongtrolink.framework.node.controller.entity.InfoData;
import com.kongtrolink.framework.core.service.ModuleInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by mystoxlol on 2019/2/19, 10:05.
 * company: kongtrolink
 * description: 分布式协调控制服务-注册模块类
 * update record:
 */
@Service
public class ControllerRegistryModule extends DefaultServiceRegistry implements ModuleInterface
{
    private Logger LOG = LoggerFactory.getLogger(this.getClass());
    private ControllerNodeData cNodeData; //控制节点信息实体
    private InfoData iData;//控制节点集群信息实体
    private List<WorkerNodeData> wNodeDatas;//集群下的工作节点信息集合
    @Autowired
    ControllerNodeDataCollector cNodeDataCollector;//控制节点采集类


    @Autowired
    @Qualifier(value = "coordinateSchedule")
    ScheduledExecutorService scheduledExecutorService;//调度控制器

    /**
     * 控制节点注册
     */
    @Override
    public boolean register(Object context)
    {

        RegistryContext context1 = (RegistryContext) context;
        LOG.info("coordinate-registry controller node register {}", context1.getPath());
        //todo 向集群注册节点信息
        return true;
    }

    /**
     * 节点数据更新
     */
    public boolean init()
    {
        LOG.info("coordinate-registry-schedule init");
        //todo 注册策略
        scheduledExecutorService.scheduleWithFixedDelay(() ->
        {
            LOG.info("coordinate-registry-schedule update controller node data...");
            //todo 节点信息采集
            //todo 获取works信息
            //更新info信息
            //更新Controller
            //更新works信息
            //todo 向controller节点更新信息
        }, 1, 3, TimeUnit.SECONDS);
        return true;
    }
}

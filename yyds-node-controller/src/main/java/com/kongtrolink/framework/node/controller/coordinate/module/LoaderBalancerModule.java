package com.kongtrolink.framework.node.controller.coordinate.module;

import com.kongtrolink.framework.node.controller.entity.WorkLoader;
import com.kongtrolink.framework.core.entity.WorkerNodeData;
import com.kongtrolink.framework.core.service.ModuleInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by mystoxlol on 2019/2/19, 14:44.
 * company: kongtrolink
 * description: 负载均衡模块
 * update record:
 */
@Service
public class LoaderBalancerModule implements ModuleInterface
{
    Logger LOG = LoggerFactory.getLogger(this.getClass());
    @Value("${balancer.pluginUrl}")
    private String pluginUrl; //插件目录
    @Value("${balancer.className}")
    private String className; //类名(全路径)
    @Value("${balancer.methodName}")
    private String methodName;  //方法名称
    private Date bTime; //负载更新时间

    private List<WorkLoader> sequenceLoad ; //负载排名表

    @Autowired
    @Qualifier(value = "coordinateSchedule")
    ScheduledExecutorService scheduledExecutorService;

    private List<WorkerNodeData> getRegistryWorkCollections()
    {
        LOG.debug("获取工作节点注册信息集合...");
        //todo
        return null;
    }

    private void balanceArithmetic(List<WorkerNodeData> workerNodeDatas)
    {
        LOG.debug("负载算法执行...");
        PluginLoader pluginLoader = new PluginLoader(pluginUrl,className,methodName);
        setSequenceLoad((List<WorkLoader>) pluginLoader.executeByJar(workerNodeDatas));
        //todo
    }

    @Override
    public boolean init()
    {
        scheduledExecutorService.scheduleWithFixedDelay(() ->
        {
            LOG.debug("负载均衡定时器...");
            //获取work 集合
            List<WorkerNodeData> workerNodeDatas = getRegistryWorkCollections();
            //负载均衡生成负载排名表
            balanceArithmetic(workerNodeDatas);
            bTime = new Date(System.currentTimeMillis());
        }, 1, 3, TimeUnit.SECONDS);
        return true;
    }

    public List<WorkLoader> getSequenceLoad()
    {
        return sequenceLoad;
    }

    public void setSequenceLoad(List<WorkLoader> sequenceLoad)
    {
        this.sequenceLoad = sequenceLoad;
    }
}

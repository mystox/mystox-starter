package com.kongtrolink.framework.node.worker.coordinate.module;

import com.kongtrolink.framework.core.entity.RegistryContext;
import com.kongtrolink.framework.core.entity.WorkerNodeData;
import com.kongtrolink.framework.core.node.register.DefaultServiceRegistry;
import com.kongtrolink.framework.core.service.ModuleInterface;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by mystoxlol on 2019/2/25, 9:39.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class WorkerRegistryModule  extends DefaultServiceRegistry implements ModuleInterface
{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private WorkerNodeData wNodeData;
    private String nodePath;

    @Autowired
    @Qualifier(value = "coordinateSchedule")
    ScheduledExecutorService scheduledExecutorService;//调度控制器

    @Autowired
    WorkerNodeDataCollector workerNodeDataCollector;

    @Value("{cluster.id}")
    private String clusterId;

    public boolean init()
    {

        //更新works信息
        logger.info("coordinate-registry module init");
        //todo 注册策略
        scheduledExecutorService.scheduleWithFixedDelay(() ->
        {
            logger.info("coordinate-registry-schedule update worker node data...");
            //todo 节点信息采集
            collectorWorkerData();
            //todo 向worker节点更新信息
            updateNodeData();
        }, 1, 3, TimeUnit.SECONDS);

        return true;
    }

    private void updateNodeData()
    {
        RegistryContext context = new RegistryContext(nodePath,wNodeData);
        super.setData(context);
    }

    @Override
    public boolean register(Object context)
    {
        try
        {
            RegistryContext context1 = (RegistryContext)context;
            logger.info("coordinate-registry {} register" , context1.getPath());
        //todo 判断集群节点是否存在，不存在返回错误
            if (zooKeeper.exists("/" + clusterId, true) == null)
                return true;

        //todo 向集群注册节点
        } catch (KeeperException e)
        {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 采集工作节点信息
     */
    private void collectorWorkerData()
    {
        //todo 采集工作节点信息
        workerNodeDataCollector.getWorkerNodeDate();
    }

    public WorkerNodeData getwNodeData()
    {
        return wNodeData;
    }

    public void setwNodeData(WorkerNodeData wNodeData)
    {
        this.wNodeData = wNodeData;
    }
}

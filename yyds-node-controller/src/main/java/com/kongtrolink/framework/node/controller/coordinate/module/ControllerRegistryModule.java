package com.kongtrolink.framework.node.controller.coordinate.module;

import com.kongtrolink.framework.core.entity.RegistryContext;
import com.kongtrolink.framework.core.entity.WorkerNodeData;
import com.kongtrolink.framework.core.exception.EnumErrorCode;
import com.kongtrolink.framework.core.node.register.DefaultServiceRegistry;
import com.kongtrolink.framework.core.service.ModuleInterface;
import com.kongtrolink.framework.node.controller.entity.ControllerNodeData;
import com.kongtrolink.framework.node.controller.entity.InfoData;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Date;
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
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private ControllerNodeData cNodeData; //控制节点信息实体
    private InfoData iData;//控制节点集群信息实体
    private List<WorkerNodeData> wNodeDatas;//集群下的工作节点信息集合

    @Value("${register.periodTime}") int periodTime;
    @Autowired
    ControllerNodeDataCollector cNodeDataCollector;//控制节点采集类

    private volatile boolean initMark = false;

    @Autowired
    @Qualifier(value = "coordinateSchedule")
    ScheduledExecutorService scheduledExecutorService;//调度控制器

    private String clusterIdPath;
    private String infoPath;
    private String controllerPath;
    private String workerPath;

    /**
     * 控制节点注册
     */
    @Override
    public boolean register(Object context)
    {
        RegistryContext context1 = (RegistryContext) context;
        this.clusterIdPath = context1.getPath();
        this.infoPath = clusterIdPath + "/info";
        this.controllerPath = clusterIdPath + "/controller";
        this.workerPath = clusterIdPath + "/worker";
        logger.info("coordinate-registry controller node register {}", clusterIdPath);
        try
        {
            if (!exists(clusterIdPath))
            {//集群节点是否存在
                create(new RegistryContext(clusterIdPath), CreateMode.PERSISTENT);//新建集群编号节点
                logger.info("create clusterId:{}", clusterIdPath);
                //创建controller节点
                this.cNodeData = new ControllerNodeData();
                cNodeData.setStartTime(new Date());
                create(new RegistryContext(infoPath), CreateMode.EPHEMERAL);//只有controller节点为临时节点
                //创建worker节点
                create(new RegistryContext(workerPath), CreateMode.PERSISTENT);
                //todo 监听worker节点
                //创建info节点
                this.iData = new InfoData();
                iData.setStartTime(new Date());
                create(new RegistryContext(infoPath, iData), CreateMode.PERSISTENT);
            } else
            {
                if (exists(controllerPath))
                {
                    logger.error(EnumErrorCode.CONTROLLER_COORDINATION_REGISTRY_NODEEXISTS.getEntity(), controllerPath);
                    return false;
                } else
                {
                    this.cNodeData = new ControllerNodeData();
                    cNodeData.setStartTime(new Date());
                    create(new RegistryContext(controllerPath, cNodeData), CreateMode.EPHEMERAL);//创建controller临时节点
                    if (exists(infoPath))
                    {
                        this.iData = getNodeData(new RegistryContext(infoPath), InfoData.class);

                    } else
                    {
                        this.iData = new InfoData();
                        iData.setStartTime(new Date());
                        create(new RegistryContext(infoPath, iData), CreateMode.PERSISTENT); //info 保留节点
                    }
                    if (!exists(workerPath))
                        create(new RegistryContext(workerPath), CreateMode.PERSISTENT); //info 保留节点
                    return true; //注册成功
                }
            }
        } catch (KeeperException | UnsupportedEncodingException e)
        {
            logger.error(EnumErrorCode.CONTROLLER_COORDINATION_REGISTRY_ZOOKEEPERERROR.getEntity(), e.toString());
            e.printStackTrace();
            return false;
        } catch (InterruptedException e)
        {
            logger.error(EnumErrorCode.CONTROLLER_COORDINATION_REGISTRY_NETWORKERERROR.getEntity(), e.toString());
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * 节点数据更新
     */
    public boolean init()
    {
        logger.info("coordinate-registry-schedule init");
        scheduledExecutorService.scheduleWithFixedDelay(() ->
        {
            logger.info("coordinate-registry-schedule update controller node data...");
            // 获取works信息
            try
            {
                if (collectWorks())
                {
                    //info信息采集
                        this.iData =cNodeDataCollector.collectInfoData();

                    //controller信息采集
                        this.cNodeData = cNodeDataCollector.collectControllerNodeData();


                    //更新info信息
                    super.setData(new RegistryContext(infoPath, iData));
                    //更新Controller
                    super.setData(new RegistryContext(controllerPath, cNodeData));

                    //更新works信息
                } else
                {
                    logger.error(EnumErrorCode.CONTROLLER_COORDINATION_REGISTRY_SCHEDULE.getEntity());
                }
            } catch (UnsupportedEncodingException | KeeperException e)
            {
                logger.error(EnumErrorCode.CONTROLLER_COORDINATION_REGISTRY_SCHEDULE.getEntity(), e.toString());
                e.printStackTrace();
            } catch (InterruptedException e)
            {
                logger.error(EnumErrorCode.CONTROLLER_COORDINATION_REGISTRY_NETWORKERERROR.getEntity(), e.toString());
                e.printStackTrace();
            }

            initMark = true;
        }, 1, periodTime, TimeUnit.SECONDS);
        while (!initMark)
        {
            try
            {
                //等待注冊更新
                logger.info("waiting init schedule...");
                Thread.sleep(1000l);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
                return false;

            }
        }
        return initMark;
    }

    /**
     * 采集workers节点的信息集合
     * @return
     * @throws InterruptedException
     * @throws UnsupportedEncodingException
     * @throws KeeperException
     */
    boolean collectWorks() throws InterruptedException, UnsupportedEncodingException, KeeperException
    {
        this.setwNodeDatas(cNodeDataCollector.getWorkNodeDates());
        return true;
    }


    public List<WorkerNodeData> getwNodeDatas()
    {
        return wNodeDatas;
    }

    public void setwNodeDatas(List<WorkerNodeData> wNodeDatas)
    {
        this.wNodeDatas = wNodeDatas;
    }

    public InfoData getiData()
    {
        return iData;
    }

    public boolean isInitMark()
    {
        return initMark;
    }

    public void setInitMark(boolean initMark)
    {
        this.initMark = initMark;
    }

    public void setiData(InfoData iData)
    {
        this.iData = iData;
    }

    public ControllerNodeData getcNodeData()
    {
        return cNodeData;
    }

    public void setcNodeData(ControllerNodeData cNodeData)
    {
        this.cNodeData = cNodeData;
    }
}

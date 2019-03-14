package com.kongtrolink.framework.node.controller.coordinate.module;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.WorkerNodeData;
import com.kongtrolink.framework.core.node.collect.BaseNodeDataCollector;
import com.kongtrolink.framework.node.controller.entity.ControllerNodeData;
import com.kongtrolink.framework.node.controller.entity.InfoData;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mystoxlol on 2019/2/19, 13:46.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class ControllerNodeDataCollector extends BaseNodeDataCollector
{
    @Autowired
    protected ZooKeeper zooKeeper;
    @Value("${register.cluster.id}")
    private String clusterId;
    @Value("${server.tcp.port}")
    private int tcpPort;

    @Autowired
    ControllerRegistryModule controllerRegistryModule;


    @Autowired
    LoaderBalancerModule loaderBalancerModule;

    public InfoData collectInfoData()
    {
        InfoData infoData = controllerRegistryModule.getiData();
        if (infoData == null)
        {
            infoData = new InfoData();
            infoData.setStartTime(new Date());
        }
        List<WorkerNodeData> workerNodeDatas = controllerRegistryModule.getwNodeDatas();
        if (workerNodeDatas == null)
        {
            workerNodeDatas = new ArrayList<>();
            controllerRegistryModule.setwNodeDatas(workerNodeDatas);
        }
        infoData.setWorkNumOnline(workerNodeDatas.size());
        infoData.setUpdateTime(new Date());
        return infoData;
    }

    public ControllerNodeData collectControllerNodeData()
    {
        ControllerNodeData cNodeData = controllerRegistryModule.getcNodeData();
        if (cNodeData == null)
        {
            cNodeData = new ControllerNodeData();
            cNodeData.setStartTime(new Date());
        }
        cNodeData.setIp(getIp());
        cNodeData.setPort(getPort());//controller port 为服务tcp 端口 其他端口定义修改getPort()方法即可
        cNodeData.setName(getName());
        cNodeData.setUpdateTime(new Date());
        cNodeData.setbTime(getBalanceTime());
        return cNodeData;
    }

    public int getPort()
    {
        return tcpPort;
    }

    public Date getBalanceTime()
    {
        //TODO
        loaderBalancerModule.getbTime();
        return null;
    }

    public List<WorkerNodeData> getWorkNodeDates() throws KeeperException, InterruptedException, UnsupportedEncodingException
    {
        String workersPath = "/" + clusterId + "/worker";
        //TODO 采集worker节点信息
        List<WorkerNodeData> workerNodeDatas = new ArrayList<>();

        System.out.println(zooKeeper);
        List<String> workersPathList = zooKeeper.getChildren(workersPath, false);
        if (workersPathList != null && workersPathList.size() > 0)
        {
            for (String workerPath : workersPathList)
            {
                workerPath = workersPath + "/" + workerPath;
                String pathData = new String(zooKeeper.getData(workerPath, false, null), "utf-8");
                WorkerNodeData workerNodeData = JSONObject.parseObject(pathData, WorkerNodeData.class);
                workerNodeDatas.add(workerNodeData);
            }
        }
        return workerNodeDatas;

    }

}

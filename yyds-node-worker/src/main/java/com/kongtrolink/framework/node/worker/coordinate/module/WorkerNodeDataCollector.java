package com.kongtrolink.framework.node.worker.coordinate.module;

import com.kongtrolink.framework.core.entity.WorkerNodeData;
import com.kongtrolink.framework.core.node.collect.BaseNodeDataCollector;
import com.kongtrolink.framework.core.node.collect.NodeDataCollector;
import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/2/22, 20:17.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class WorkerNodeDataCollector extends BaseNodeDataCollector implements NodeDataCollector
{

    public WorkerNodeData getWorkerNodeDate()
    {
        //TODO 采集节点信息逻辑
        return null;
    }
}

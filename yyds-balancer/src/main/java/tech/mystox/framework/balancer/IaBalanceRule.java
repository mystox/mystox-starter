package tech.mystox.framework.balancer;

import tech.mystox.framework.entity.ServerMsg;
import tech.mystox.framework.scheduler.LoadBalanceScheduler;

/**
 * Created by mystoxlol on 2020/6/8, 10:37.
 * company:
 * description: 复杂均衡规则定义接口
 * update record:
 */
public interface IaBalanceRule {
    ServerMsg choose(Object var1);

    void setLoadBalancer(LoadBalanceScheduler var1);

    LoadBalanceScheduler getLoadBalancer();
}

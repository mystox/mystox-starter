package tech.mystox.framework.balancer.rule;

import tech.mystox.framework.balancer.IaBalanceRule;
import tech.mystox.framework.entity.ServerMsg;
import tech.mystox.framework.scheduler.LoadBalanceScheduler;

/**
 * Created by mystoxlol on 2020/6/8, 15:20.
 * company:
 * description:
 * update record:
 */
public class BaseLoadBalanceRule implements IaBalanceRule {
    @Override
    public ServerMsg choose(Object var1) {
        return null;
    }

    @Override
    public void setLoadBalancer(LoadBalanceScheduler var1) {

    }

    @Override
    public LoadBalanceScheduler getLoadBalancer() {
        return null;
    }
}

package tech.mystox.framework.entity;

import tech.mystox.framework.config.IaConf;

/**
 * Created by mystoxlol on 2020/6/9, 14:03.
 * company:
 * description:
 * update record:
 */
public class BalancerContext {
    private IaConf.LoadBalanceType loadBalanceType;

    public BalancerContext() {
    }


    public BalancerContext build() {
        return new BalancerContext();
    }

    public BalancerContext balanceType(IaConf.LoadBalanceType loadBalanceType) {
        this.loadBalanceType = loadBalanceType;
        return this;
    }


    public IaConf.LoadBalanceType getLoadBalanceType() {
        return loadBalanceType;
    }

}

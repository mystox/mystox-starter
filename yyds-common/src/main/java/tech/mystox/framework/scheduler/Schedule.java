package tech.mystox.framework.scheduler;

import tech.mystox.framework.core.OperaCall;
import tech.mystox.framework.core.IaENV;

/**
 * Created by mystoxlol on 2020/6/8, 11:15.
 * company: kongtrolink
 * description:
 * update record:
 */
public interface Schedule {
    public void initCaller(OperaCall caller);//设置回调方法
    public void build(IaENV iaENV);

    void unregister();
}

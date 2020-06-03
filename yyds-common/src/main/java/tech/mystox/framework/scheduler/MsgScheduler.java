package tech.mystox.framework.scheduler;

import tech.mystox.framework.core.IaENV;
import tech.mystox.framework.core.RegCall;
import tech.mystox.framework.entity.RegisterSub;
import tech.mystox.framework.service.MsgHandler;

import java.util.List;

public interface MsgScheduler {
    public void subTopic(List<RegisterSub> subList);
    public void removerSubTopic(List<RegisterSub> subList);
    public void build(IaENV iaENV);
    public void initCaller(RegCall caller);//设置回调方法
    public MsgHandler getIaHandler();
}

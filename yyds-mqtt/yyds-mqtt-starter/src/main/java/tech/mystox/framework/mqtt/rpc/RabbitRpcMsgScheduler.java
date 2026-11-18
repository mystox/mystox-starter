package tech.mystox.framework.mqtt.rpc;

import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.core.IaENV;
import tech.mystox.framework.entity.RegisterSub;
import tech.mystox.framework.scheduler.MsgScheduler;
import tech.mystox.framework.service.MsgHandler;
import java.util.List;

public final class RabbitRpcMsgScheduler implements MsgScheduler {
    private final IaContext context;
    private RabbitRpcHandler handler;
    public RabbitRpcMsgScheduler(IaContext context) { this.context = context; }
    @Override public void build(IaENV environment) { handler = new RabbitRpcHandler(context); }
    @Override public void subTopic(List<RegisterSub> subscriptions) { handler.start(subscriptions); }
    @Override public void removerSubTopic(List<RegisterSub> subscriptions) {
        throw new UnsupportedOperationException("Registryless subscriptions are managed by the process lifecycle");
    }
    @Override public MsgHandler getIaHandler() { return handler; }
    @Override public void unregister() { if (handler != null) handler.close(); }
}
